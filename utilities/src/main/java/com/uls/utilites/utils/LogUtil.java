package com.uls.utilites.utils;

import android.util.Log;

import com.uls.utilites.BuildConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by daiepngfei on 11/5/19
 */
public class LogUtil {
    private static Map<String, Long> sUncontinuedLogs = new ConcurrentHashMap<>();

    public static void v(String tag, String msg) {
        v(tag, msg, null, 0);
    }

    public static void i(String tag, String msg) {
        i(tag, msg, null, 0);
    }

    public static void d(String tag, String msg) {
        d(tag, msg, null, 0);
    }

    public static void e(String tag, String msg, Exception e) {
        e(tag, msg, e, null, 0);
    }

    public static void e(String tag, String msg) {
        e(tag, msg, null, null, 0);
    }

    public static void w(String tag, String msg, Exception e) {
        w(tag, msg, e, null, 0);
    }

    public static void v(String tag, String msg, String intervalTag, long interval) {
        if (BuildConfig.DEBUG && timeAllowed(intervalTag, interval)) {
            Log.v(tag, msg);
        }
    }

    public static void i(String tag, String msg, String intervalTag, long interval) {
        if (BuildConfig.DEBUG && timeAllowed(intervalTag, interval)) {
            Log.i(tag, msg);
        }
    }

    public static void d(String tag, String msg, String intervalTag, long interval) {
        if (BuildConfig.DEBUG && timeAllowed(intervalTag, interval)) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg, Exception e, String intervalTag, long interval) {
        if (BuildConfig.DEBUG && timeAllowed(intervalTag, interval)) {
            if(e == null){
                Log.e(tag, msg);
            } else {
                Log.e(tag, msg, e);
            }
        }
    }

    public static void w(String tag, String msg, Exception e, String intervalTag, long interval) {
        if (BuildConfig.DEBUG && timeAllowed(intervalTag, interval)) {
            Log.w(tag, msg, e);
        }
    }

    private static boolean timeAllowed(String intervalTag, long interval) {
        if (intervalTag != null) {
            final long cur = System.currentTimeMillis();
            Long lastTime = sUncontinuedLogs.get(intervalTag);
            if (lastTime != null && lastTime > 0) {
                final long delta = cur - lastTime;
                if (delta < interval) {
                    return false;
                }
            }
            sUncontinuedLogs.put(intervalTag, cur);
        }
        return true;
    }

}
