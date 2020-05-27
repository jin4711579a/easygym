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

package com.libenli.easygym.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String CREATE_TABLE_START_SQL = "CREATE TABLE IF NOT EXISTS ";
    private static final String INSERT_TABLE_START_SQL = "INSERT INTO ";
    private static final String CREATE_TABLE_PRIMIRY_SQL = " integer primary key autoincrement,";
    /** 数据库名称 */
    private static final String DB_NAME = "easyGym.db";
    /** 数据库版本 */
    private static final int VERSION = 1;
    /** 用户信息表 */
    public static final String TABLE_INFO = "info";
    /** 日常锻炼表 */
    public static final String TABLE_SCHEDULE = "schedule";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_INFO);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULE);

            onCreate(db);
        }
    }


    private synchronized void createTables(SQLiteDatabase db) {
        if (db == null || db.isReadOnly()) {
            db = getWritableDatabase();
        }

        StringBuilder infoSql = new StringBuilder();
        infoSql.append(CREATE_TABLE_START_SQL).append(TABLE_INFO).append(" ( ");
        infoSql.append(" _id").append(CREATE_TABLE_PRIMIRY_SQL);
        infoSql.append(" name").append(" varchar(32) default \"\" ,");
        infoSql.append(" sex").append(" varchar(32) default \"\" ,");
        infoSql.append(" birth").append(" varchar(32) default \"\" ,");
        infoSql.append(" height").append(" integer ,");
        infoSql.append(" weight").append(" real ,");
        infoSql.append(" avatar").append(" varchar(256) default \"\" ,");
        infoSql.append(" ctime").append(" long ,");
        infoSql.append(" update_time").append(" long )");

        StringBuilder scheSql = new StringBuilder();
        scheSql.append(CREATE_TABLE_START_SQL).append(TABLE_SCHEDULE).append(" ( ");
        scheSql.append(" _id").append(CREATE_TABLE_PRIMIRY_SQL);
        scheSql.append(" date").append(" varchar(32) default \"\" ,");
        scheSql.append(" time").append(" integer ,");
        scheSql.append(" count").append(" integer ,");
        scheSql.append(" weight").append(" real ,");
        scheSql.append(" calories").append(" real ,");
        scheSql.append(" complete").append(" integer ,");
        scheSql.append(" ctime").append(" long ,");
        scheSql.append(" update_time").append(" long )");
        try {
            db.execSQL(infoSql.toString());
            db.execSQL(scheSql.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
