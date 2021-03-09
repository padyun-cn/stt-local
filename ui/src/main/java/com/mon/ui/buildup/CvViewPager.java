package com.mon.ui.buildup;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

/**
 * Created by daiepngfei on 2019-12-31
 */
public class CvViewPager extends ViewPager {
    private boolean touchDisabled;

    public CvViewPager(@NonNull Context context) {
        super(context);
    }

    public CvViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return !touchDisabled && super.onInterceptTouchEvent(ev);
    }

    public boolean isTouchDisabled() {
        return touchDisabled;
    }

    public void setTouchDisabled(boolean touchDisabled) {
        this.touchDisabled = touchDisabled;
    }
}
