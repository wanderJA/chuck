package com.readystatesoftware.chuck.internal.room;

import android.arch.persistence.room.Room;
import android.content.Context;

/**
 * author wangdou
 * date 2018/4/17
 */
public class RoomUtils {

    private TransactionDao transactionDao;
    private HttpDatabase database;

    private RoomUtils() {

    }

    public static RoomUtils getInstance() {
        return RoomUtilsHolder.holder;
    }

    private static class RoomUtilsHolder {
        private static RoomUtils holder = new RoomUtils();
    }

    public TransactionDao getTransaction(Context context) {
        if (transactionDao == null) {
            transactionDao = getDatabase(context).transationDao();
        }
        return transactionDao;
    }

    private HttpDatabase getDatabase(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context, HttpDatabase.class, "chuck")
                    .addMigrations()
//                    .allowMainThreadQueries()
                    .build();
        }
        return database;
    }

}
