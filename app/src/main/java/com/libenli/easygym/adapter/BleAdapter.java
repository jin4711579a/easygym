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

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.libenli.easygym.R;
import com.libenli.easygym.activity.BluetoothLeService;
import com.libenli.easygym.model.MyDevice;
import com.libenli.easygym.utils.XToastUtils;
import com.xuexiang.xui.widget.button.roundbutton.RoundButton;

import java.util.List;

public class BleAdapter extends BaseAdapter {

    private Context mContext;
    private List<MyDevice> mBluetoothDevices;
    private List<MyDevice> connectingDevices;
    private BluetoothLeService service;

    public BleAdapter(Context mContext, List<MyDevice> mBluetoothDevices, List<MyDevice> connectingDevices, BluetoothLeService service) {
        this.mContext = mContext;
        this.mBluetoothDevices = mBluetoothDevices;
        this.connectingDevices = connectingDevices;
        this.service = service;
    }

    public void clear() {
        mBluetoothDevices.clear();
    }

    @Override
    public int getCount() {
        return mBluetoothDevices.size();
    }

    @Override
    public Object getItem(int position) {
        return mBluetoothDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.bluetooth_devices_list_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MyDevice device = (MyDevice) getItem(position);
        viewHolder.name.setText(device.getName());
        viewHolder.introduce.setText(device.getAddressData());

        viewHolder.btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service.connect(device.getAddressData());
            }
        });

        viewHolder.btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service.disconnect();
            }
        });

        if (connectingDevices.size() > 0) {
            for (MyDevice deviceTemp : connectingDevices) {
                if (deviceTemp.getAddressData().equals(device.getAddressData())) {
                    viewHolder.btnDisconnect.setVisibility(View.VISIBLE);
                    viewHolder.btnConnect.setVisibility(View.GONE);
                    return convertView;
                }
            }
        }

        viewHolder.btnDisconnect.setVisibility(View.GONE);
        viewHolder.btnConnect.setVisibility(View.VISIBLE);

        return convertView;
    }

    class ViewHolder {
        public TextView name;
        public TextView introduce;
        public RoundButton btnConnect;
        public RoundButton btnDisconnect;

        public ViewHolder(View view) {
            name = view.findViewById(R.id.name);
            introduce = view.findViewById(R.id.introduce);
            btnConnect = view.findViewById(R.id.btn_connect);
            btnDisconnect = view.findViewById(R.id.btn_disconnect);
        }
    }
}
