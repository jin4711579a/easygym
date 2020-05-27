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

package com.libenli.easygym.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.libenli.easygym.R;
import com.libenli.easygym.model.MyDevice;
import com.libenli.easygym.service.BluetoothLeService;
import com.libenli.easygym.utils.XToastUtils;
import com.xuexiang.xui.widget.button.roundbutton.RoundButton;
import com.xuexiang.xui.widget.dialog.MiniLoadingDialog;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.progress.loading.MiniLoadingView;

import java.util.List;

public class BleDeviceAdapter extends RecyclerView.Adapter<BleDeviceAdapter.MyHolder> {

    private Context mContext;
    private List<MyDevice> mBluetoothDevices;
    private List<MyDevice> connectingDevices;
    private BluetoothLeService service;

    public BleDeviceAdapter (Context mContext, List<MyDevice> mBluetoothDevices, List<MyDevice> connectingDevices, BluetoothLeService service) {
        this.mContext = mContext;
        this.mBluetoothDevices = mBluetoothDevices;
        this.connectingDevices = connectingDevices;
        this.service = service;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bluetooth_devices_list_item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        MyDevice device = mBluetoothDevices.get(position);
        holder.name.setText(device.getName());
        holder.introduce.setText(device.getAddressData());
        holder.btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service.connect(device.getAddressData());
            }
        });

        holder.btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service.disconnect(device.getAddressData());
            }
        });

        if (connectingDevices.size() > 0) {
            for (MyDevice deviceTemp : connectingDevices) {
                if (deviceTemp.getAddressData().equals(device.getAddressData())) {
                    holder.btnDisconnect.setVisibility(View.VISIBLE);
                    holder.btnConnect.setVisibility(View.GONE);
                    return;
                }
            }
        }
        holder.btnDisconnect.setVisibility(View.GONE);
        holder.btnConnect.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return mBluetoothDevices.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView introduce;
        RoundButton btnConnect;
        RoundButton btnDisconnect;
        MiniLoadingView loadingView;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            introduce = itemView.findViewById(R.id.introduce);
            btnConnect = itemView.findViewById(R.id.btn_connect);
            btnDisconnect = itemView.findViewById(R.id.btn_disconnect);
            loadingView = itemView.findViewById(R.id.loading_view);
        }
    }
}
