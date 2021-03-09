package com.padyun.scripttools.biz.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.padyun.scripttools.R;
import com.uls.utilites.ui.DensityUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

/**
 * Created by daiepngfei on 10/9/19
 */
public class CvCropOffsetAndSlideLayout extends ViewGroup {

    private SampleView mFrameCrop = null;
    private SampleView mFrameOffset = null;
    private SampleView mButtonConfirm = null;
    private SampleView mButtonCancel = null;
    private SampleView mFrameSlideStart = null;
    private SampleView mFrameSlideEnd = null;
    private SampleView[] mTouchedFrames = null;
    private static final int MASK_COLOR = Color.argb(128, 0, 0, 0);
    private SampleView mTouchTarget;
    private Path mPath = new Path();

    private Paint mPaint = new Paint();
    private Paint mPaintStroke = new Paint();

    private final int fButtonSize = DensityUtils.dip2px(getContext(), 35);
    private final int fButtonDrawableSize = DensityUtils.dip2px(getContext(), 25);
    private final int fSlideSize = DensityUtils.dip2px(getContext(), 50);

    public static final int MODE_OFFSET = 0;
    public static final int MODE_SLIDE = 1;
    private RectF lBounds = new RectF();
    private Rect mAnchorRect = new Rect();
    private int mBitmapWidth, mBitmapHeight;


    public CvCropOffsetAndSlideLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public CvCropOffsetAndSlideLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        Frame crop = new Frame(getContext());
        crop.setBorderlineColor(Color.RED);
        mFrameCrop = crop;

        Frame offset = new Frame(getContext(), DensityUtils.dip2px(getContext(), 50));
        offset.setBorderlineColor(Color.GREEN);
        mFrameOffset = offset;

        mFrameSlideStart = new SDrawable(getContext());
        initDefaultDrawable((SDrawable) mFrameSlideStart, R.drawable.ic_script_crop_option_slide_start, fSlideSize, fSlideSize);
        mFrameSlideEnd = new SDrawable(getContext());
        initDefaultDrawable((SDrawable) mFrameSlideEnd, R.drawable.ic_script_crop_option_slide_end, fSlideSize, fSlideSize);
        mButtonConfirm = new SDrawableButton(getContext());
        initDefaultDrawable((SDrawable) mButtonConfirm, R.drawable.ic_script_crop_option_done, fButtonDrawableSize, fButtonSize);
        mButtonCancel = new SDrawableButton(getContext());
        initDefaultDrawable((SDrawable) mButtonCancel, R.drawable.ic_script_crop_option_close, fButtonDrawableSize, fButtonSize);

        mTouchedFrames = new SampleView[]{mFrameCrop, mFrameOffset, mFrameSlideStart, mFrameSlideEnd, mButtonConfirm, mButtonCancel};
        setWillNotDraw(false);
        mPaint.setColor(MASK_COLOR);
        mPaint.setAntiAlias(true);

