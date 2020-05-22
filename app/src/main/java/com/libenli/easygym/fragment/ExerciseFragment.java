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
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.libenli.easygym.R;
import com.libenli.easygym.activity.BluetoothLeService;
import com.libenli.easygym.activity.MainActivity;
import com.libenli.easygym.core.BaseFragment;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;

import butterknife.BindView;

@Page(anim = CoreAnim.slide, name = "开始锻炼")
public class ExerciseFragment extends BaseFragment {

    @BindView(R.id.value)
    TextView tvValue;

    private MainActivity mainActivity;
    private BluetoothLeService service;

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.e("blemain", "接收到数据！！！！");
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_exercise;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.unregisterReceiver(mGattUpdateReceiver);
                popToBack();
            }
        });
        return titleBar;
    }

    @Override
    protected void initViews() {
        mainActivity = (MainActivity)mActivity;
        service = mainActivity.mBluetoothLeService;

        mainActivity.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private void displayData(String data) {
        if (data != null) {
            tvValue.append(data);
            tvValue.append("\n");
        }
    }
}
