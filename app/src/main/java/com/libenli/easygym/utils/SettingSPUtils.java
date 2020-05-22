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

package com.libenli.easygym.utils;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.xuexiang.xutil.XUtil;
import com.xuexiang.xutil.data.BaseSPUtil;

import java.util.List;

/**
 * SharedPreferences管理工具基类
 *
 * @author xuexiang
 * @since 2018/11/27 下午5:16
 */
public class SettingSPUtils extends BaseSPUtil {

    private static volatile SettingSPUtils sInstance = null;

    private SettingSPUtils(Context context) {
        super(context);
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static SettingSPUtils getInstance() {
        if (sInstance == null) {
            synchronized (SettingSPUtils.class) {
                if (sInstance == null) {
                    sInstance = new SettingSPUtils(XUtil.getContext());
                }
            }
        }
        return sInstance;
    }


    private final String IS_FIRST_OPEN_KEY = "is_first_open_key";

    private final String IS_AGREE_PRIVACY_KEY = "is_agree_privacy_key";

    private final String IS_USE_CUSTOM_THEME_KEY = "is_use_custom_theme_key";

    private final String TARGET_WEIGHT = "target_weight";

    private final String JSON_DEVICES = "json_devices";


    /**
     * 是否是第一次启动
     */
    public boolean isFirstOpen() {
        return getBoolean(IS_FIRST_OPEN_KEY, true);
    }

    /**
     * 设置是否是第一次启动
     */
    public void setIsFirstOpen(boolean isFirstOpen) {
        putBoolean(IS_FIRST_OPEN_KEY, isFirstOpen);
    }

    /**
     * @return 是否同意隐私政策
     */
    public boolean isAgreePrivacy() {
        return getBoolean(IS_AGREE_PRIVACY_KEY, false);
    }

    public void setIsAgreePrivacy(boolean isAgreePrivacy) {
        putBoolean(IS_AGREE_PRIVACY_KEY, isAgreePrivacy);
    }

    public boolean isUseCustomTheme() {
        return getBoolean(IS_USE_CUSTOM_THEME_KEY, false);
    }

    public void setIsUseCustomTheme(boolean isUseCustomTheme) {
        putBoolean(IS_USE_CUSTOM_THEME_KEY, isUseCustomTheme);
    }

    public String getTarget() {
        return getString(TARGET_WEIGHT, "100");
    }

    public void setTarget(String weight) {
        putString(TARGET_WEIGHT, weight);
    }

    public String getDevices() {
        return getString(JSON_DEVICES, "");
    }

    public void setDevices(String jsonDevices) {
        putString(JSON_DEVICES, jsonDevices);
    }

}
