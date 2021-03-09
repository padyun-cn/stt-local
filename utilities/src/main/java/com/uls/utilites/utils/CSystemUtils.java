package com.uls.utilites.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.uls.utilites.un.Useless;

/**
 * Created by daiepngfei on 12/4/18
 */
@SuppressWarnings("WeakerAccess")
public class CSystemUtils {


    public static <T> T getSystemServiceSafe(Context ctx, String serviceName, Class<T> cls) {
        Object obj = null;
        T t = null;
        if (ctx != null && !Useless.isEmpty(serviceName)) obj = ctx.getSystemService(serviceName);
        if (cls != null && cls.isInstance(obj)) t = (T) obj;
        return t;
    }

    public static class Clipboard {

        public static String getText(Context ctx) {
            String rs = "";
            try {
                ClipboardManager manager = getClipboard(ctx);
                if (manager != null) {
                    if (manager.hasPrimaryClip()) {
                        ClipData data = manager.getPrimaryClip();
                        if (data != null && data.getItemAt(0) != null && data.getItemAt(0).getText() != null) {
                            rs = data.getItemAt(0).getText().toString();
                        }
                    } else if (manager.getText() != null) {
                        rs = manager.getText().toString();
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            /* MODIFIED END. */
            return rs;
        }

        public static void setClipboardText(Context ctx, String label, String text) {
            ClipboardManager manager = getClipboard(ctx);
            if (manager != null) manager.setPrimaryClip(ClipData.newPlainText(label, text));
        }


        public static ClipboardManager getClipboard(Context ctx) {
            return getSystemServiceSafe(ctx, Context.CLIPBOARD_SERVICE, ClipboardManager.class);
        }
    }

    public static class InputMethod {

        public static InputMethodManager inputMethod(Context ctx) {
            return getSystemServiceSafe(ctx, Context.INPUT_METHOD_SERVICE, InputMethodManager.class);
        }

        public static void hideOnFocused(Activity act, View focused) {
            if (act != null && !act.isFinishing() && focused == act.getCurrentFocus()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    if(!act.isDestroyed()) show(act, focused, false);
                } else show(act, focused, false);
            }
        }

        public static void show(Activity act, View focused) {
            show(act, focused, true);
        }

        private static void show(Activity act, View focused, boolean show) {
            final InputMethodManager imm = inputMethod(act);
            if (imm != null) {
                if (show) imm.showSoftInput(focused, InputMethodManager.SHOW_FORCED);
                else imm.hideSoftInputFromWindow(focused.getWindowToken(), 0);
            }
        }

        public static void show(Activity act, View focused , ResultReceiver receiver){
            final InputMethodManager imm = inputMethod(act);
            if (imm != null) {
                /*if (show)*/ imm.showSoftInput(focused, InputMethodManager.SHOW_FORCED, new ResultReceiver(new Handler(Looper.getMainLooper())){
                    @Override
                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                        System.out.println("InputMethodManager#resultCode is " + resultCode);
                    }
                });
//                else imm.hideSoftInputFromWindow(focused.getWindowToken(), 0);
            }
        }



        public static boolean isSoftShowing(Activity activity) {
            //获取当前屏幕内容的高度
            int screenHeight = activity.getWindow().getDecorView().getHeight();
            //获取View可见区域的bottom
            Rect rect = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            return screenHeight - rect.bottom - getSoftButtonsBarHeight(activity) != 0;
        }

        public static void hideKeyboard(Activity activity) {
            final InputMethodManager imm = inputMethod(activity);
            if (imm != null) {
                imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
            }
        }

        /**
         * 获取底部虚拟键盘的高度
         *
         * @return
         */
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        public static int getSoftButtonsBarHeight(Activity activity) {
            DisplayMetrics metrics = new DisplayMetrics();
            //这个方法获取可能不是真实屏幕的高度
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            //获取当前屏幕的真实高度
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight) {
                return realHeight - usableHeight;
            } else {
                return 0;
            }
        }
    }
}
