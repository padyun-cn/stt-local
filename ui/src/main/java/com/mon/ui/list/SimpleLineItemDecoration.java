package com.mon.ui.list;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by daiepngfei on 2020-11-16
 */
public class SimpleLineItemDecoration extends RecyclerView.ItemDecoration{
    private int color;
    private int height;
    private int marginLeft;
    private int marginRight;
    private RecyclerView.Adapter adapter;
    private boolean isNeedTopDivider;
    private final Paint dividerPaint = new Paint();

    public SimpleLineItemDecoration(RecyclerView.Adapter adapter, int color, int height){
        this.adapter = adapter;
        this.height = height;
        this.color = color;
        /*final Paint shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        shadowPaint.setColor(getShadowLineColor());*/
        dividerPaint.setAntiAlias(true);
        dividerPaint.setColor(color);
    }

    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
    }

    public void setMarginRight(int marginRight) {
        this.marginRight = marginRight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (isNeedTopDivider || (/*!adapter.isFooterView(view) && */parent.getChildAdapterPosition(view) != 0)) {
            outRect.top = height;
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
            final int left = parent.getPaddingLeft();
            final int right = parent.getWidth() - parent.getPaddingRight();
            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);
                if (/*!getAdapter().isFooterView(child) && */height >= 1) {
                    /*if (isNeedShadowLine()) {
                        c.drawLine(left, child.getTop() - lineHeight, right, child.getTop(), shadowPaint);
                        c.drawLine(left, child.getBottom(), right, child.getBottom() + lineHeight, shadowPaint);
                    }*/
                    c.drawRect(left + marginLeft, child.getTop() - height, right - marginRight, child.getTop(), dividerPaint);
                }
            }
        }
}
