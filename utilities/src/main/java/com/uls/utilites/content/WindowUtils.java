package com.uls.utilites.content;

import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by daiepngfei on 2020-12-15
 */
public class WindowUtils {
    public static void setWindowStatusBarColor(int color, Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }
}
