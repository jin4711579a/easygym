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

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codbking.calendar.CaledarAdapter;
import com.codbking.calendar.CalendarDate;
import com.codbking.calendar.CalendarDateView;
import com.codbking.calendar.CalendarView;
import com.libenli.easygym.R;
import com.libenli.easygym.core.BaseFragment;
import com.libenli.easygym.utils.XToastUtils;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.utils.DensityUtils;
import com.xuexiang.xui.utils.ThemeUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;

import java.util.Date;

import butterknife.BindView;

@Page(anim = CoreAnim.slide, name = "月状态")
public class ScheduleFragment extends BaseFragment {

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.calendar)
    CalendarDateView calendar;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_schedule;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();

        titleBar.addAction(new TitleBar.TextAction("周状态") {
            @Override
            public void performAction(View view) {
                openPage(WeekStateFragment.class);
            }
        });

        return titleBar;
    }

    @Override
    protected void initViews() {

        calendar.setAdapter(new CaledarAdapter() {
            @Override
            public View getView(View convertView, ViewGroup parentView, CalendarDate calendarDate) {
                TextView textView;
                if (convertView == null) {
                    convertView = LayoutInflater.from(parentView.getContext()).inflate(R.layout.adapter_calendar_item, null);
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(DensityUtils.dp2px(48), DensityUtils.dp2px(48));
                    convertView.setLayoutParams(params);
                }

                textView = convertView.findViewById(R.id.tv_text);
                textView.setBackgroundResource(R.drawable.bg_calendar_material_design_item);

                textView.setText(String.valueOf(calendarDate.day));

                if (calendarDate.monthFlag != 0) {
                    convertView.setVisibility(View.INVISIBLE);
                } else {
                    if (calendarDate.isToday() && calendarDate.equals(calendar.getSelectCalendarDate())) {
                        textView.setTextColor(ThemeUtils.resolveColor(getContext(), R.attr.colorAccent));
                    } else {
                        textView.setTextColor(ThemeUtils.resolveColor(getContext(), R.attr.xui_config_color_content_text));
                    }
                }
                return convertView;
            }
        });

        calendar.setOnCalendarSelectedListener(new CalendarView.OnCalendarSelectedListener() {
            @Override
            public void onCalendarSelected(View view, int postion, CalendarDate date) {
                XToastUtils.toast("选中：" + date.formatDate());
            }
        });

        calendar.setOnTodaySelectStatusChangedListener(new CalendarView.OnTodaySelectStatusChangedListener() {
            @Override
            public void onStatusChanged(View todayView, boolean isSelected) {
                TextView view = todayView.findViewById(R.id.tv_text);
                if (isSelected) {
                    view.setTextColor(ThemeUtils.resolveColor(getContext(), R.attr.xui_config_color_content_text));
                } else {
                    view.setTextColor(ThemeUtils.resolveColor(getContext(), R.attr.colorAccent));
                }
            }
        });

        calendar.setOnMonthChangedListener(new CalendarDateView.OnMonthChangedListener() {
            @Override
            public void onMonthChanged(View view, int postion, CalendarDate date) {
                tvTitle.setText(String.format("%d年%d月", date.year, date.month));
            }
        });

        CalendarDate date = CalendarDate.get(new Date());
        tvTitle.setText(String.format("%d年%d月", date.year, date.month));
    }
}
