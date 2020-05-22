/*
 * Copyright (C) 2020 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.libenli.easygym.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.libenli.easygym.R;
import com.libenli.easygym.activity.BluetoothLeService;
import com.libenli.easygym.activity.MainActivity;
import com.libenli.easygym.adapter.BleAddAdapter;
import com.libenli.easygym.core.BaseFragment;
import com.libenli.easygym.model.MyDevice;
import com.libenli.easygym.utils.SettingSPUtils;
import com.libenli.easygym.utils.XToastUtils;
import com.luck.picture.lib.permissions.RxPermissions;
import com.squareup.haha.perflib.Main;
import com.umeng.commonsdk.debug.I;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xui.widget.popupwindow.status.Status;
import com.xuexiang.xui.widget.popupwindow.status.StatusView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

@Page(anim = CoreAnim.slide, name = "添加设备")
public class DevicesAddFragment extends BaseFragment {

    private static final String TAG ="ble_tag" ;

    @BindView(R.id.device_list)
    ListView bleListView;

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.left_icon)
    ImageView leftIcon;

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.tv_right)
    TextView tvRight;

    @BindView(R.id.status)
    StatusView status;

    private List<BluetoothDevice> mDatas;
    private List<MyDevice> connectedDevices;
    private BleAddAdapter mAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    private BluetoothDevice currentDevice;

    private MainActivity mainActivity;
    private BluetoothLeService mBluetoothLeService;

    private boolean isScaning=false;
    private boolean isConnecting=false;

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                status.setStatus(Status.COMPLETE);
                mainActivity.mConnected = true;
                isConnecting = false;
                if (currentDevice != null) {
                    mainActivity.connectingDevices.add(new MyDevice(currentDevice.getName(), currentDevice.getAddress()));
                    if (connectedDevices.size() > 0) {
                        for (MyDevice device :
                                connectedDevices) {
                            if (!device.getAddressData().equals(currentDevice.getAddress())) {
                                connectedDevices.add(new MyDevice(currentDevice.getName(), currentDevice.getAddress()));
                                mAdapter.notifyDataSetChanged();
                                Gson gson = new Gson();
                                String deviceJson = gson.toJson(connectedDevices);
                                SettingSPUtils.getInstance().setDevices(deviceJson);
                            }
                        }
                    } else {
                        connectedDevices.add(new MyDevice(currentDevice.getName(), currentDevice.getAddress()));
                        mAdapter.notifyDataSetChanged();
                        Gson gson = new Gson();
                        String deviceJson = gson.toJson(connectedDevices);
                        SettingSPUtils.getInstance().setDevices(deviceJson);
                    }
                }
            }
        }
    };


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_devices_add;
    }

    @Override
    protected TitleBar initTitle() {
        return null;
    }

    @Override
    protected void initViews() {
        init();

        mBluetoothManager = (BluetoothManager) mActivity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter =mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, 0);
        }

        autoRefresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivity.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    private void autoRefresh() {
        refreshLayout.setRefreshing(true);
        checkPermissions();
    }

    private void refreshData() {
        mAdapter.clear();
        mDatas.clear();
        mAdapter.notifyDataSetChanged();
        checkPermissions();
    }

    private void init() {
        tvTitle.setText("添加设备");
        tvRight.setVisibility(View.GONE);

        mainActivity = (MainActivity)mActivity;
        mBluetoothLeService = mainActivity.mBluetoothLeService;

        mDatas = new ArrayList<>();
        String deviceList = SettingSPUtils.getInstance().getDevices();
        if (deviceList.equals("")) {
            connectedDevices = new ArrayList<>();
        } else {
            Gson gson = new Gson();
            connectedDevices = gson.fromJson(deviceList, new TypeToken<List<MyDevice>>(){}.getType());
        }

        mAdapter = new BleAddAdapter(mActivity.getApplicationContext(), mDatas, connectedDevices);
        bleListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void initListeners() {
        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.unregisterReceiver(mGattUpdateReceiver);
                openPage(DevicesFragment.class);
            }
        });

        bleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DialogLoader.getInstance().showConfirmDialog(
                        getContext(),
                        "添加这个设备？",
                        getString(R.string.lab_yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (isScaning) {
                                    stopScanDevice();
                                    refreshLayout.setRefreshing(false);
                                }
                                if (!isConnecting) {
                                    isConnecting = true;
                                    currentDevice = mDatas.get(position);
                                    status.setMinimumHeight(48);
                                    status.setStatus(Status.LOADING);
                                    if (!mBluetoothLeService.connect(currentDevice.getAddress())) {
                                        XToastUtils.toast("连接失败！！！！！");
                                    }
                                }
                                dialog.dismiss();
                            }
                        },
                        getString(R.string.lab_no),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }
                );
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
    }

    /**
     * 检查权限
     */
    private void checkPermissions() {
        RxPermissions rxPermissions = new RxPermissions(mActivity);
        rxPermissions.request(android.Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(new io.reactivex.functions.Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            // 用户已经同意该权限
                            scanDevice();
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            XToastUtils.toast("用户开启权限后才能使用");
                        }
                    }
                });
    }

    /**
     * 开始扫描 10秒后自动停止
     * */
    private void scanDevice(){
        isScaning=true;
        mBluetoothAdapter.startLeScan(scanCallback);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //结束扫描
                stopScanDevice();
                refreshLayout.setRefreshing(false);
            }
        },10000);
    }

    /**
     * 停止扫描
     * */
    private void stopScanDevice() {
        isScaning=false;
        mBluetoothAdapter.stopLeScan(scanCallback);
    }

    private BluetoothAdapter.LeScanCallback scanCallback=new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (device.getName() != null)
            {
                if (!mDatas.contains(device) && device.getName() != null){
                    mDatas.add(device);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        return intentFilter;
    }
}
