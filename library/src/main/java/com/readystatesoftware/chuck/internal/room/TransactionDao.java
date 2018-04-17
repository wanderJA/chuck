package com.readystatesoftware.chuck.internal.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.readystatesoftware.chuck.internal.data.HttpTransaction;

import java.util.List;

/**
 * author wangdou
 * date 2018/4/17
 */
@Dao
public interface TransactionDao {
    @Insert
    void insertAll(HttpTransaction ... transactions);


    @Query("SELECT * FROM httptransaction")
    List<HttpTransaction> getAll();




}
