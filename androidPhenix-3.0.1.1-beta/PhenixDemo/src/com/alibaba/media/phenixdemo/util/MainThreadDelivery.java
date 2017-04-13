package com.alibaba.media.phenixdemo.util;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * UI主线程回调任务
 */
public final class MainThreadDelivery {

    public static final Handler MAIN_HANDLER;
    private static final Looper MAIN_LOOPER;
    private static final String TAG = "MainThreadDelivery";

    static {
        MAIN_LOOPER = Looper.getMainLooper();
        MAIN_HANDLER = new Handler(MAIN_LOOPER);
    }

    public static void run(Runnable runnable) {
        if (runnable == null) {
            Log.e(TAG, " runnable  == null ");
            return;
        }
        if (MAIN_LOOPER != Looper.myLooper()) {
            MAIN_HANDLER.post(runnable);
        } else {
            runnable.run();
        }
    }
}
