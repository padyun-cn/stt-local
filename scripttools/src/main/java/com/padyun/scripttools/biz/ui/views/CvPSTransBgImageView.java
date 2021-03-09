package com.padyun.scripttools.biz.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.uls.utilites.ui.DensityUtils;

import androidx.annotation.Nullable;


/**
 * Created by daiepngfei on 7/17/19
 */
@SuppressLint("AppCompatCustomView")
public class CvPSTransBgImageView extends ImageView {
    private float size = DensityUtils.dip2px(getContext(), 12);
    private Paint paintBg = new Paint();
    private int colorTransGray = Color.parseColor("#CBCACB");
    private int colorTransWhite = Color.parseColor("#FEFEFE");
    public CvPSTransBgImageView(Context context) {
        super(context);
        paintBg.setColor(colorTransGray);
        paintBg.setStyle(Paint.Style.FILL);
    }

    public CvPSTransBgImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paintBg.setColor(colorTransGray);
        paintBg.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int cols = (int) (getMeasuredWidth() / size);
        final int rows = (int) (getMeasuredHeight() / size);
        for (int x = 0; x <= cols; x++) {
            final float xStart = Math.min(size * x * 1.0f, getMeasuredWidth() * 1.0f - 1);
            for (int y = 0; y <= rows; y++) {
                final float yStart = Math.min(size * y, getMeasuredHeight() * 1.0f - 1);
                paintBg.setColor((y % 2) == (x % 2) ? colorTransGray : colorTransWhite);
                canvas.drawRect(xStart, yStart, Math.min(getMeasuredWidth(), xStart + size), Math.min(getMeasuredHeight(), yStart + size), paintBg);
            }
        }
        super.onDraw(canvas);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
    }
}
