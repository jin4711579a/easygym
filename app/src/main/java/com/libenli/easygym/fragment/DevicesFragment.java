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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.libenli.easygym.R;
import com.libenli.easygym.activity.BluetoothLeService;
import com.libenli.easygym.activity.MainActivity;
import com.libenli.easygym.adapter.BleAdapter;
import com.libenli.easygym.core.BaseFragment;
import com.libenli.easygym.model.MyDevice;
import com.libenli.easygym.utils.SettingSPUtils;
import com.libenli.easygym.utils.XToastUtils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.popupwindow.status.Status;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

@Page(anim = CoreAnim.slide, name = "我的设备")
public class DevicesFragment extends BaseFragment {

    @BindView(R.id.device_list)
    ListView deviceList;

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.left_icon)
    ImageView leftIcon;

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.tv_right)
    TextView tvRight;

    private List<MyDevice> connectedDevices;
    private BleAdapter adapter;
    private MainActivity mainActivity;
    private List<MyDevice> connectingDevices;
    private BluetoothLeService bluetoothLeService;

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                String address = intent.getStringExtra(BluetoothLeService.EXTRA_ADDRESS);
                MyDevice currentDevice = null;
                if (address != null && !address.equals("")) {
                    for (MyDevice device : connectedDevices) {
                        if (device.getAddressData().equals(address)) {
                            currentDevice = device;
                        }
                    }
                }
                if (currentDevice != null) {
                    mainActivity.mConnected = true;
                    mainActivity.connectingDevices.add(new MyDevice(currentDevice.getName(), currentDevice.getAddressData()));
                    connectingDevices = mainActivity.connectingDevices;
                    XToastUtils.toast("连接成功！！！" + String.valueOf(connectingDevices.size()));
                    adapter.notifyDataSetChanged();
                }
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                String address = intent.getStringExtra(BluetoothLeService.EXTRA_ADDRESS);

                for (MyDevice device : connectingDevices) {
                    if (device.getAddressData().equals(address)) {
                        mainActivity.connectingDevices.remove(device);
                        connectingDevices = mainActivity.connectingDevices;
                        adapter.notifyDataSetChanged();
                        XToastUtils.toast("断开连接成功！！！" + String.valueOf(connectingDevices.size()));
                    }
                }

                mainActivity.mConnected = false;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_devices;
    }

    @Override
    protected TitleBar initTitle() {
        return null;
    }

    @Override
    protected void initViews() {
        tvTitle.setText("我的设备");
        tvRight.setText("添加");

        mainActivity = (MainActivity)mActivity;
        connectingDevices = mainActivity.connectingDevices;
        bluetoothLeService = mainActivity.mBluetoothLeService;

        String devices = SettingSPUtils.getInstance().getDevices();
        if (devices.equals("")) {
            connectedDevices = new ArrayList<>();
        } else {
            Gson gson = new Gson();
            connectedDevices = gson.fromJson(devices, new TypeToken<List<MyDevice>>(){}.getType());
        }

        adapter = new BleAdapter(mActivity.getApplicationContext(), connectedDevices, connectingDevices, bluetoothLeService);
        deviceList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        mainActivity.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        autoRefresh();
    }

    @Override
    protected void initListeners() {
        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.unregisterReceiver(mGattUpdateReceiver);
                openPage(MainFragment.class);
            }
        });

        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPage(DevicesAddFragment.class);
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
    }

    private void autoRefresh() {
        refreshLayout.setRefreshing(true);
        refreshData();
    }

    private void refreshData() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
            }
        }, 1000);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        return intentFilter;
    }
}
