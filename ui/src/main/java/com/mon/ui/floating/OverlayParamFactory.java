package com.mon.ui.floating;

import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.WindowManager;

/**
 * Created by daiepngfei on 2021-01-19
 */
public class OverlayParamFactory {

    public static WindowManager.LayoutParams matchParent() {
        return matchParent(Gravity.START | Gravity.TOP);
    }
    /**
     *
     * @param gravity
     * @return
     */
    public static WindowManager.LayoutParams matchParent(int gravity) {
        return getLayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, gravity);
    }

    /**
     *
     * @return
     */
    public static WindowManager.LayoutParams wrapContent() {
        return wrapContent(Gravity.START | Gravity.TOP);
    }

    /**
     *
     * @param gravity
     * @return
     */
    public static WindowManager.LayoutParams wrapContent(int gravity) {
        return getLayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, gravity);
    }

    /**
     *
     * @param width
     * @param height
     * @param gravity
     * @return
     */
    public static WindowManager.LayoutParams getLayoutParams(int width, int height, int gravity) {
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = width;
        params.height = height;
        //初始化坐标
        params.x = 0;
        params.y = 0;
        //弹窗类型为系统Window
        System.out.println("mumu version " + Build.VERSION.SDK_INT);
//        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 24) {
//            params.type = WindowManager.LayoutParams.TYPE_TOAST;
//        } else {
//            params.type = WindowManager.LayoutParams.TYPE_PHONE;
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        //以左上角为基准
        params.gravity = gravity;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        //如果不加,背景会是一片黑色。
        params.format = PixelFormat.TRANSLUCENT;
        return params;
    }
}
