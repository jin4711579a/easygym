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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.libenli.easygym.model.Info;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DBManager {

    private static final String TAG = "DBManager";

    private AtomicInteger mOpenCounter = new AtomicInteger();
    private static DBManager instance;
    private static SQLiteOpenHelper mDBHelper;
    private SQLiteDatabase mDatabase;
    private boolean allowTransaction = true;
    private Lock writeLock = new ReentrantLock();
    private volatile boolean writeLocked = false;

    /**
     * 获取DBManager 实例
     *
     * @return DBManager
     */
    public static synchronized DBManager getInstance() {
        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
    }

    public void release() {
        if (mDBHelper != null) {
            mDBHelper.close();
            mDBHelper = null;
        }
        instance = null;
    }

    public void init(Context context) {
        if (context == null) {
            return;
        }

        if (mDBHelper == null) {
            mDBHelper = new DBHelper(context.getApplicationContext());
        }

        List<Info> infoList = queryInfo();

        if (infoList.size() < 1) {
            addInfo(new Info("noName", "男", "1999-12-12", 175, 50.0f));
        }
    }

    /**
     * 打开数据库
     * @return SQLiteDatabase
     */
    public synchronized SQLiteDatabase openDatabase() {
        if (mOpenCounter.incrementAndGet() == 1) {
            // Opening new database
            try {
                mDatabase = mDBHelper.getWritableDatabase();
            } catch (Exception e) {
                mDatabase = mDBHelper.getReadableDatabase();
            }
        }
        return mDatabase;
    }

    /**
     * 关闭数据库
     */
    public synchronized void closeDatabase() {
        if (mOpenCounter.decrementAndGet() == 0) {
            // Closing database
            mDatabase.close();
        }
    }


    public List<Info> queryInfo() {
        List<Info> infoList = new ArrayList<>();
        Cursor cursor = null;

        try {
            if (mDBHelper == null) {
                return infoList;
            }
            SQLiteDatabase db = mDBHelper.getReadableDatabase();
            cursor = db.query(DBHelper.TABLE_INFO, null, null, null, null, null, null);
            while (cursor != null && cursor.getCount() > 0 && cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("_id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String sex = cursor.getString(cursor.getColumnIndex("sex"));
                String birth = cursor.getString(cursor.getColumnIndex("birth"));
                int height = cursor.getInt(cursor.getColumnIndex("height"));
                float weight = cursor.getFloat(cursor.getColumnIndex("weight"));
                String avatar = cursor.getString(cursor.getColumnIndex("avatar"));

                Info info = new Info(id, name, sex, birth, height, weight, avatar);
                infoList.add(info);
            }
        } finally {
            closeCursor(cursor);
        }

        return infoList;
    }

    public boolean addInfo(Info info) {
        if (mDBHelper == null) {
            return false;
        }

        try {
            mDatabase = mDBHelper.getWritableDatabase();
            beginTransaction(mDatabase);

            ContentValues cv = new ContentValues();
            cv.put("name", info.getName());
            cv.put("sex", info.getSex());
            cv.put("birth", info.getBirth());
            cv.put("height", info.getHeight());
            cv.put("weight", info.getWeight());
            cv.put("avatar", info.getAvatar());

            long id = mDatabase.insert(DBHelper.TABLE_INFO, null, cv);

            if (id < 0) {
                return false;
            }
            setTransactionSuccessful(mDatabase);
        } finally {
            endTransaction(mDatabase);
        }
        return true;
    }

    public boolean editInfo(Info info) {
        if (mDBHelper == null) {
            return false;
        }

        try {
            mDatabase = mDBHelper.getWritableDatabase();
            beginTransaction(mDatabase);

            ContentValues cv = new ContentValues();
            cv.put("name", info.getName());
            cv.put("sex", info.getSex());
            cv.put("birth", info.getBirth());
            cv.put("height", info.getHeight());
            cv.put("weight", info.getWeight());

            long id = mDatabase.update(DBHelper.TABLE_INFO, cv, null, null);

            if (id < 0) {
                return false;
            }
            setTransactionSuccessful(mDatabase);
        } finally {
            endTransaction(mDatabase);
        }

        return true;
    }

    public boolean editInfoAvatar(String path) {
        if (mDBHelper == null) {
            return false;
        }

        try {
            mDatabase = mDBHelper.getWritableDatabase();
            beginTransaction(mDatabase);

            ContentValues cv = new ContentValues();
            cv.put("avatar", path);

            long id = mDatabase.update(DBHelper.TABLE_INFO, cv, null, null);

            if (id < 0) {
                return false;
            }
            setTransactionSuccessful(mDatabase);
        } finally {
            endTransaction(mDatabase);
        }

        return true;
    }




    private void setTransactionSuccessful(SQLiteDatabase mDatabase) {
        if (allowTransaction) {
            mDatabase.setTransactionSuccessful();
        }
    }

    private void beginTransaction(SQLiteDatabase mDatabase) {
        if (allowTransaction) {
            mDatabase.beginTransaction();
        } else {
            writeLock.lock();
            writeLocked = true;
        }
    }

    private void endTransaction(SQLiteDatabase mDatabase) {
        if (allowTransaction) {
            mDatabase.endTransaction();
        }
        if (writeLocked) {
            writeLock.unlock();
            writeLocked = false;
        }
    }


    private void closeCursor(Cursor cursor) {
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Throwable e) {
            }
        }
    }
}
