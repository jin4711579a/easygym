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

import android.app.Activity;
import android.content.Intent;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.libenli.easygym.R;
import com.libenli.easygym.core.BaseFragment;
import com.libenli.easygym.db.DBManager;
import com.libenli.easygym.model.Info;
import com.libenli.easygym.utils.Utils;
import com.libenli.easygym.utils.XToastUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.utils.DensityUtils;
import com.xuexiang.xui.widget.button.roundbutton.RoundButton;
import com.xuexiang.xui.widget.dialog.bottomsheet.BottomSheet;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.grouplist.XUICommonListItemView;
import com.xuexiang.xui.widget.grouplist.XUIGroupListView;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.picker.widget.TimePickerView;
import com.xuexiang.xui.widget.picker.widget.builder.TimePickerBuilder;
import com.xuexiang.xui.widget.picker.widget.listener.OnTimeSelectListener;
import com.xuexiang.xutil.data.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@Page(anim = CoreAnim.slide, name = "个人资料")
public class InfoFragment extends BaseFragment {

    @BindView(R.id.groupListView)
    XUIGroupListView mGroupListView;

    @BindView(R.id.img_icon)
    RadiusImageView iv;

    @BindView(R.id.btn_save)
    RoundButton btnSave;

    private List<LocalMedia> mSelectList = new ArrayList<>();

    private Info info = new Info();

    private XUICommonListItemView itemName;
    private XUICommonListItemView itemSex;
    private XUICommonListItemView itemBirth;
    private XUICommonListItemView itemHeight;
    private XUICommonListItemView itemWeight;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_info;
    }

    @Override
    protected void initViews() {
        initGroupListView();

        List<Info> infoList = DBManager.getInstance().queryInfo();
        if (infoList.size() > 0) {
            info = infoList.get(0);
        }

        itemName.setDetailText(info.getName());
        itemSex.setDetailText(info.getSex());
        itemBirth.setDetailText(info.getBirth());
        itemHeight.setDetailText(String.valueOf(info.getHeight()));
        itemWeight.setDetailText(String.valueOf(info.getWeight()));

        if (!info.getAvatar().equals("")) {
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.color.color_f4)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(this)
                    .load(info.getAvatar())
                    .apply(options)
                    .into(iv);
        }
    }

    @Override
    protected void initListeners() {
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.getPictureSelector(InfoFragment.this)
                        .selectionMedia(mSelectList)
                        .maxSelectNum(1)
                        .selectionMode(PictureConfig.SINGLE)
                        .forResult(PictureConfig.CHOOSE_REQUEST);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = itemName.getDetailText().toString();
                String sex = itemSex.getDetailText().toString();
                String birth = itemBirth.getDetailText().toString();
                int height = Integer.parseInt(itemHeight.getDetailText().toString());
                float weight = Float.parseFloat(itemWeight.getDetailText().toString());
                Info info = new Info(name, sex, birth, height, weight);
                if (DBManager.getInstance().editInfo(info)) {
                    openPage(MainFragment.class);
                } else {
                    XToastUtils.toast("保存失败！！！！！！");
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    mSelectList = PictureSelector.obtainMultipleResult(data);
                    LocalMedia media = mSelectList.get(0);
                    String path = media.getPath();

                    if(DBManager.getInstance().editInfoAvatar(path)) {
                        RequestOptions options = new RequestOptions()
                                .centerCrop()
                                .placeholder(R.color.color_f4)
                                .diskCacheStrategy(DiskCacheStrategy.ALL);
                        Glide.with(this)
                                .load(path)
                                .apply(options)
                                .into(iv);
                    } else {
                        XToastUtils.toast("保存失败！！！！！！");
                    }
            }
        }
    }

    private void initGroupListView() {
        itemName = mGroupListView.createItemView("姓名");
        itemName.setAccessoryType(XUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemName.setDetailText("unKnow");

        itemSex = mGroupListView.createItemView("性别");
        itemSex.setAccessoryType(XUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemSex.setDetailText("男");

        itemBirth = mGroupListView.createItemView("出生日期");
        itemBirth.setAccessoryType(XUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemBirth.setDetailText("1970-01-01");

        itemHeight = mGroupListView.createItemView("身高");
        itemHeight.setAccessoryType(XUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemHeight.setDetailText("170");

        itemWeight = mGroupListView.createItemView("体重");
        itemWeight.setAccessoryType(XUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemWeight.setDetailText("50");

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof XUICommonListItemView) {
                    String text = (String) ((XUICommonListItemView) v).getText();
                    switch (text) {
                        case "姓名":
                            new MaterialDialog.Builder(getContext())
                                    .title("姓名")
                                    .content("请输入姓名")
                                    .input(
                                            getString(R.string.hint_please_input_name),
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
                                            itemName.setDetailText(dialog.getInputEditText().getText().toString());
                                        }
                                    })
                                    .cancelable(false)
                                    .show();
                            break;

                        case "性别":
                            new MaterialDialog.Builder(getContext())
                                    .title("性别")
                                    .items(R.array.sex_values)
                                    .itemsCallbackSingleChoice(
                                            0,
                                            new MaterialDialog.ListCallbackSingleChoice() {
                                                @Override
                                                public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                                    itemSex.setDetailText(text);
                                                    return true;
                                                }
                                            })
                                    .positiveText("确定")
                                    .negativeText("取消")
                                    .show();
                            break;

                        case "出生日期":
                            TimePickerView mDatePicker = new TimePickerBuilder(getContext(), new OnTimeSelectListener() {
                                @Override
                                public void onTimeSelected(Date date, View v) {
                                    itemBirth.setDetailText(DateUtils.date2String(date, DateUtils.yyyyMMdd.get()));
                                }
                            }).setTitleText("出生日期").build();
                            mDatePicker.show();
                            break;

                        case "身高":
                            new MaterialDialog.Builder(getContext())
                                    .title("身高")
                                    .content("请输入身高，单位为cm")
                                    .input(
                                            getString(R.string.hint_please_input_height),
                                            "",
                                            false,
                                            (new MaterialDialog.InputCallback() {
                                                @Override
                                                public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                                                }
                                            }))
                                    .inputType(
                                            InputType.TYPE_CLASS_NUMBER)
                                    .inputRange(1, 3)
                                    .positiveText("确定")
                                    .negativeText("取消")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            itemHeight.setDetailText(dialog.getInputEditText().getText().toString());
                                        }
                                    })
                                    .cancelable(false)
                                    .show();
                            break;

                        case "体重":
                            new MaterialDialog.Builder(getContext())
                                    .title("体重")
                                    .content("请输入体重，单位为kg")
                                    .input(
                                            getString(R.string.hint_please_input_weight),
                                            "",
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
                                            itemWeight.setDetailText(dialog.getInputEditText().getText().toString());
                                        }
                                    })
                                    .cancelable(false)
                                    .show();
                            break;
                        default:
                            XToastUtils.toast(text + " is Clicked");
                    }
                }
            }
        };

        int size = DensityUtils.dp2px(getContext(), 20);
        XUIGroupListView.newSection(getContext())
                .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
                .addItemView(itemName, onClickListener)
                .addItemView(itemSex, onClickListener)
                .addItemView(itemBirth, onClickListener)
                .addItemView(itemHeight, onClickListener)
                .addItemView(itemWeight, onClickListener)
                .addTo(mGroupListView);
    }
}
