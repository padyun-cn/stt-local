package com.padyun.scripttools.biz.ui.views;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.customview.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.uls.utilites.un.Useless;
import com.padyun.scripttools.R;


/**
 * Created by daiepngfei on 7/10/19
 */
@SuppressWarnings("NullableProblems")
public class CvColorDropperDragLayout extends ConstraintLayout {

    private ViewDragHelper mViewDragHelper;
    private View child0;
    private boolean mIsEnableDrag;
    private Integer mOutMarginH, mOutMarginV;
    private int child0Left, child0Top;

    public CvColorDropperDragLayout(Context context) {
        super(context);
        initialize();
    }

    public CvColorDropperDragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.overlays_screen_color_picker_draglayout, this, true);
        final View startButton = findViewById(R.id.cnm_picker);
        child0 = startButton;
        mViewDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {

            @Override
            public boolean tryCaptureView(@NonNull View view, int i) {
                return view == startButton;
            }

            @Override
            public void onViewDragStateChanged(int state) {
                super.onViewDragStateChanged(state);
            }

            @Override
            public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {
                super.onViewCaptured(capturedChild, activePointerId);
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
               /* if (top > getHeight() - child.getMeasuredHeight()) {
                    top = getHeight() - child.getMeasuredHeight();
                } else if (top < 0) {
                    top = 0;
                }
                return top;*/
                return Math.max(top, 0);
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                /*if (left > getWidth() - child.getMeasuredWidth()) {
                    left = getWidth() - child.getMeasuredWidth();
                } else if (left < 0) {
                    left = 0;
                }
                return left;*/
                return Math.max(0, left);
            }

            @Override
            public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
                if (mOnPositionChanged != null) {
                    mOnPositionChanged.onPosChanged(left, top, changedView.getWidth(), changedView.getHeight());
                }
            }

            @Override
            public int getViewVerticalDragRange(View child) {
                return getMeasuredHeight();
            }

            @Override
            public int getViewHorizontalDragRange(View child) {
                return getMeasuredWidth();
            }

            @Override
            public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);
            }
        });

    }

    public void offsetChild0(int x, int y) {
        child0.offsetLeftAndRight(x);
        child0.offsetTopAndBottom(y);
        child0.invalidate();
        initialize();
    }

    public void setEnableDrag(boolean isEnableDrag) {
        this.mIsEnableDrag = isEnableDrag;
    }

    private boolean isEnableDrag() {
        return this.mIsEnableDrag;
    }

    public void setOutMargin(int outMarginHorizontal, int outMarginVertical){
        this.mOutMarginH = outMarginHorizontal;
        this.mOutMarginV = outMarginVertical;
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
         child0.layout(child0Left, child0Top, child0Left + child0.getMeasuredWidth(), child0Top + child0.getMeasuredHeight());
    }

    public void setCenterOffset(int x, int y) {
        setCenterOffset(x, y, false);
    }

    public void setCenterOffset(int x, int y, boolean notify) {
        if (child0 != null) {
            final int hw = child0.getMeasuredWidth() / 2;
            final int hh = child0.getMeasuredHeight() / 2;
            final int cx = getLeft() + hw;
            final int cy = getTop() + hh;

            final int marginH = mOutMarginH == null ? -hw : mOutMarginH;
            final int marginV = mOutMarginV == null ? -hh : mOutMarginV;
            final int l = Useless.limitInRange(getLeft() - cx + x, -marginH, getMeasuredWidth() - child0.getMeasuredWidth() + marginH);
            final int t = Useless.limitInRange(getTop() - cy + y, -marginV, getMeasuredHeight() - child0.getMeasuredHeight() + marginV);
            if(l != child0.getLeft() || t != child0.getTop()) {
                child0Left = l;
                child0Top = t;
                //requestLayout();
                if (notify && mOnPositionChanged != null) {
                    mOnPositionChanged.onPosChanged(l, t,  child0.getMeasuredWidth(), child0.getMeasuredHeight());
                }

            }
        }
    }

    public void setOffset(int x, int y) {
        /*if (x >= 0 && y >= 0 && x <= getMeasuredWidth() && y <= getMeasuredHeight()) {
            if (child0 != null)
                child0.layout(x, y, x + child0.getMeasuredWidth(), y + child0.getMeasuredHeight());
        }*/
    }

    private OnPositionChanged mOnPositionChanged;

    public void printChild() {
        System.out.println("child0 : " + child0.getLeft() + ", " + child0.getTop());
    }

    public interface OnPositionChanged {
        void onPosChanged(int x, int y, int width, int height);
    }

    public void setOnPositionChanged(OnPositionChanged onPositionChanged) {
        this.mOnPositionChanged = onPositionChanged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnableDrag()) return super.onTouchEvent(event);
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isEnableDrag()) return super.onInterceptTouchEvent(ev);
        return mViewDragHelper.shouldInterceptTouchEvent(ev);

    }

    @Override
    public void computeScroll() {
        if (isEnableDrag()) {
            if (mViewDragHelper.continueSettling(true)) {
                invalidate();
            }
        }
    }
}
