package com.padyun.scripttools.biz.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

/**
 * Created by daiepngfei on 10/9/19
 */
public class CvCropBubbleOptionsOverlay extends ViewGroup {

    private Rect mRect;

    public CvCropBubbleOptionsOverlay(@NonNull Context context) {
        super(context);
    }

    public CvCropBubbleOptionsOverlay(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final Rect r = mRect;
        return r != null && r.contains((int) ev.getX(), (int) ev.getY()) || super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    /**
     * @param key
     * @param l
     * @param t
     * @param r
     * @param b
     */
    public void setRect(String key, int l, int t, int r, int b) {
        if (mRect == null) mRect = new Rect();
        mRect.set(l, t, r, b);
        postInvalidate();
    }

}
