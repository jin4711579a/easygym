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
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.libenli.easygym.R;
import com.libenli.easygym.chart.BaseChartFragment;
import com.libenli.easygym.utils.XToastUtils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

@Page(anim = CoreAnim.slide, name = "周状态")
public class WeekStateFragment extends BaseChartFragment implements OnChartValueSelectedListener {

    @BindView(R.id.line_chart)
    LineChart chart;

    @BindView(R.id.tv_1)
    TextView tv1;

    @BindView(R.id.tv_2)
    TextView tv2;

    @BindView(R.id.tv_3)
    TextView tv3;

    @BindView(R.id.tv_4)
    TextView tv4;

    @BindView(R.id.tv_5)
    TextView tv5;

    @BindView(R.id.tv_6)
    TextView tv6;

    @BindView(R.id.tv_7)
    TextView tv7;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_week;
    }

    @Override
    protected void initViews() {
        initChartStyle();
        initChartLabel();
        setChartData(7, 180);

        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day - 1) {
            case 1:
                tv1.setText("今日");
                break;
            case 2:
                tv2.setText("今日");
                break;
            case 3:
                tv3.setText("今日");
                break;
            case 4:
                tv4.setText("今日");
                break;
            case 5:
                tv5.setText("今日");
                break;
            case 6:
                tv6.setText("今日");
                break;
            case 7:
                tv7.setText("今日");
                break;
        }

        chart.animateXY(1500, 1500);
        chart.setOnChartValueSelectedListener(this);
    }

    @Override
    protected void initChartStyle() {
        chart.setBackgroundColor(Color.WHITE);
        //关闭描述
        chart.getDescription().setEnabled(false);
        //设置不画背景网格
        chart.setDrawGridBackground(false);

        YAxis yAxis = chart.getAxisLeft();
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setEnabled(false);
        chart.getXAxis().setEnabled(false);
    }

    @Override
    protected void initChartLabel() {
        Legend legend = chart.getLegend();
        legend.setEnabled(false);
    }

    @Override
    protected void setChartData(int count, float range) {
        List<Entry> values = new ArrayList<>();
        //设置数据源
        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * range);
            values.add(new Entry(i, val));
        }
        LineDataSet set1;

        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "DataSet 1");
            set1.setDrawIcons(false);
            //设置点的样式
            set1.setCircleColor(Color.BLACK);
            set1.setCircleRadius(3f);
            // 设置不画空心圆
            set1.setDrawCircleHole(true);

            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);

            // 设置折线图的填充区域
            set1.setDrawFilled(true);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    if (chart == null) return 0;
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });

            set1.setFillColor(Color.GREEN);

            List<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            LineData data = new LineData(dataSets);
            chart.setData(data);
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        XToastUtils.toast("选中了,  x轴值:" + e.getX() + ", y轴值:" + e.getY());
    }

    @Override
    public void onNothingSelected() {

    }

    private List<String> getWeekDate() {
        List<String> weekDate = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int firstDayOfWeek = calendar.getFirstDayOfWeek();
        for (int i = 0; i < 7; i ++) {
            calendar.set(Calendar.DAY_OF_WEEK, firstDayOfWeek + i);
            weekDate.add(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
        }
        return weekDate;
    }
}
