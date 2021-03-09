package com.padyun.scripttools.biz.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.util.Consumer;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Created by daiepngfei on 10/9/19
 */
public class CvCropOffsetAndSlideLayoutBK extends ViewGroup {

    private LinkedHashMap<String, Frame> mInterceptTouchRects = new LinkedHashMap<>();
    private static final int MASK_COLOR = Color.argb(128, 0, 0, 0);
    private Frame mTouchTarget;
    private Path mPath = new Path();
    private Paint mPaint = new Paint();

    public CvCropOffsetAndSlideLayoutBK(@NonNull Context context) {
        super(context);
        init();
    }

    public CvCropOffsetAndSlideLayoutBK(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        mPaint.setColor(MASK_COLOR);
        mPaint.setAntiAlias(true);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                final Collection<Frame> frames = mInterceptTouchRects.values();
                final Frame[] frameArray = new Frame[frames.size()];
                frames.toArray(frameArray);
                for (int i = frameArray.length - 1; i > -1; i--) {
                    final Frame f = frameArray[i];
                    if (f == null || !f.enable || f.rect == null || f.rect.isEmpty() ||
                            !f.rect.contains((int)ev.getX(), (int)ev.getY()) || !f.enableDragging) {
                        continue;
                    }
                    if (f.onTouchEvent(ev)) {
                        mTouchTarget = f;
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(mTouchTarget != null){
                    mTouchTarget.onTouchEvent(ev);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if(mTouchTarget != null){
                    mTouchTarget.onTouchEvent(ev);
                    mTouchTarget = null;
                }
                break;
                default:
        }
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        foreachTouchRects(f -> {
            f.layout(f.rect.left, f.rect.top, f.rect.right, f.rect.bottom);
        });
    }

    private void foreachTouchRects(Consumer<Frame> consumer) {
        if (consumer == null) return;
        final Set<String> keySet = mInterceptTouchRects.keySet();
        for (String key : keySet) {
            Frame f = mInterceptTouchRects.get(key);
            if (f == null || f.rect == null || f.rect.isEmpty()) continue;
            consumer.accept(f);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPath.reset();
        foreachTouchRects(t -> {
            mPath.addRect(t.rect.left, t.rect.top, t.rect.right, t.rect.bottom, Path.Direction.CW);
            t.draw(canvas);
        });
        canvas.save();
        canvas.clipPath(mPath, Region.Op.DIFFERENCE);
        canvas.drawColor(MASK_COLOR);
        canvas.restore();
    }

    /**
     * @param key
     * @param l
     * @param t
     * @param r
     * @param b
     * @param borderColor
     * @param enableDragging
     */
    public void addFrame(String key, int l, int t, int r, int b, int borderColor, boolean enableDragging) {
        Frame f = this.mInterceptTouchRects.get(key);
        if (f == null) f = new Frame(getContext());
        f.rect.set(l, t, r, b);
        f.layout(l, t, r, b);
        f.borderlineColor = borderColor;
        f.enableDragging = enableDragging;
        f.parent = this;
        f.enable = true;
        this.mInterceptTouchRects.put(key, f);
    }

    public void release(){
        foreachTouchRects(t -> t.enable = false);
    }

    /**
     *
     */
    public static class Frame extends View {
        public Rect rect = new Rect();
        public int borderlineColor;
        public boolean enableDragging;
        public boolean enable;
        private Paint paint;
        public ViewGroup parent;

        public Frame(Context context) {
            super(context);
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(1);
            paint.setStyle(Paint.Style.STROKE);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            paint.setColor(borderlineColor);
            canvas.drawRect(getLeft(), getTop(), getRight(), getBottom(), paint);
        }

        private float mPointerDownX;
        private float mPointerDownY;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (enableDragging) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mPointerDownX = event.getX();
                        mPointerDownY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        final int offsetX = (int) (event.getX() - mPointerDownX);
                        final int offsetY = (int) (event.getY() - mPointerDownY);
                        mPointerDownX = event.getX();
                        mPointerDownY = event.getY();
                        rect.offset(offsetX, offsetY);
                        layout(rect.left, rect.top, rect.right, rect.bottom);
                        parent.postInvalidate();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        mPointerDownY = mPointerDownX = 0;
                        break;
                    default:
                }
                return true;
            }
            return false;
        }
    }

}
