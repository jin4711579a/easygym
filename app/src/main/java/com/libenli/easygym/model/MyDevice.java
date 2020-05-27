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

package com.libenli.easygym.model;

import android.view.View;

import java.util.List;

public class MyDevice {
    private String name;
    private String address;

    public MyDevice() {}

    public MyDevice(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddressData() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean include(List<MyDevice> devices) {
        for (MyDevice deviceTemp : devices) {
            if (deviceTemp.getAddressData().equals(address)) {
                return true;
            }
        }
        return false;
    }
}
