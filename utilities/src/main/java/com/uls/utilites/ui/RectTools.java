package com.uls.utilites.ui;

import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by daiepngfei on 2020-04-08
 */
public class RectTools {

    /**
     *
     * @param e
     * @param scaledView
     * @param oriRect
     * @param oriWidth
     * @param oriHeight
     * @return
     */
    public static boolean isContainsMotionPointInScaledView(MotionEvent e, View scaledView, RectF oriRect, float oriWidth, float oriHeight ){
        if(e == null || scaledView == null || oriRect == null || oriWidth == 0 || oriHeight == 0){
            return false;
        }
        final float w = scaledView.getMeasuredWidth();
        final float h = scaledView.getMeasuredHeight();
        final float oriPicL = oriRect.left;
        final float oriPicT = oriRect.top;
        final float oriPicR = oriRect.right;
        final float oriPicB = oriRect.bottom;
        final float factorW = w / oriWidth;
        final float factorH = h / oriHeight;
        final RectF scacleTarRect = new RectF(factorW * oriPicL, factorH * oriPicT, factorW * oriPicR, factorH * oriPicB);
        return scacleTarRect.contains(e.getX(), e.getY());
    }
}
