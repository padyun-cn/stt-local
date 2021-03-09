package com.mon.ui.buildup;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mon.ui.R;
import com.uls.utilites.ui.DensityUtils;
import com.uls.utilites.un.Useless;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by daiepngfei on 10/30/17
 * <p>
 * TODO ### bad code in this class need to rejust some day ###
 */

public class CoreCvSimpleWheelSelector extends RecyclerView {
    private int mChildHeight;
    private Paint mPaintFrame = new Paint();
    private Paint mPaintBoard = new Paint();
    private OnItemSelectedListener mOnItemSelectedListener;
    private List<String> mData;
    private List<String> mTime;
    private LinearLayoutManager mLayoutManager;
    private Context contexts;

    @SuppressWarnings("unused")
    public CoreCvSimpleWheelSelector(Context context, int firstChildHeight) {
        super(context);
        contexts = context;
        mChildHeight = DensityUtils.dip2px(context, firstChildHeight);
        initialize();
    }

    public CoreCvSimpleWheelSelector(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        contexts = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CoreCvSimpleWheelSelector);
        mChildHeight = (int) a.getDimension(R.styleable.CoreCvSimpleWheelSelector_child_height, 0);
        mPaintFrame.setColor(a.getColor(R.styleable.CoreCvSimpleWheelSelector_selection_frame_color, Color.parseColor("#999999")));
        mPaintBoard.setColor(a.getColor(R.styleable.CoreCvSimpleWheelSelector_selection_mask_color, Color.parseColor("#0ae1e1e1")));
        a.recycle();
        initialize();
    }

    private void initialize() {
        mPaintBoard.setAntiAlias(true);
        mPaintBoard.setStyle(Paint.Style.FILL);
        mPaintFrame.setAntiAlias(true);
        mPaintFrame.setStyle(Paint.Style.STROKE);
        mPaintFrame.setStrokeWidth(1);
        mLayoutManager = new LinearLayoutManager(getContext());
        setLayoutManager(mLayoutManager);
        setSelectBase();
    }

    private void setSelectBase() {
        addOnScrollListener(new OnScrollListener() {
            private View mTagView;
            private int mState;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                final int count = getChildCount();
                final int vcenter = getMeasuredHeight() / 2;
                boolean setted = mState != newState && newState == SCROLL_STATE_IDLE;
                if (setted) {
                    for (int i = 0; i < count; i++) {
                        final View child = getChildAt(i);
                        final int centerChild = (child.getTop() + child.getMeasuredHeight() / 2);
                        if ((vcenter >= child.getTop() && vcenter <= child.getBottom())) {
                            recyclerView.smoothScrollBy(0, -(vcenter - centerChild));
                            performSelected(mLayoutManager.getPosition(child));
                            if (child instanceof TextView) {
                                setAllChildrenTexts(child);
                            }
                        }
                    }
                }
                mState = newState;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                final int count = getChildCount();
                final int vcenter = getMeasuredHeight() / 2;
                for (int i = 0; i < count; i++) {
                    final View child = getChildAt(i);
                    if (!RelativeLayout.class.isInstance(child)) continue;
                    if ((vcenter >= child.getTop() && vcenter <= child.getBottom())) {
                        if (child != mTagView) {
                            setAllChildrenTexts(mTagView);
                            setAllChildrenTexts(child, true);
                            mTagView = child;
                        }
                    }
                }
            }

            private void setAllChildrenTexts(View parent) {
                setAllChildrenTexts(parent, false);
            }

            private void setAllChildrenTexts(View parent, boolean scale) {
                if (RelativeLayout.class.isInstance(parent)) {
                    int textColor = Color.parseColor("#666666");
                    int textSize = 12;
                    int textBgColor = Color.WHITE;
                    if (scale) {
                        textColor = Color.parseColor("#222222");
                        textSize = 14;
                        textBgColor = Color.parseColor("#EDEDED");
                    }
                    setChildText(parent, R.id.text, textColor, textSize, textBgColor);
                }
            }

            private void setChildText(View parent, int textViewId, int textColor, int textSize, int textBgColor) {
                if (parent == null) return;
                final View view = parent.findViewById(textViewId);
                if (TextView.class.isInstance(view)) {
                    final TextView textView = (TextView) view;
                    textView.setTextColor(textColor);
                    textView.setTextSize(textSize);
                }
            }
        });

    }

    private ItemDecoration mItemDecoration = new ItemDecoration() {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            super.getItemOffsets(outRect, view, parent, state);
            final int position = parent.getChildAdapterPosition(view);
            if (position == 0) {
                final int ch = view.getMeasuredHeight() == 0 ? mChildHeight : view.getMeasuredHeight();
                outRect.top = (parent.getMeasuredHeight() - parent.getPaddingTop() - parent.getPaddingBottom() - ch) / 2;
            }
            if (position == state.getItemCount() - 1) {
                outRect.bottom = (parent.getMeasuredHeight() - parent.getPaddingTop() - parent.getPaddingBottom() - view.getMeasuredHeight()) / 2;
            }
        }
    };

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        removeItemDecoration(mItemDecoration);
        addItemDecoration(mItemDecoration);
    }

    private void performSelected(int position) {
        if (mOnItemSelectedListener != null) {
            mOnItemSelectedListener.onItemSelected(Useless.isIndexesValid(mData, position) ? "" : mData.get(position), position);
        }
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        View child = getChildAt(0);
        if (child != null) {
            c.drawRect(0, (getMeasuredHeight() - child.getMeasuredHeight()) / 2,
                    getMeasuredWidth(), (getMeasuredHeight() + child.getMeasuredHeight()) / 2, mPaintBoard);
//            c.drawRect(0, (getMeasuredHeight() - child.getMeasuredHeight()) / 2,
//                    getMeasuredWidth(), (getMeasuredHeight() + child.getMeasuredHeight()) / 2, mPaintFrame);
        }
    }

    public void init(List<String> data, OnItemSelectedListener l) {
        if (data != null && data.size() > 0) {
            setAdapter(new StringAdapter(data));
            this.mData = data;
            this.mOnItemSelectedListener = l;
            //setSelection(selection);
            performSelected(0);
        }
    }


    public void setOnItemSelectedListener(OnItemSelectedListener l) {
        this.mOnItemSelectedListener = l;
    }

    public interface OnItemSelectedListener {
        void onItemSelected(String text, int position);
    }

    private class StringAdapter extends Adapter<MainHolder> {
        private List<String> data;

        StringAdapter(List<String> data) {
            this.data = data;
        }


        @Override
        public MainHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MainHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_core_selectors_dialog, parent, false));
        }

        @Override
        public void onBindViewHolder(MainHolder holder, int position) {
            final int p = position;
            holder.text.setText(data.get(p));
            holder.text.setOnClickListener(v -> setSelection(p));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private class MainHolder extends ViewHolder {
        TextView text;

        MainHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
        }
    }

    public void setSelection(int p) {
        setSelection(p, true);
    }

    public void setSelection(int p, boolean smooth) {
        if(smooth) smoothScrollToPosition(p);
        else scrollToPosition(p);
        if (mLayoutManager != null && getAdapter() != null && getAdapter().getItemCount() > 1) {
            if(p > 0 && p < getAdapter().getItemCount() - 1) {
                new Handler().post(() -> {
                    final View child = mLayoutManager.findViewByPosition(p);
                    if(child != null) {
                        final int centerChild = (child.getTop() + child.getMeasuredHeight() / 2);
                        CoreCvSimpleWheelSelector.this.smoothScrollBy(0, -(getMeasuredHeight() / 2 - centerChild));
                    }
                });
            }
        }
    }
}
