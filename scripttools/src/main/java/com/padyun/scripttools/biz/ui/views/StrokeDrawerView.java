package com.padyun.scripttools.biz.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.uls.utilites.un.Useless;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daiepngfei on 8/9/19
 */
public class StrokeDrawerView extends View {

    private List<RectF> drawingRect = new ArrayList<>();
    private int aw, ah;
    private Paint paint = new Paint();
    private boolean isShowFrames;

    public StrokeDrawerView(Context context) {
        super(context);
        init();
    }

    public StrokeDrawerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isShowFrames && !Useless.isEmpty(drawingRect)) {
            Useless.foreach(drawingRect, t -> {
                canvas.drawRect(t, paint);
            });
        }
    }

    public void setApstioWH(int w, int h) {
        this.aw = w;
        this.ah = h;
    }

    public android.graphics.Rect getRecoveryARect(RectF rect) {
        final int x = (int) (rect.left * aw / getMeasuredWidth());
        final int y = (int) (rect.top * ah / getMeasuredHeight());
        final int r = (int) (rect.right * aw / getMeasuredWidth());
        final int b = (int) (rect.bottom * ah / getMeasuredHeight());
        return new android.graphics.Rect(x, y, r, b);
    }

    public void addRect(org.opencv.core.Rect rect) {
        final float x = rect.x * 1.0f / aw * getMeasuredWidth();
        final float y = rect.y * 1.0f / ah * getMeasuredHeight();
        final float r = (rect.x + rect.width) * 1.0f / aw * getMeasuredWidth();
        final float b = (rect.y + rect.height) * 1.0f / ah * getMeasuredHeight();
        drawingRect.add(new RectF(x, y, r, b));
    }


    public boolean isShowFrames() {
        return isShowFrames;
    }

    public void setShowFrames(boolean showFrames) {
        isShowFrames = showFrames;
    }

}
