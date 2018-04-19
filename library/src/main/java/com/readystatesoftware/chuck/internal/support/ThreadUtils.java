package com.readystatesoftware.chuck.internal.support;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * author wangdou
 * date 2018/4/19
 */
public class ThreadUtils {

    public static Context applicationContext;
    public static volatile Handler applicationHandler;


    public static void onCreate(Context context) {
        applicationContext = context;
        applicationHandler = new Handler(context.getMainLooper());
    }

    public static void runOnUIThread(Runnable runnable) {
        runOnUIThread(runnable, 0L);
    }

    public static void runOnUIThread(Runnable runnable, long delay) {
        if (delay == 0L) {
            applicationHandler.post(runnable);
        } else {
            applicationHandler.postDelayed(runnable, delay);
        }

    }

    public static void cancelRunOnUIThread(Runnable runnable) {
        applicationHandler.removeCallbacks(runnable);
    }
    private static Executor singleDBThread;

    public static Executor getSingleDB() {
        if (singleDBThread == null) {
            singleDBThread = Executors.newSingleThreadExecutor();
        }
        return singleDBThread;
    }

}
