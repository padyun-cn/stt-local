package com.mon.ui.buildup;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;

import com.mon.ui.R;
import com.uls.utilites.un.Useless;


/**
 * Created by daiepngfei on 7/1/19
 */
public class IntegerNumberInput extends androidx.appcompat.widget.AppCompatEditText {
    private TextWatcher mTextWatcher;
    private OnNumberChangedListener mOnNumberChangedListener;
    private final int SRC_HEIGHT_5_4;
    private Integer mMaxValue;
    private TextWatcher mMainWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (mTextWatcher != null) mTextWatcher.beforeTextChanged(charSequence, i, i1, i2);
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            // To prevent recursively callback on this
            removeTextChangedListener(this);

            // get string value of charSequence
            String s = charSequence.toString();
            // remove all '0's at the begining
            int selectionStart = getSelectionStart();
            if (s.startsWith("0")) {
                StringBuilder b = new StringBuilder();
                boolean breakZero = true;
                char[] chars = s.toCharArray();
                for (char c : chars) {
                    if (c == '0' && breakZero) {
                        // Remember to decount the selection start index
                        selectionStart--;
                        continue;
                    }
                    if (breakZero) {
                        breakZero = false;
                    }
                    b.append(c);
                }
                s = b.toString();
            }

            // limit selection start
            selectionStart = Math.max(0, selectionStart);

            if (s.length() == 0) {
                resetText("0");
                setSelection(1);
            } else {
                Integer value = null;
                try {
                    value = Integer.valueOf(s);
                } catch (NumberFormatException e) {
                    // value = Integer.MAX_VALUE;
                    // do nothing
                }
                if (value == null) {
                    setText(mLastText);
                    setSelection(mLastTextSelection);
                } else if (mMaxValue != null && value >= mMaxValue) {
                    setText(String.valueOf(mMaxValue));
                    setSelection(getText().length());
                } else {
                    setText(String.valueOf(value));
                    setSelection(selectionStart);
                }
            }

            mLastText = getText().toString();
            mLastTextSelection = getSelectionStart();

            if (mTextWatcher != null)
                mTextWatcher.onTextChanged(charSequence, start, before, count);
            if (mOnNumberChangedListener != null)
                mOnNumberChangedListener.onNumnberChanged(Useless.stringToInt(charSequence, 0));

            IntegerNumberInput.super.addTextChangedListener(this);
        }

        private void resetText(String value) {
            setText(String.valueOf(value));
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (mTextWatcher != null) mTextWatcher.afterTextChanged(editable);
        }

    };

    public void setMaxValue(Integer mMaxValue) {
        this.mMaxValue = mMaxValue;
    }

    public interface OnNumberChangedListener {
        void onNumnberChanged(int number);
    }

    public IntegerNumberInput(Context context) {
        this(context, null);
    }

    public IntegerNumberInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.IntegerNumberInput);
        final boolean transparentBackground = typedArray.getBoolean(R.styleable.IntegerNumberInput_default_transparent_background, true);
        if (transparentBackground) setBackgroundColor(Color.TRANSPARENT);
        if (typedArray.hasValue(R.styleable.IntegerNumberInput_value_max)) {
            mMaxValue = typedArray.getInt(R.styleable.IntegerNumberInput_value_max, Integer.MAX_VALUE);
        }
        typedArray.recycle();
        internalInit();
        SRC_HEIGHT_5_4 = context.getResources().getDisplayMetrics().heightPixels * 4 / 5;
    }

    public IntegerNumberInput(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, null);
    }

    private void internalInit() {
        super.setInputType(InputType.TYPE_CLASS_NUMBER);
        super.addTextChangedListener(mMainWatcher);
        setImeOptions(EditorInfo.IME_ACTION_NONE);
    }

    public void setOnNumberChangedListener(OnNumberChangedListener onNumberChangedListener) {
        this.mOnNumberChangedListener = onNumberChangedListener;
    }

    @Override
    public void setInputType(int type) {
        // super.setInputType(type);
    }

    public void setNumber(int number) {
        setText(String.valueOf(number));
        setSelection(String.valueOf(number).length());
    }

    @Override
    public void addTextChangedListener(TextWatcher watcher) {
        mTextWatcher = watcher;
    }

    public void removeTextChangedListener() {
        mTextWatcher = null;
    }

    private String mLastText = "0";
    private int mLastTextSelection = 0;

    public int getNumber() {
        return getText() == null ? 0 : Useless.stringToInt(getText().toString(), 0);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
    }

    int[] l = new int[2];

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        getLocationOnScreen(l);
        if (onLayoutChangeListener != null)
            onLayoutChangeListener.onMightSoftKeyBoardShow(l[1] <= SRC_HEIGHT_5_4);
        // System.out.println("nimade : " + l[0] + "," + l[1] + " | " + SRC_HEIGHT_5_4);
    }

    public OnMightSoftKeyboardShowListener getOnLayoutChangeListener() {
        return onLayoutChangeListener;
    }

    public void setOnMightSoftKeyboardShowListener(OnMightSoftKeyboardShowListener onLayoutChangeListener) {
        this.onLayoutChangeListener = onLayoutChangeListener;
    }

    public void removeLayoutOnChangeListener() {
        this.onLayoutChangeListener = null;
    }

    private OnMightSoftKeyboardShowListener onLayoutChangeListener;

    public interface OnMightSoftKeyboardShowListener {
        void onMightSoftKeyBoardShow(boolean show);
    }

    public void resetCursor() {
        if (getText() != null) {
            setSelection(getText().toString().length());
        }
    }
}
