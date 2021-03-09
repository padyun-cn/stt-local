package com.padyun.scripttools.biz.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by daiepngfei on 7/15/19
 */
public class CvPixelView extends View {

    public enum Direction {
        UP, DOWN, LEFT, RIGHT, NONE
    }

    private OnColorPickedListener mOnColorPickedListener;
    private float rows = 34.0F;
    private float cols = 34.0F;
    private Paint paint = new Paint();
    private Paint paintC = new Paint();
    private Paint paintBg = new Paint();
    private Paint paintCenterFrame = new Paint();
    private int colorTransGray = Color.parseColor("#CBCACB");
    private int colorTransWhite = Color.parseColor("#FEFEFE");
    private int pivotX = 0;
    private int pivotY = 0;

    private Path path = new Path();
    private Bitmap bitmap;
    private int mPickedColor;

    public CvPixelView(Context context) {
        super(context);
        init();
    }

    public CvPixelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);

        setLayerType(LAYER_TYPE_HARDWARE, paintC);

        paintC.setColor(Color.parseColor("#EDEDED"));
        paintC.setStrokeWidth(20F);
        paintC.setStyle(Paint.Style.STROKE);
        paintC.setAntiAlias(true);
        paintC.setDither(true);

        paintBg.setColor(colorTransGray);
        paintBg.setStyle(Paint.Style.FILL);
        paintC.setAntiAlias(true);

        paintCenterFrame.setColor(Color.parseColor("#6E5E5E"));
        paintCenterFrame.setStyle(Paint.Style.STROKE);
        paintCenterFrame.setAntiAlias(true);
        paintCenterFrame.setDither(true);
        paintCenterFrame.setStrokeWidth(2F);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final float hw = getMeasuredWidth() / 2;
        final float hh = getMeasuredHeight() / 2;
        path.reset();
        path.addCircle(hw, hh, hh, Path.Direction.CW);
        canvas.clipPath(path);
        final float h = getMeasuredWidth() / cols;
        final float w = getMeasuredHeight() / rows;
        for (int x = 0; x < cols; x++) {
            final float xStart = Math.min(w * x * 1.0f, getMeasuredWidth() * 1.0f - 1);
            for (int y = 0; y < cols; y++) {
                final float yStart = Math.min(h * y, getMeasuredHeight() * 1.0f - 1);
                paintBg.setColor((y % 2) == (x % 2) ? colorTransGray : colorTransWhite);
                canvas.drawRect(xStart, yStart, xStart + w, yStart + h, paintBg);
                if (bitmap != null && !bitmap.isRecycled()) {
                    final float bx = x + 1 + pivotX - cols / 2F;
                    final float by = y + 1 + pivotY - cols / 2F;
                    if (bx < 0 || by < 0 || bx >= bitmap.getWidth() || by >= bitmap.getHeight())
                        continue;
                    paintBg.setColor(bitmap.getPixel((int) bx, (int) by));
                    canvas.drawRect(xStart, yStart, xStart + w, yStart + h, paintBg);
                }
            }
        }

        for (int x = 0; x < cols; x++) {
            canvas.drawLine(0F, Math.min(h * x, getMeasuredHeight() - 1F), getMeasuredWidth(), Math.min(h * x, getMeasuredHeight() - 1F), paint);
            canvas.drawLine(Math.min(w * x, getMeasuredWidth() - 1), 0F, Math.min(w * x, getMeasuredWidth() - 1), getMeasuredHeight(), paint);
        }

        final float csx = (int) cols / 2 * w;
        final float csy = (int) cols / 2 * h;

        canvas.drawRect(csx, csy, csx + w, csy + h, paintCenterFrame);
        canvas.drawCircle(getMeasuredWidth() / 2F, getMeasuredHeight() / 2F, getMeasuredHeight() / 2F, paintC);
    }

    public void setPivot(int x, int y) {
        setPivot(x, y, false);
    }

    private void setPivot(int x, int y, boolean notify) {
        if (bitmap != null) {
            final float x1 = pivotX, y1 = pivotY;
            pivotY = Math.max(0, Math.min(y, bitmap.getHeight() - 1));
            pivotX = Math.max(0, Math.min(x, bitmap.getWidth() - 1));
            if (x1 != pivotX || y1 != pivotY) {
                mHandler.post(() -> notifyColorPicked(getPickedColor()));
                invalidate();
            }
        }
    }

    public int getPickedColor() {
        int color = Color.TRANSPARENT;
        if (bitmap != null && !bitmap.isRecycled()) {
            return bitmap.getPixel(pivotX, pivotY);
        }
        return color;
    }

    public Point getPixel() {
        return new Point(pivotX, pivotY);
    }


    public void moveBy(Direction dir, int v, OnMovingListner l) {
        int x = pivotX;
        int y = pivotY;
        switch (dir) {
            case UP:
                y -= v;
                break;
            case DOWN:
                y += v;
                break;
            case LEFT:
                x -= v;
                break;
            case RIGHT:
                x += v;
                break;
            default:
        }
        setPivot(x, y, true);
        if (l != null) {
            l.onMoving(x, y);
        }
    }


    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        invalidate();
    }

    public void setOnColorPickedListener(OnColorPickedListener onColorPickedListener) {
        this.mOnColorPickedListener = onColorPickedListener;
    }

    public interface OnColorPickedListener {
        void onColorPicked(int color);
    }


    private void notifyColorPicked(int pickedColor) {
        if (mOnColorPickedListener != null) mOnColorPickedListener.onColorPicked(pickedColor);
    }

    private Handler mHandler = new Handler();

   /* public void moveUpBy(int v) {
        moveBy(Direction.UP, v);
    }

    public void moveDownBy(int v) {
        moveBy(Direction.DOWN, v);
    }

    public void moveLeftBy(int v) {
        moveBy(Direction.LEFT, v);
    }

    public void moveRightBy(int v) {
        moveBy(Direction.RIGHT, v);
    }*/

    private OnMoveTouchListner mMoveGestureListener = new OnMoveTouchListner(this);


    private class OnMoveTouchListner implements OnTouchListener {

        private CvPixelView dropper;
        private Runnable onReleaseRunnalbe;
        public OnMovingListner onMovingListener;

        OnMoveTouchListner(CvPixelView dropper) {
            this.dropper = dropper;
        }

        private Direction dir = Direction.NONE;
        private int maxSpeed = 16;
        private static final int INTERVAL = 10;
        private Handler mMoveHandler = new Handler();
        private long mLongPressStartTime = 0L;
        private static final long DURATION = 1000L;
        private AccelerateInterpolator interpolator = new AccelerateInterpolator();

        @Override
        public boolean onTouch(View p0, MotionEvent p1) {
            switch (p1.getAction()) {
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    final long time = p1.getEventTime() - p1.getDownTime();
                    if (time < 250) onSingleTapUp();
                    release();
                    break;
                case MotionEvent.ACTION_DOWN:
                    onLongPress();
                    break;
                default:
            }

            return true;
        }

        private void onLongPress() {
            mLongPressStartTime = System.currentTimeMillis();
            moveContinuouslyWithMaxSpeed(300);
        }

        private void moveContinuouslyWithMaxSpeed(long t) {
            mMoveHandler.postDelayed(() -> {
                moveContinuouslyWithMaxSpeed(INTERVAL);
                final float rate = Math.min(1.0f, interpolator.getInterpolation((System.currentTimeMillis() - mLongPressStartTime) * 1.0f / DURATION));
                dropper.moveBy(dir, (int) (maxSpeed * rate), onMovingListener);
            }, t);
        }

        private void onSingleTapUp() {
            dropper.moveBy(dir, 1, onMovingListener);
        }

        private void release() {
            mMoveHandler.removeCallbacksAndMessages(null);
            if (onReleaseRunnalbe != null) onReleaseRunnalbe.run();
        }
    }

    public interface OnMovingListner {
        void onMoving(int x, int y);
    }


    public void handleMoving(View v, Direction dir, int maxSpeed, Runnable onRealseRunnable) {
        handleMoving(v, dir, maxSpeed, onRealseRunnable, null);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void handleMoving(View v, Direction dir, int maxSpeed, Runnable onRealseRunnable, OnMovingListner l) {
        v.setOnTouchListener((v1, event) -> {
            mMoveGestureListener.dir = dir;
            mMoveGestureListener.onMovingListener = l;
            mMoveGestureListener.onReleaseRunnalbe = onRealseRunnable;
            mMoveGestureListener.maxSpeed = maxSpeed;
            mMoveGestureListener.onTouch(v1, event);
            return true;
        });
    }

    public int getBitmapWidth(){
        return bitmap == null ? 0 : bitmap.getWidth();
    }

    public int getBitmapHeight(){
        return bitmap == null ? 0 : bitmap.getHeight();
    }

}
