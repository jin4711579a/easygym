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


public class Schedule {
    private int id;
    private String date;
    private int time;
    private int count;
    private float weight;
    private float calories;
    private int complete;

    public Schedule() {}

    public Schedule(int id, String date, int time, int count, float weight, float calories, int complete) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.count = count;
        this.weight = weight;
        this.calories = calories;
        this.complete = complete;
    }

    public Schedule(String date, int time, int count, float weight, float calories, int complete) {
        this.date = date;
        this.time = time;
        this.count = count;
        this.weight = weight;
        this.calories = calories;
        this.complete = complete;
    }

    public Schedule(String date, int time, int count, float weight, float calories) {
        this.date = date;
        this.time = time;
        this.count = count;
        this.weight = weight;
        this.calories = calories;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getCalories() {
        return calories;
    }

    public void setCalories(float calories) {
        this.calories = calories;
    }

    public int getComplete() {
        return complete;
    }

    public void setComplete(int complete) {
        this.complete = complete;
    }
}
