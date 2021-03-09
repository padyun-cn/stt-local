package com.mon.ui.buildup;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Created by daiepngfei on 12/29/17
 */

public class CvV2HomeSwipeRefreshLayout extends SwipeRefreshLayout {

    private boolean mIsFreezingTouchEvent;

    public CvV2HomeSwipeRefreshLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        return !mIsFreezingTouchEvent && super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return !mIsFreezingTouchEvent && super.onTouchEvent(e);
    }

    public void setIsFreezingTouchEvent(boolean isFreezingTouchEvent) {
        this.mIsFreezingTouchEvent = isFreezingTouchEvent;
    }
}
