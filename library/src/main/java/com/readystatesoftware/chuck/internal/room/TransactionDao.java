package com.readystatesoftware.chuck.internal.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.readystatesoftware.chuck.internal.data.HttpTransaction;

import java.util.List;

/**
 * author wangdou
 * date 2018/4/17
 */
@Dao
public interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(HttpTransaction... transactions);


    @Query("SELECT * FROM httptransaction ORDER BY requestDate DESC")
    List<HttpTransaction> getAll();

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(HttpTransaction... httpTransaction);

    @Delete
    void delete(HttpTransaction... httpTransactions);

    @Query("SELECT * FROM HTTPTRANSACTION WHERE responseCode LIKE:word+'%'")
    List<HttpTransaction> findResponse(String word);

    @Query("SELECT * FROM HTTPTRANSACTION WHERE path LIKE:word")
    List<HttpTransaction> findPath(String word);

    @Query("SELECT * FROM HTTPTRANSACTION WHERE _id = :transactionId")
    HttpTransaction findById(long transactionId);

    @Query("DELETE FROM HTTPTRANSACTION")
    void deleteAll();
}
