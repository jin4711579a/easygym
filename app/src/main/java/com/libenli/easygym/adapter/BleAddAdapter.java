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
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.libenli.easygym.R;
import com.libenli.easygym.model.MyDevice;

import java.util.List;

public class BleAddAdapter extends BaseAdapter {

    private Context mContext;
    private List<BluetoothDevice> mBluetoothDevices;
    private List<MyDevice> connectedDevices;

    public BleAddAdapter(Context mContext, List<BluetoothDevice> mBluetoothDevices, List<MyDevice> connectedDevices) {
        this.mContext = mContext;
        this.mBluetoothDevices = mBluetoothDevices;
        this.connectedDevices = connectedDevices;
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
    public boolean isEnabled(int position) {
        if (connectedDevices.size() > 0) {

            BluetoothDevice device = mBluetoothDevices.get(position);

            for (MyDevice myDevice:connectedDevices) {
                if (myDevice.getAddressData().equals(device.getAddress())) {
                    return false;
                }
            }

            return true;
        }
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.bluetooth_devices_add_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        BluetoothDevice device = (BluetoothDevice) getItem(position);
        viewHolder.name.setText(device.getName());
        viewHolder.introduce.setText(device.getAddress());
        if (connectedDevices.size() > 0) {
            for (MyDevice myDevice:connectedDevices) {
                if (myDevice.getAddressData().equals(device.getAddress())) {
                    viewHolder.name.setTextColor(Color.LTGRAY);
                    viewHolder.introduce.setTextColor(Color.LTGRAY);
                    viewHolder.state.setTextColor(Color.LTGRAY);
                    viewHolder.state.setText("已添加");
                }
            }
        }
        else {
            viewHolder.state.setText("未连接");
        }
        return convertView;
    }

    class ViewHolder {
        public TextView name;
        public TextView introduce;
        public TextView state;

        public ViewHolder(View view) {
            name = view.findViewById(R.id.name);
            introduce = view.findViewById(R.id.introduce);
            state = view.findViewById(R.id.state);
        }
    }
}
