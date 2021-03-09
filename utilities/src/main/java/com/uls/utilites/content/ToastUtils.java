package com.uls.utilites.content;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.uls.utilites.un.Useless;

/**
 * Toast统一管理类
 *
 * @author way
 */
public class ToastUtils {

//    private static Toast toast;

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    @SuppressLint("ShowToast")
    public static void show(Context context, CharSequence message) {
        Log.e("hjh", "toast1");
        if (Useless.nulls(context, message) || Useless.isEmpty(message.toString())) return;
        Log.e("hjh", "toast2" + message);
        showToast(context, message);
//        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void show(Context context, int message) {
        if (Useless.nulls(context, message)) return;
        showToast(context, message);
//        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, CharSequence message, int duration) {
        if (Useless.nulls(context, message)) return;
        showToast(context, message);
//        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 判断手机是否是华为，用于toast提示适配
     *
     * @return
     */
    private static boolean isHuaWei() {
        String manufacturer = Build.MANUFACTURER;
        //这个字符串可以自己定义,例如判断华为就填写huawei,魅族就填写meizu
        if ("huawei".equalsIgnoreCase(manufacturer)) {
            return true;
        }
        return false;
    }


    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, int message, int duration) {
        if (Useless.nulls(context, message)) return;
        showToast(context, message);
//        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, int message) {
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        });
    }

    public static void showToast(Context context, CharSequence message) {
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        });
    }

}
