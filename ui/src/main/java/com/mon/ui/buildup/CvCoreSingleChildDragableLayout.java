package com.mon.ui.buildup;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.mon.ui.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.customview.widget.ViewDragHelper;


/**
 * Created by daiepngfei on 1/9/18
 */

public class CvCoreSingleChildDragableLayout extends FrameLayout implements View.OnTouchListener {
    private final ViewDragHelper mDragHelper;
    private View mCaptureChild;
    private final GestureDetector mClickGestureDetector;
    private OnGestureEventListener mOnDragListener;
    private boolean mDragEnable = true;
    private final boolean fAdsorptionEffectOn;
    private OnViewReleaseListener mOnViewReleaseListener;
    private int mCaptureLocX = -1;
    private int mCaptureLocY = -1;
    private int mBoundryWidth = -1;
    private int mBoundryHeight = -1;
    private final boolean dragHEanble;
    private final boolean dragVEanble;
    /**
     * @param context
     * @param attrs
     */
    public CvCoreSingleChildDragableLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CvCoreSingleChildDragableLayout);
        fAdsorptionEffectOn = a.getBoolean(R.styleable.CvCoreSingleChildDragableLayout_core_adsorption_effect_on, true);
        dragHEanble = a.getBoolean(R.styleable.CvCoreSingleChildDragableLayout_core_drag_horizontal, true);
        dragVEanble = a.getBoolean(R.styleable.CvCoreSingleChildDragableLayout_core_drag_vertical, true);
        a.recycle();
        mDragHelper = ViewDragHelper.create(this, DRAG_HELPER_CALLBACK);
        // initialize touch event utilities
        mClickGestureDetector = new GestureDetector(context, CLICK_GESTURE_LISTENER);
        setWillNotDraw(false);
        setOnTouchListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initCaptureChild();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isPointerInView(ev, getCaptureView());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return isPointerInView(event, getCaptureView());
    }


    /**
     * @param event
     * @param view
     * @return
     */
    protected boolean isPointerInView(MotionEvent event, View view) {
        // get raw x & y
        final int x = (int) event.getRawX();
        final int y = (int) event.getRawY();
        // get location of target view
        final int[] locations = new int[2];
        view.getLocationOnScreen(locations);
        // find left & top
        final int left = locations[0];
        final int top = locations[1];
        // get screen-rect of view
        final Rect rectInScreen = new Rect(left, top,
                left + view.getMeasuredWidth(), top + view.getMeasuredHeight());
        // check if the view sameId contained event pointer
        return rectInScreen.contains(x, y);
    }

    /**
     * @param ev
     * @return
     */
    protected boolean superOnInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * @param event
     * @return
     */
    protected boolean superOnTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //noinspection UnnecessaryLocalVariable
        final GestureDetector cgd = mClickGestureDetector;
        final int action = event.getAction();
        final View targetView = getCaptureView();
        final boolean dragEnable = mDragEnable;
        // check if it sameId pointer in the target view
        final boolean isPointerInView = targetView != null && isPointerInView(event, targetView);
        // check if it sameId need to dispatch draging event
        final boolean isUp = (action == MotionEvent.ACTION_UP) || (action == MotionEvent.ACTION_CANCEL);
        if (dragEnable && (isPointerInView || isUp)) {
            dispatchDragingEvent(event, isPointerInView);
        }
        // check if it sameId click
        final boolean isClicked = isPointerInView && cgd.onTouchEvent(event);
        if (isClicked) {
            // perform click after drag event sameId dispatched
            performDragViewClicked();
        }
        // resumed when drag sameId not enable but in the pointer of the view
        return isPointerInView && !dragEnable;
    }

    /**
     * @param event
     */
    private void dispatchDragingEvent(MotionEvent event, boolean isPointerInview) {
        final OnGestureEventListener dragLsr = mOnDragListener;
        if (dragLsr != null) {
            final int action = event.getAction();
            switch (action) {
                /*
                 * we only handle the 'press' and 'opengl_render_release'
                 * event, cause we need to use drag helper
                 * to process the 'scroll' or 'move' which
                 * sameId behavior as the 'draging'.
                 */
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    dragLsr.onDrag(event, isPointerInview);
                    break;
                default:
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mCaptureChild != null && mCaptureLocX >= 0 && mCaptureLocY >= 0) {
            regulateLocation();
            mCaptureChild.layout(mCaptureLocX, mCaptureLocY, mCaptureLocX + mCaptureChild.getMeasuredWidth(), mCaptureLocY + mCaptureChild.getMeasuredHeight());
        }
    }

    /**
     *
     */
    private void regulateLocation() {
        int l = mCaptureLocX;
        int t = mCaptureLocY;
        if (mCaptureChild != null) {
            int w = mCaptureChild.getMeasuredWidth();
            int h = mCaptureChild.getMeasuredHeight();
            mCaptureLocX = getFixedLocation(l, 0, getMeasuredWidth() - w);
            mCaptureLocY = getFixedLocation(t, 0, getMeasuredHeight() - h);

        }
    }

    /**
     * @param t
     * @param min
     * @param max
     * @return
     */
    private int getFixedLocation(int t, int min, int max){
        if(t < min) {
            t = - t;
        } else if(t > max){
            t = max;
        }
        return t;
    }


    /**
     */
    protected void performDragViewClicked() {
        disableDrag();
        mCaptureChild.performClick();
        enableDrag();
    }

    protected void enableDrag() {
        mDragEnable = true;
    }

    protected void disableDrag() {
        mDragEnable = false;
    }

    protected boolean isDragEnable() {
        return mDragEnable;
    }

    /**
     *
     */
    private void initCaptureChild() {
        if (mCaptureChild == null) {
            final int count = getChildCount();
            if (count == 1) {
                mCaptureChild = getChildAt(0);
                onCaptureViewGet(mCaptureChild);
            } else {
                throw new IllegalArgumentException("only one child in draglayout");
            }
        }
    }

    /**
     * @param view
     */
    protected void onCaptureViewGet(View view) {

    }

    /**
     * @param x
     * @param y
     */
    public void setLocation(int x, int y) {
        mCaptureLocX = x;
        mCaptureLocY = y;
        regulateLocation();
        requestLayout();
    }

    public void setBoundry(int w, int h) {
        this.mBoundryWidth = w;
        this.mBoundryHeight = h;
    }


    @SuppressWarnings("FieldCanBeLocal")
    private final ViewDragHelper.Callback DRAG_HELPER_CALLBACK = new ViewDragHelper.Callback() {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if(!dragHEanble){
                return 0;
            }
            left = Math.max(0, left);
            left = Math.min(getMeasuredWidth() - child.getMeasuredWidth(), left);
            return left;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            if(!dragVEanble){
                return 0;
            }
            top = Math.max(0, top);
            top = Math.min(getMeasuredHeight() - child.getMeasuredHeight(), top);
            return top;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int destX = releasedChild.getLeft();
            int destY = releasedChild.getTop();
            if (fAdsorptionEffectOn) {
                final int centerX = releasedChild.getWidth() / 2 + releasedChild.getLeft();
                final int centerY = releasedChild.getHeight() / 2 + releasedChild.getTop();
                destX = centerX > (getMeasuredWidth() / 2) ? getMeasuredWidth() - releasedChild.getMeasuredWidth() : 0;
                destY = releasedChild.getTop();
                if (centerY > (getMeasuredHeight() - releasedChild.getMeasuredHeight() * 1.5)) {
                    destY = getMeasuredHeight() - releasedChild.getMeasuredHeight();
                    if (adsorptionXEnable()) destX = releasedChild.getLeft();
                } else if (centerY < releasedChild.getMeasuredHeight() * 1.5) {
                    destY = 0;
                    if (adsorptionXEnable()) destX = releasedChild.getLeft();
                }
                onViewRelease(releasedChild, destX, destY);
            }
            mCaptureLocX = destX;
            mCaptureLocY = destY;
            if (mOnViewReleaseListener != null) mOnViewReleaseListener.onViewRelease(destX, destY);
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return dragHEanble && getCaptureView() == child ? child.getWidth() : 0;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return dragVEanble && getCaptureView() == child ? child.getHeight() : 0;
        }

    };

    protected boolean adsorptionXEnable() {
        return false;
    }

    /**
     * @param releasedChild
     * @param destX
     * @param destY
     */
    protected void onViewRelease(View releasedChild, int destX, int destY) {
        doViewRelease(destX, destY);
    }

    /**
     * @param destX
     * @param destY
     */
    private void doViewRelease(int destX, int destY) {
        mDragHelper.settleCapturedViewAt(destX, destY);
        invalidate();
    }

    /**
     * @return
     */
    protected View getCaptureView() {
        return mCaptureChild;
    }

    @Override
    public void computeScroll() {
        if (mDragHelper != null && mDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    /**
     * @return
     */
    @SuppressWarnings("FieldCanBeLocal")
    private final GestureDetector.SimpleOnGestureListener CLICK_GESTURE_LISTENER =
            new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            };

    /**
     *
     */
    public interface OnGestureEventListener {
        void onDrag(MotionEvent event, boolean valid);
    }

    /**
     * @param onDragListener
     */
    public void setOnGestureEventListener(OnGestureEventListener onDragListener) {
        this.mOnDragListener = onDragListener;
    }

    public void setOnViewReleaseListener(OnViewReleaseListener l) {
        this.mOnViewReleaseListener = l;
    }

    /**
     *
     */
    public interface OnViewReleaseListener {
        void onViewRelease(int x, int y);
    }

}
