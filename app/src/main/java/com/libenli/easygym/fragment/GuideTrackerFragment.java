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

import com.libenli.easygym.R;
import com.libenli.easygym.core.BaseFragment;
import com.libenli.easygym.utils.XToastUtils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.banner.anim.select.ZoomInEnter;
import com.xuexiang.xui.widget.banner.transform.DepthTransformer;
import com.xuexiang.xui.widget.banner.widget.banner.SimpleGuideBanner;

import java.util.ArrayList;
import java.util.List;

import static com.libenli.easygym.fragment.GuideTrackerFragment.TYPE;

@Page(anim = CoreAnim.slide, name = "", params = {TYPE})
public class GuideTrackerFragment extends BaseFragment {

    public final static String TYPE = "type";

    private List<Object> list = new ArrayList<>();

    private List<Object> list1 = new ArrayList<>();

    @AutoWired
    int type;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_guide_tracker;
    }

    @Override
    protected TitleBar initTitle() {
        return null;
    }

    @Override
    protected void initArgs() {
        XRouter.getInstance().inject(this);
    }

    @Override
    protected void initViews() {
        list.add(R.drawable.guide_img_1);
        list.add(R.drawable.guide_img_2);
        list.add(R.drawable.guide_img_3);
        list.add(R.drawable.guide_img_4);


        list1.add(R.drawable.guide_img_4);
        list1.add(R.drawable.guide_img_3);
        list1.add(R.drawable.guide_img_2);
        list1.add(R.drawable.guide_img_1);

        sgb();
    }

    private void sgb() {
        SimpleGuideBanner sgb = findViewById(R.id.sgb);

        if (type == 0) {
            sgb.setSource(list);
        } else {
            sgb.setSource(list1);
        }

        sgb
                .setIndicatorWidth(6)
                .setIndicatorHeight(6)
                .setIndicatorGap(12)
                .setIndicatorCornerRadius(3.5f)
                .setSelectAnimClass(ZoomInEnter.class)
                .setTransformerClass(DepthTransformer.class)
                .barPadding(0, 10, 0, 10)
                .startScroll();

        sgb.setOnJumpClickListener(new SimpleGuideBanner.OnJumpClickListener() {
            @Override
            public void onJumpClick() {
                popToBack();
            }
        });
    }
}
