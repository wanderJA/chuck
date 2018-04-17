package com.readystatesoftware.chuck.internal.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.readystatesoftware.chuck.internal.data.HttpTransaction;

/**
 * author wangdou
 * date 2018/4/17
 */
@Database(entities = {HttpTransaction.class},version = 1)
public abstract class HttpDatabase extends RoomDatabase {
    public abstract TransactionDao transationDao();
}
