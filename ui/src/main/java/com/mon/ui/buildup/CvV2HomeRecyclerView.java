package com.mon.ui.buildup;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by daiepngfei on 12/29/17
 */

public class CvV2HomeRecyclerView extends RecyclerView {

    private boolean mIsFreezingTouchEvent;

    public CvV2HomeRecyclerView(Context context, @Nullable AttributeSet attrs) {
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
