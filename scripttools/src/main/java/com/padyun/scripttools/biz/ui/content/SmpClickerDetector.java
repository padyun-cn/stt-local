package com.padyun.scripttools.biz.ui.content;

import android.annotation.SuppressLint;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by daiepngfei on 10/21/19
 */
public class SmpClickerDetector implements GestureDetector.OnGestureListener {
    private GestureDetector mGetstureDetector;
    private boolean enable;
    private View.OnClickListener l;
    private View v;
    private float downX, downY;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @SuppressLint("ClickableViewAccessibility")
    public SmpClickerDetector(View v) {
        if (v != null) {
            this.v = v;
            this.v.setOnTouchListener((v1, event) -> onTouchEvent(event));
            mGetstureDetector = new GestureDetector(v.getContext(), this);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        return enable && mGetstureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        this.downX = e.getX();
        this.downY = e.getY();
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (l != null) l.onClick(this.v);
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public void setOnClickListener(View.OnClickListener l) {
        this.l = l;
    }

    public float getDownX() {
        return downX;
    }

    public float getDownY() {
        return downY;
    }
}
