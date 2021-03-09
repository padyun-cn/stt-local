package com.padyun.scripttools.biz.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import com.padyun.scripttools.common.utils.CoBitmapWorker;
import com.uls.utilites.ui.DensityUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


/**
 * Created by daiepngfei on 9/10/19
 */
public class CvBlurGuideView extends FrameLayout {
    private int outSR = 20;
    private int outDSR = 40;
    private int outDASH_W = 5;

    private Rect rect = null;
    private final Rect leftRect = new Rect(), topRect = new Rect(), rightRect = new Rect(), bottomRect = new Rect();
    private int bgColor = Color.parseColor("#7F000000");
    private Paint paint = new Paint();
    private Paint anchorPaint = new Paint();
    private Paint clickPaint = new Paint();
    private ViewConfiguration vc = ViewConfiguration.get(this.getContext());
    private final int touchSlop = vc.getScaledTouchSlop();
    private final int tapTimeOut = ViewConfiguration.getTapTimeout();
    private Path path = new Path();
    private float[] rcs = new float[]{20, 20, 20, 20, 20, 20, 20, 20};
    private Bitmap anchorBitmap;
    private Rect anchorBounds = new Rect();
    private OnClickListener onClickL;
    private Anchor anchor = Anchor.LEFT;

    public enum Anchor {
        LEFT, RIGHT, TOP, BOTTOM
    }

    public void setAnchorImageResouce(int res, int height, Anchor anchor) {
        this.anchor = anchor;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), res, options);
        int width = 0;
        final int w = options.outWidth;
        final int h = options.outHeight;
        if (h != 0) width = w / h * height;

        if (width > 0) {
            anchorBitmap = CoBitmapWorker.decodeSampledBitmapFromResource(getResources(), res, width, height);
            resetArounds();
        }


    }

    public void setCornerRadius(int radius) {
        outSR = radius;
        outDSR = radius * 2;
        for (int i = 0; i < rcs.length; i++) {
            rcs[i] = radius;
        }
        clickPaint.setPathEffect(new DashPathEffect(new float[]{outDSR, outSR}, 0));
    }

    public void setDashWidth(int width) {
        outDASH_W = width;
        clickPaint.setStrokeWidth(outDASH_W);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void show(final Rect rect, final OnClickListener l) {
        if (rect.equals(this.rect) || getVisibility() != View.VISIBLE) return;
        super.setOnTouchListener(new OnTouchListener() {
            private boolean isDowned;
            private long downTime;
            private float x, y;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = event.getX();
                        y = event.getY();
                        Rect rect = getRect();
                        if (rect != null && rect.contains((int) x, (int) y)) {
                            isDowned = true;
                            downTime = System.currentTimeMillis();
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (Math.abs(event.getX() - x) > touchSlop) {
                            isDowned = false;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        final long deltaTime = (System.currentTimeMillis() - downTime);
                        if (isDowned && deltaTime <= tapTimeOut) {
                            System.out.println("qunima - - - ");
                            if (onClickL != null) onClickL.onClick(CvBlurGuideView.this);
                        }
                        isDowned = false;
                        break;
                }
                return true;
            }
        });
        this.path.reset();
        //setVisibility(View.VISIBLE);
        this.rect = rect;
        this.onClickL = l;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            path.addRoundRect(rect.left, rect.top, rect.right, rect.bottom, rcs, Path.Direction.CW);
        }
        resetArounds();
        invalidate();
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        // super.setOnTouchListener(l);
    }

    public CvBlurGuideView(@NonNull Context context) {
        super(context);
        setWillNotDraw(false);
        init();
    }

    public void dismiss() {
        setVisibility(View.GONE);
        this.path.reset();
        this.rect = null;
    }

    private Rect getRect(){
        return rect;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        paint.setAntiAlias(true);
        clickPaint.setColor(Color.parseColor("#f1f1f1"));
        clickPaint.setStyle(Paint.Style.STROKE);
        clickPaint.setStrokeWidth(outDASH_W);
        clickPaint.setPathEffect(new DashPathEffect(new float[]{outDSR, outSR}, 0));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        resetArounds();
    }

    private void resetArounds() {
        if (rect != null && !rect.isEmpty()) {
            leftRect.set(0, 0, rect.left, getMeasuredHeight());
            topRect.set(rect.left, 0, rect.right, rect.top);
            rightRect.set(rect.right, 0, getMeasuredWidth(), getMeasuredHeight());
            bottomRect.set(rect.left, rect.bottom, rect.right, getMeasuredHeight());

            if (anchorBitmap != null) {
                final int width = anchorBitmap.getWidth();
                final int height = anchorBitmap.getHeight();
                final int margin = DensityUtils.dip2px(getContext(), 10) + outDASH_W;
                int left = 0, top = 0;
                switch (anchor) {
                    case LEFT:
                        left = rect.left - width - margin;
                        top = rect.top + rect.height() / 4;
                        break;
                    case TOP:
                        break;
                    case RIGHT:
                        left = rect.right + margin;
                        top = rect.top + rect.height() / 4;
                        break;
                    case BOTTOM:
                        break;
                    default:
                }

                anchorBounds.set(left, top, left + width, top + height);
                System.out.println("anchor bounds is " + anchorBounds.toShortString());
            }
        }

    }

    public CvBlurGuideView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (rect != null && !rect.isEmpty()) {
            if (!path.isEmpty()) canvas.clipPath(path, Region.Op.DIFFERENCE);
            canvas.drawColor(bgColor);
            canvas.drawRoundRect(rect.left - outDASH_W, rect.top - outDASH_W, rect.right + outDASH_W, rect.bottom + outDASH_W, outSR, outSR, clickPaint);
            if (anchorBitmap != null && !anchorBitmap.isRecycled()) {
                canvas.drawBitmap(anchorBitmap, anchorBounds.left, anchorBounds.top, anchorPaint);
            }
        } /*else {
            canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);
        }*/
    }

}
