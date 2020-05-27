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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.libenli.easygym.R;
import com.libenli.easygym.adapter.BleAdapter;
import com.libenli.easygym.adapter.BleDeviceAdapter;
import com.libenli.easygym.service.BluetoothLeService;
import com.libenli.easygym.activity.MainActivity;
import com.libenli.easygym.core.BaseFragment;
import com.libenli.easygym.model.MyDevice;
import com.libenli.easygym.utils.SettingSPUtils;
import com.libenli.easygym.utils.XToastUtils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.popupwindow.status.Status;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

@Page(anim = CoreAnim.slide, name = "我的设备")
public class DevicesFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    SwipeRecyclerView deviceList;

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;

    private List<MyDevice> connectedDevices = new ArrayList<>();
    private BleDeviceAdapter adapter;
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
        TitleBar titleBar = super.initTitle();
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.unregisterReceiver(mGattUpdateReceiver);
                openPage(MainFragment.class);
            }
        });

        titleBar.addAction(new TitleBar.TextAction("添加") {
            @Override
            public void performAction(View view) {
                mainActivity.unregisterReceiver(mGattUpdateReceiver);
                openPage(DevicesAddFragment.class);
            }
        });

        return titleBar;
    }

    @Override
    protected void initViews() {
        mainActivity = (MainActivity)mActivity;
        connectingDevices = mainActivity.connectingDevices;
        bluetoothLeService = mainActivity.mBluetoothLeService;
        mainActivity.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        String devices = SettingSPUtils.getInstance().getDevices();
        if (devices.equals("")) {
            connectedDevices = new ArrayList<>();
        } else {
            Gson gson = new Gson();
            connectedDevices = gson.fromJson(devices, new TypeToken<List<MyDevice>>(){}.getType());
        }

        WidgetUtils.initRecyclerView(deviceList);

        deviceList.setSwipeMenuCreator(swipeMenuCreator);
        deviceList.setOnItemMenuClickListener(mMenuItemClickListener);

        adapter = new BleDeviceAdapter(mActivity.getApplicationContext(), connectedDevices, connectingDevices, bluetoothLeService);
        deviceList.setAdapter(adapter);

        refreshLayout.setColorSchemeColors(0xff0099cc, 0xffff4444, 0xff669900, 0xffaa66cc, 0xffff8800);
    }

    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {
            int width = getResources().getDimensionPixelSize(R.dimen.dp_70);
            int height = getResources().getDimensionPixelSize(R.dimen.dp_70);

            SwipeMenuItem deleteItem = new SwipeMenuItem(getContext()).setBackground(R.drawable.menu_selector_red)
                    .setText("删除")
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
            rightMenu.addMenuItem(deleteItem);

            SwipeMenuItem editItem = new SwipeMenuItem(getContext()).setBackground(R.drawable.menu_selector_green)
                    .setText("编辑")
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
            rightMenu.addMenuItem(editItem);
        }
    };

    private OnItemMenuClickListener mMenuItemClickListener = new OnItemMenuClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge, int adapterPosition) {
            menuBridge.closeMenu();

            int direction = menuBridge.getDirection();
            int menuPosition = menuBridge.getPosition();

            if (direction == SwipeRecyclerView.RIGHT_DIRECTION) {
                if (menuPosition == 0) {
                    DialogLoader.getInstance().showConfirmDialog(
                            getContext(),
                            "确定要删除这个设备吗？",
                            getString(R.string.lab_yes),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    connectedDevices.remove(adapterPosition);
                                    adapter.notifyDataSetChanged();
                                    Gson gson = new Gson();
                                    String deviceJson = gson.toJson(connectedDevices);
                                    SettingSPUtils.getInstance().setDevices(deviceJson);
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
                } else if (menuPosition == 1) {
                    new MaterialDialog.Builder(getContext())
                            .title("设备名称")
                            .content("请输入新的设备名称")
                            .input(
                                    getString(R.string.hint_please_input_deviceName),
                                    "",
                                    false,
                                    (new MaterialDialog.InputCallback() {
                                        @Override
                                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                                        }
                                    }))
                            .inputRange(2, 10)
                            .positiveText("确定")
                            .negativeText("取消")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    connectedDevices.get(adapterPosition).setName(dialog.getInputEditText().getText().toString());
                                    Gson gson = new Gson();
                                    String deviceJson = gson.toJson(connectedDevices);
                                    SettingSPUtils.getInstance().setDevices(deviceJson);
                                    adapter.notifyDataSetChanged();
                                }
                            })
                            .cancelable(false)
                            .show();
                }
            }
        }
    };

    @Override
    protected void initListeners() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                deviceList.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        loadData();
                    }
                }, 1000);
            }
        });
    }

    private void loadData() {
        String devices = SettingSPUtils.getInstance().getDevices();
        if (devices.equals("")) {
            connectedDevices = new ArrayList<>();
        } else {
            Gson gson = new Gson();
            connectedDevices = gson.fromJson(devices, new TypeToken<List<MyDevice>>(){}.getType());
        }

        adapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        return intentFilter;
    }
}
