/*
 * Copyright (C) 2019 xuexiangjys(xuexiangjys@163.com)
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

import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;
import com.libenli.easygym.R;
import com.libenli.easygym.activity.MainActivity;
import com.libenli.easygym.core.BaseFragment;
import com.libenli.easygym.db.DBHelper;
import com.libenli.easygym.db.DBManager;
import com.libenli.easygym.model.Info;
import com.libenli.easygym.model.Schedule;
import com.libenli.easygym.utils.SettingSPUtils;
import com.libenli.easygym.utils.XToastUtils;
import com.squareup.haha.perflib.Main;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.button.roundbutton.RoundButton;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.progress.CircleProgressView;
import com.xuexiang.xutil.XUtil;
import com.xuexiang.xutil.common.ClickUtils;
import com.xuexiang.xutil.tip.ToastUtils;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

/**
 * @author xuexiang
 * @since 2018/11/7 下午1:16
 */
@Page(anim = CoreAnim.slide)
public class MainFragment extends BaseFragment implements ClickUtils.OnClick2ExitListener, SwipeRefreshLayout.OnRefreshListener, CircleProgressView.CircleProgressUpdateListener {

    @BindView(R.id.progressView_circle_main)
    CircleProgressView progressViewCircleMain;

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.nav_view)
    NavigationView navView;

    @BindView(R.id.tv_target)
    TextView tvTarget;

    @BindView(R.id.progress_text_main)
    TextView progress_text_main;

    @BindView(R.id.iv_show_slide)
    AppCompatImageView ivSlide;

    @BindView(R.id.iv_schedule)
    AppCompatImageView ivSchedule;

    @BindView(R.id.btn_begin)
    RoundButton btnBegin;

    @BindView(R.id.tv_time)
    TextView tv_time;

    @BindView(R.id.tv_calories)
    TextView tv_calories;

    private View headerLayout;

    private Info info;

    private int target = Integer.parseInt(SettingSPUtils.getInstance().getTarget());

    private float todayKG = 0;

    @Override
    protected TitleBar initTitle() {
        return null;
    }

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ClickUtils.exitBy2Click(2000, this);
        }
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initViews() {
        // 初始化数据库
        DBManager.getInstance().init(mActivity.getApplicationContext());

        List<Info> infoList = DBManager.getInstance().queryInfo();
        if (infoList.size() > 0) {
            info = infoList.get(0);
        }

        if (((MainActivity)mActivity).mConnected) {
            btnBegin.setEnabled(true);
        } else {
            btnBegin.setEnabled(false);
        }

        headerLayout = navView.inflateHeaderView(R.layout.layout_nav_header);

        TextView tvName = headerLayout.findViewById(R.id.info_name);
        tvName.setText(info.getName());

        RadiusImageView ivAvatar = headerLayout.findViewById(R.id.img_icon);
        if (!info.getAvatar().equals("")) {
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.color.color_f4)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(this)
                    .load(info.getAvatar())
                    .apply(options)
                    .into(ivAvatar);
        }

        tvTarget.setText(SettingSPUtils.getInstance().getTarget() + " kg");

        // 设置进度条，后期按实际情况来显示
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        List<Schedule> schedules = DBManager.getInstance().queryScheduleByDate(simpleDateFormat.format(date));

        if (schedules.size() > 0) {
            todayKG = schedules.get(0).getWeight();
            tv_time.setText(String.valueOf(schedules.get(0).getTime()));
            tv_calories.setText(String.valueOf(schedules.get(0).getCalories()));

            XToastUtils.toast(String.valueOf(schedules.get(0).getComplete()));
        }

        progressViewCircleMain.setProgressViewUpdateListener(this);
        progressViewCircleMain.setEndProgress(todayKG < target ? todayKG / target * 100 : 100);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressViewCircleMain.startProgressAnimation();
            }
        }, 1000);
    }

    @Override
    protected void initListeners() {
        swipeRefreshLayout.setOnRefreshListener(this);

        RadiusImageView imageView = headerLayout.findViewById(R.id.img_icon);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPage(InfoFragment.class);
            }
        });

        navView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_equipment:
                    openPage(DevicesFragment.class);
                    break;

                case R.id.nav_target:
                    showTargetDialog();
                    break;

                case R.id.nav_guide:
                    openNewPage(GuideFragment.class);
                    break;

                case R.id.nav_opinion:
                    XToastUtils.toast("如有任何反馈，请到官网的意见反馈区给予反馈，谢谢！");
                    break;
            }
            return true;
        });

        ivSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPage(ScheduleFragment.class);
            }
        });

        ivSlide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBManager.getInstance().addSchedule(new Schedule("2020-05-28", 100, 20, 120, 100));
                XToastUtils.toast("添加成功！！！");
            }
        });

        btnBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPage(ExerciseFragment.class);
            }
        });
    }

    private void showTargetDialog() {
        new MaterialDialog.Builder(getContext())
                .title("我的目标")
                .content("设置今天的目标重量（kg）")
                .input(
                        getString(R.string.hint_please_input_target),
                        SettingSPUtils.getInstance().getTarget(),
                        false,
                        (new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                            }
                        }))
                .inputType(
                        InputType.TYPE_CLASS_NUMBER)
                .inputRange(1, 5)
                .positiveText("确定")
                .negativeText("取消")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        SettingSPUtils.getInstance().setTarget(dialog.getInputEditText().getText().toString());
                        XToastUtils.toast(dialog.getInputEditText().getText().toString());
                    }
                })
                .cancelable(false)
                .show();
    }

    private void loadData() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        List<Schedule> schedules = DBManager.getInstance().queryScheduleByDate(simpleDateFormat.format(date));

        if (schedules.size() > 0) {
            todayKG = schedules.get(0).getWeight();
            tv_time.setText(String.valueOf(schedules.get(0).getTime()));
            tv_calories.setText(String.valueOf(schedules.get(0).getCalories()));
        }

        progressViewCircleMain.setEndProgress(todayKG < target ? todayKG / target * 100 : 100);
    }

    private void refreshData() {
        loadData();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressViewCircleMain.startProgressAnimation();
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }

    @Override
    public void onRetry() {
        XToastUtils.toast("再按一次退出程序");
    }

    @Override
    public void onExit() {
        XUtil.get().exitApp();
    }

    @Override
    public void onRefresh() {
        refreshData();
    }

    @Override
    public void onCircleProgressStart(View view) {

    }

    @Override
    public void onCircleProgressUpdate(View view, float progress) {
        if (target >= todayKG) {
            progress_text_main.setText(String.valueOf(Math.floor(progress / 100 * target)));
        } else {
            progress_text_main.setText(String.valueOf(Math.floor(progress / 100 * todayKG)));
        }
    }

    @Override
    public void onCircleProgressFinished(View view) {

    }
}
