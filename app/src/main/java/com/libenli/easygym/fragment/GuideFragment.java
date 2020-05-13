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

import android.os.Bundle;
import android.view.View;

import com.libenli.easygym.R;
import com.libenli.easygym.core.BaseFragment;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.button.roundbutton.RoundButton;
import com.xuexiang.xui.widget.imageview.RadiusImageView;

import butterknife.BindView;

import static com.libenli.easygym.fragment.GuideTrackerFragment.TYPE;

@Page(anim = CoreAnim.slide, name = "开始引导")
public class GuideFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.btn_tracker)
    RadiusImageView iv_tracker;

    @BindView(R.id.btn_equip)
    RadiusImageView iv_equip;

    @BindView(R.id.btn_close)
    RoundButton btn_close;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_guide;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initListeners() {
        btn_close.setOnClickListener(this);
        iv_tracker.setOnClickListener(this);
        iv_equip.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                openPage(MainFragment.class);
                break;

            case R.id.btn_tracker:
                Bundle param = new Bundle();
                param.putInt(TYPE, 1);
                openPage(GuideTrackerFragment.class, param);
                break;

            case R.id.btn_equip:
                Bundle param1 = new Bundle();
                param1.putInt(TYPE, 0);
                openPage(GuideTrackerFragment.class, param1);
                break;
        }
    }
}
