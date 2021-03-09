package com.mon.ui.buildup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageView;

import com.mon.ui.R;

import androidx.annotation.Nullable;

/**
 * Created by daiepngfei on 2019-12-31
 */
@SuppressLint("AppCompatCustomView")
public class CvImageCheckbox extends ImageView implements Checkable {

    private boolean mIsChecked;
    private boolean mIsBroadcasting;
    private OnCheckedChangeListener mOnCheckedChangeListener;

    private static final int[] CHECKED_STATE_SET = {
            android.R.attr.state_checked
    };

    public CvImageCheckbox(Context context) {
        super(context);
    }

    public CvImageCheckbox(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CvImageCheckbox);
        final Drawable d = a.getDrawable(R.styleable.CvImageCheckbox_checkedSelector);
        if(d != null){
            setImageDrawable(d);
        }
        mIsChecked = a.getBoolean(R.styleable.CvImageCheckbox_checked, false);
        a.recycle();
        setChecked(mIsChecked);
        setClickable(true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }


    public void setCheckWithoutNotifing(boolean checked){
        if (mIsChecked != checked) {
            mIsChecked = checked;
            refreshDrawableState();
        }
    }

    @Override
    public void setChecked(boolean checked) {
        if (mIsChecked != checked) {
            mIsChecked = checked;
            refreshDrawableState();

            // Avoid infinite recursions if setChecked() is called from a listener
            if (mIsBroadcasting) {
                return;
            }
            mIsBroadcasting = true;
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(this, checked);
            }

            mIsBroadcasting = false;
        }
    }

    @Override
    public boolean performClick() {
        toggle();
        return super.performClick();
    }

    @Override
    public boolean isChecked() {
        return mIsChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mIsChecked);
    }


    /**
     * Interface definition for a callback to be invoked when the checked state of a compound button
     * changed.
     */
    public interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param buttonView
         *         The compound button view whose state has changed.
         * @param isChecked
         *         The new checked state of buttonView.
         */
        void onCheckedChanged(CvImageCheckbox buttonView, boolean isChecked);
    }


    public void setOnCheckedChangeListener(OnCheckedChangeListener l) {
        this.mOnCheckedChangeListener = l;
    }

}
