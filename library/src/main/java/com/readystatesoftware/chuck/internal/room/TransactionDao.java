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
    @Insert
    long insert(HttpTransaction transactions);

    @Insert
    long[] insertAll(HttpTransaction... transactions);

    @Query("SELECT * FROM httptransaction ORDER BY requestDate DESC")
    List<HttpTransaction> getAll();

    @Query("SELECT * FROM HTTPTRANSACTION ORDER BY requestDate DESC LIMIT:pageSize OFFSET:startId")
    List<HttpTransaction> getPage(int startId, int pageSize);

    @Query("select COUNT(1) FROM HTTPTRANSACTION")
    int getCount();

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(HttpTransaction httpTransaction);

    @Delete
    void delete(HttpTransaction... httpTransactions);

    @Query("SELECT * FROM HTTPTRANSACTION WHERE responseCode LIKE:word+'%' ORDER BY requestDate DESC LIMIT 2000")
    List<HttpTransaction> findResponse(String word);

    @Query("SELECT * FROM HTTPTRANSACTION WHERE path LIKE:word ORDER BY requestDate DESC LIMIT 2000")
    List<HttpTransaction> findPath(String word);

    @Query("SELECT * FROM HTTPTRANSACTION WHERE _id = :transactionId")
    HttpTransaction findById(long transactionId);

    @Query("DELETE FROM HTTPTRANSACTION")
    void deleteAll();

}
