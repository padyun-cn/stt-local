package com.padyun.scripttools.biz.ui.views;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.padyun.scripttools.R;
import com.uls.utilites.ui.Viewor;
import com.uls.utilites.un.Useless;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.customview.widget.ViewDragHelper;


/**
 * Created by daiepngfei on 7/10/19
 */
@SuppressWarnings("NullableProblems")
public class CvSlideSettingDragLayout extends ConstraintLayout {

    private ViewDragHelper mViewDragHelper;
    private Rect mStartRect = new Rect(), mEndRect = new Rect();
    private PointF mStartPointF, mEndPointF;
    public static final float RS[] = {0.306f, 0.105f, 0.62f, 0.466f};

    public CvSlideSettingDragLayout(Context context) {
        super(context);
        initialize();
    }

    public CvSlideSettingDragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_overlays_of_slide_setting, this, true);

        final View startButton = findViewById(R.id.button_slide_start);
        final View endButton = findViewById(R.id.button_slide_end);
        final View bgFrame = findViewById(R.id.bg_frame);
        bgFrame.setVisibility(mStartRect.isEmpty() || mEndRect.isEmpty() ? View.VISIBLE : View.INVISIBLE);
        mViewDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(@NonNull View view, int i) {
                return view == startButton || view == endButton;
            }

            @Override
            public void onViewDragStateChanged(int state) {
                super.onViewDragStateChanged(state);
            }

            @Override
            public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {
                super.onViewCaptured(capturedChild, activePointerId);
                bgFrame.setVisibility(View.INVISIBLE);
                endButton.setVisibility(capturedChild == startButton && mEndRect.isEmpty() ? View.INVISIBLE : View.VISIBLE);
                startButton.setVisibility(capturedChild == endButton && mStartRect.isEmpty()? View.INVISIBLE : View.VISIBLE);
                if(capturedChild == startButton && mStartPointF != null) {
                    mStartPointF = null;
                }
                if(capturedChild == endButton && mEndPointF != null) {
                    mEndPointF = null;
                }
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                if (top > getHeight() - child.getMeasuredHeight()) {
                    top = getHeight() - child.getMeasuredHeight();
                } else if (top < 0) {
                    top = 0;
                }
                return top;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                if (left > getWidth() - child.getMeasuredWidth()) {
                    left = getWidth() - child.getMeasuredWidth();
                } else if (left < 0) {
                    left = 0;
                }
                return left;
            }

            @Override
            public int getViewVerticalDragRange(View child) {
                return getMeasuredHeight() - child.getMeasuredHeight();
            }

            @Override
            public int getViewHorizontalDragRange(View child) {
                return getMeasuredWidth() - child.getMeasuredWidth();
            }

            @Override
            public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);
                final Rect r = new Rect();
                r.set(releasedChild.getLeft(), releasedChild.getTop(), releasedChild.getLeft() + releasedChild.getMeasuredWidth(), releasedChild.getTop() + releasedChild.getMeasuredHeight());
                if (releasedChild == startButton) {
                    mStartRect = r;
                } else {
                    mEndRect = r;
                }

                startButton.setVisibility(View.VISIBLE);
                endButton.setVisibility(View.VISIBLE);
                bgFrame.setVisibility(mStartRect.isEmpty() || mEndRect.isEmpty() ? View.VISIBLE : View.INVISIBLE);
            }
        });

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        final View startButton = findViewById(R.id.button_slide_start);
        final View endButton = findViewById(R.id.button_slide_end);
        final Rect endRect = mEndRect, startRect = mStartRect;
        final PointF startPointF = mStartPointF, endPointF = mEndPointF;
        layoutTouchFrameChild(startButton, startRect, startPointF);
        layoutTouchFrameChild(endButton, endRect, endPointF);
    }

    private void layoutTouchFrameChild(View startButton, Rect startRect, PointF startPointF) {
        if (startPointF != null) {
            startRect.left = (int)(getMeasuredWidth() * startPointF.x - startButton.getMeasuredWidth() * RS[0]);
            startRect.top = (int)(getMeasuredHeight() * startPointF.y - startButton.getMeasuredHeight() * RS[1]);
            startRect.right = startRect.left + startButton.getMeasuredWidth();
            startRect.bottom = startRect.top + startButton.getMeasuredHeight();
            startButton.layout(startRect.left, startRect.top, startRect.right, startRect.bottom);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    public Rect getStartRect() {
        return mStartRect;
    }

    public Rect getEndRect() {
        return mEndRect;
    }


    public void setStartPoint(PointF p) {
        mStartPointF = p;
        if(Useless.nulls(mStartPointF, mEndPointF)) Viewor.invisible(findViewById(R.id.bg_frame));
    }

    public void setEndPoint(PointF p) {
        mEndPointF = p;
        if(Useless.nulls(mStartPointF, mEndPointF)) Viewor.invisible(findViewById(R.id.bg_frame));
    }
}