        mPaintStroke.setAntiAlias(true);
        mPaintStroke.setStrokeWidth(1);
        mPaintStroke.setStyle(Paint.Style.STROKE);
    }

    @NonNull
    private void initDefaultDrawable(SDrawable view, int resId, int drawableSize, int viewSize) {
        view.bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), resId), drawableSize, drawableSize, true);
        view.rect.set(0, 0, viewSize, viewSize);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                final SampleView[] frameArray = mTouchedFrames;
                for (int i = frameArray.length - 1; i > 0; i--) {
                    final SampleView f = frameArray[i];
                    if (f == null || !f.isEnabled() || !f.contains((int) ev.getX(), (int) ev.getY())) {
                        continue;
                    }
                    if (f.onTouchEvent(ev)) {
                        mTouchTarget = f;
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mTouchTarget != null) {
                    mTouchTarget.onTouchEvent(ev);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mTouchTarget != null) {
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
        foreachTouchRects(f -> f.layout(f.rect.left, f.rect.top, f.rect.right, f.rect.bottom));
    }

    private void foreachTouchRects(Consumer<SampleView> consumer) {
        if (consumer == null) return;
        for (SampleView f : mTouchedFrames) {
            if (f == null || f.rect == null || f.rect.isEmpty()) continue;
            consumer.accept(f);
        }
    }

    private void addViewRectToPath(SampleView view) {
        mPath.addRect(view.rect.left, view.rect.top, view.rect.right, view.rect.bottom, Path.Direction.CW);
    }

    private void addViewCircleToPath(SampleView view) {
        mPath.addCircle(view.rect.centerX(), view.rect.centerY(), view.rect.width() / 2, Path.Direction.CW);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaintStroke.setColor(Color.GREEN);
        canvas.drawRect(lBounds, mPaintStroke);
        canvas.save();
        canvas.clipPath(mPath, Region.Op.DIFFERENCE);
        canvas.drawColor(MASK_COLOR);
        canvas.restore();
        drawSample(canvas, mFrameCrop);
        drawSample(canvas, mFrameOffset);
        drawSample(canvas, mFrameSlideStart);
        drawSample(canvas, mFrameSlideEnd);
        drawSample(canvas, mButtonConfirm);
        drawSample(canvas, mButtonCancel);
    }

    protected void drawSample(Canvas canvas, SampleView child) {
        if (child.isEnabled()) {
            /*canvas.saveSync();
            canvas.clipRect(child.rect);
            canvas.translate(child.rect.left, child.rect.top);
            canvas.restore();*/
            child.draw(canvas);
        }
    }

    /**
     *
     */
    private void updatePath() {
        mPath.reset();
        addViewRectToPath(mFrameCrop);

        if (mFrameOffset.isEnabled()) {
            addViewRectToPath(mFrameOffset);
            final Rect anchorRect = mFrameOffset.rect;
            updateConfirmationDrawables(anchorRect);
        }

        if (mFrameSlideStart.isEnabled()) {
            addViewCircleToPath(mFrameSlideStart);
        }

        if (mFrameSlideEnd.isEnabled()) {
            addViewCircleToPath(mFrameSlideEnd);
            final Rect anchorRect = mFrameSlideEnd.rect;
            updateConfirmationDrawables(anchorRect);
        }
        postInvalidate();
    }

    private void updateConfirmationDrawables(Rect anchorRect) {
        final RectF rect = lBounds;
        if (rect != null && !rect.isEmpty()) {
            int x, y;
            final int popupWindowMargin = DensityUtils.dip2px(getContext(), 10);
            final int popupWindowWidth = fButtonSize;
            final int popupWindowHeight = fButtonSize * 2;

            if (rect.right - anchorRect.right > popupWindowWidth + popupWindowMargin) {
                x = anchorRect.right + popupWindowMargin;
            } else if (anchorRect.left - rect.left > popupWindowWidth + popupWindowMargin) {
                x = anchorRect.left - popupWindowWidth - popupWindowMargin;
            } else {
                x = (int) rect.left;
            }

            if (rect.bottom - anchorRect.centerY() > popupWindowHeight / 2F) {
                y = Math.max(anchorRect.centerY() - popupWindowHeight / 2, 0);
            } else /*if(anchorRect.top - rect.top > popupWindowHeight)*/ {
                y = Math.max(anchorRect.centerY() - popupWindowHeight / 2, getBottom() - popupWindowHeight);
            }
            mButtonConfirm.updateRect(x, y, x + fButtonSize, y + fButtonSize);
            mButtonCancel.updateRect(x, y + fButtonSize, x + fButtonSize, y + fButtonSize * 2);
        }
    }


    /**
     * @param l
     * @param t
     * @param r
     * @param b
     */
    public void setCropRect(int l, int t, int r, int b) {
        setRect(mFrameCrop, l, t, r, b);
    }

    /**
     * @param l
     * @param t
     * @param r
     * @param b
     */
    public void setOffsetRect(int l, int t, int r, int b) {
        setRect(mFrameOffset, l, t, r, b);

    }

    /**
     * @param l
     * @param t
     * @param r
     * @param b
     */
    public void setSlideStartRect(int l, int t, int r, int b) {
        setSlideRect(mFrameSlideStart, l, t, r, b);
    }

    /**
     * @param view
     * @param l
     * @param t
     * @param r
     * @param b
     */
    private void setSlideRect(SampleView view, int l, int t, int r, int b) {
        final int centerX = (r + l) / 2;
        final int centerY = (t + b) / 2;
        final int offsetX = centerX - view.rect.centerX();
        final int offsetY = centerY - view.rect.centerY();
        /*final int x = CUtils.limitInRange(centerX - fButtonSize, 0, getMeasuredWidth() - fButtonSize * 2);
        final int y = CUtils.limitInRange(centerY - fButtonSize, 0, getMeasuredHeight() - fButtonSize * 2);
        if(view.rect.isEmpty()){
            view.rect.set(x, y, x + fButtonSize * 2, y + fButtonSize * 2);
        }*/
        // final int offsetFinal = (int) (fSlideSize / 12F * 5);
        // view.rect.offset(offsetX - offsetFinal, offsetY - offsetFinal);
        view.rect.offset(offsetX, offsetY);
        view.relayout();
    }

    /**
     * @param onConfirm
     * @param onCancel
     */
    public void setButtonsClickListeners(View.OnClickListener onConfirm, View.OnClickListener onCancel) {
        mButtonConfirm.setOnClickListener(onConfirm);
        mButtonCancel.setOnClickListener(onCancel);
    }

    /**
     * @param l
     * @param t
     * @param r
     * @param b
     */
    public void setSlideEndRect(int l, int t, int r, int b) {
        setSlideRect(mFrameSlideEnd, l, t, r, b);
    }

    /**
     * @return
     */
    public Rect getOffsetRect() {
        return mFrameOffset.rect;
    }

    /**
     *
     * @return
     */
    public Point getOffsets(){
        return new Point(mFrameOffset.rect.centerX() - mFrameCrop.rect.centerX(), mFrameOffset.rect.centerY() - mFrameCrop.rect.centerY());
    }

    /**
     * @return
     */
    public Pair<Rect, Rect> getImageSlideRects() {
        final int size = (int) (fSlideSize / 12f);
        Rect first = new Rect(mFrameSlideStart.rect);
        first.offset(-(int)lBounds.left, -(int)lBounds.top);
        Rect firstFinal  = new Rect(first.centerX() - size, first.centerY() - size, first.centerX() + size, first.centerY() + size);
        Rect second = new Rect(mFrameSlideEnd.rect);
        second.offset(-(int)lBounds.left, -(int)lBounds.top);
        Rect secondFinal  = new Rect(second.centerX() - size, second.centerY() - size, second.centerX() + size, second.centerY() + size);
        return new Pair<>(firstFinal, secondFinal);
    }

    /**
     * @param l
     * @param t
     * @param r
     * @param b
     */
    private void setRect(SampleView f, int l, int t, int r, int b) {
        f.updateRect(l, t, r, b);
//        f.rect.set(l, t, r, b);
//        f.layout(l, t, r, b);
        updatePath();
    }

    /**
     * @param mode
     */
    public void switchMode(int mode) {
        mFrameOffset.setEnabled(mode == MODE_OFFSET);
        mFrameSlideStart.setEnabled(mode == MODE_SLIDE);
        mFrameSlideEnd.setEnabled(mode == MODE_SLIDE);
        updatePath();
    }

    public void setImageBounds(RectF imageBounds) {
        this.lBounds.set(imageBounds);
        this.mFrameCrop.setBoundary(imageBounds);
        this.mFrameOffset.setBoundary(imageBounds);
        final int size = (int) (fSlideSize / 12f * 5);
        final RectF slideBoundary = new RectF(imageBounds.left - size, imageBounds.top - size, imageBounds.right + size, imageBounds.bottom + size);
        mFrameSlideStart.setBoundary(slideBoundary);
        mFrameSlideEnd.setBoundary(slideBoundary);
    }

    public RectF getImageBounds() {
        return lBounds;
    }

    /**
     *
     */
    public class SDrawableButton extends SDrawable {

        private GestureDetector mGestureDetector;

        public SDrawableButton(Context context) {
            super(context);
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    performClick();
                    return true;
                }

                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }
            });
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            mGestureDetector.onTouchEvent(event);
            return true;
        }


    }

    public class SDrawable extends SampleView {
        public Bitmap bitmap;
        private Paint paint;

        public SDrawable(Context context) {
            super(context);
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.GRAY);
        }

        @Override
        public boolean contains(int x, int y) {
            return new Rect(getLeft(), getTop(), getRight(), getBottom()).contains(x, y);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (bitmap != null && !bitmap.isRecycled()) {
                canvas.drawBitmap(bitmap, rect.left + (rect.width() - bitmap.getWidth()) / 2, rect.top + (rect.height() - bitmap.getHeight()) / 2, paint);
            }
        }
    }

    /**
     *
     */
    public class Frame extends SampleView {
        private Paint paint;
        private Paint paint1;

        public Frame(Context context) {
            this(context, 0);
        }

        public Frame(Context context, int minTouchSize) {
            super(context, minTouchSize);
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(1);
            paint.setStyle(Paint.Style.STROKE);
            /*paint1 = new Paint();
            paint1.setAntiAlias(true);
            paint1.setStrokeWidth(1);
            paint1.setStyle(Paint.Style.STROKE);
            paint1.setColor(Color.RED);*/
        }

        public void setBorderlineColor(int color) {
            paint.setColor(color);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            //canvas.drawRect(touchedRect, paint1);
            canvas.drawRect(rect, paint);
        }

        @Override
        public boolean contains(int x, int y) {
            return super.contains(x, y);
        }
    }

    public class SampleView extends View {
        public Rect rect = new Rect();
        public Rect touchedRect = new Rect();
        public RectF boundary = new RectF();

        public SampleView(Context context) {
            this(context, 0);
        }

        public SampleView(Context context, int minTouchSize) {
            super(context);
            this.minTouchSize = minTouchSize;
        }

        private float mPointerDownX = -1;
        private float mPointerDownY = -1;

        private final int minTouchSize;


        public void updateRect(int l, int t, int r, int b) {
            rect.set(l, t, r, b);
            touchedRect.set(rect);
            if(minTouchSize > 0) {
                final int size = minTouchSize;
                final int hs = (int) (minTouchSize / 2f);
                if (touchedRect.width() <= size) {
                    touchedRect.set(touchedRect.left - hs, touchedRect.centerY(), touchedRect.right + hs, touchedRect.centerY());
                }
                if (touchedRect.height() <= size) {
                    touchedRect.set(touchedRect.left, touchedRect.centerY() - hs, touchedRect.right, touchedRect.centerY() + hs);
                }
            }
            //layout(l, t, r, b);
            layout(touchedRect.left, touchedRect.top, touchedRect.right, touchedRect.bottom);
        }

        public boolean onViewTouchEvent(MotionEvent event) {
            return super.onTouchEvent(event);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mPointerDownX = event.getX();
                    mPointerDownY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    final int offsetX = Math.min(Math.max((int) (event.getX() - mPointerDownX), (int) (getBoundary().left - rect.left)), (int) (getBoundary().right - rect.right));
                    final int offsetY = Math.min(Math.max((int) (event.getY() - mPointerDownY), (int) (getBoundary().top - rect.top)), (int) (getBoundary().bottom - rect.bottom));

                    mPointerDownX = event.getX();
                    mPointerDownY = event.getY();
                    rect.offset(offsetX, offsetY);
                    touchedRect.offset(offsetX, offsetY);
                    System.out.println("nimadeaaaa:: " + offsetX + "/" + offsetY);
                    relayout();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mPointerDownY = mPointerDownX = -1;
                    break;
                default:
            }
            return true;
        }

        public RectF getBoundary(){
            return boundary;
        }

        public void setBoundary(RectF rectF){
            this.boundary = rectF;
        }

        private void relayout() {
            layout(rect.left, rect.top, rect.right, rect.bottom);
            rInavlidate();
        }

        public boolean contains(int x, int y) {
            return touchedRect != null && !touchedRect.isEmpty() && touchedRect.contains(x, y);
        }
    }

    private void rInavlidate() {
        updatePath();
        postInvalidate();
    }

}
