package com.mon.ui.buildup;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by aj on 2017/7/7.
 *
 * 由键盘会触发 onLayput  所以创建一个隐藏的view来做监听
 */

public class CKeyBoardListenerView extends RelativeLayout {
    private KeyBoardChangeListener keyBoardChangeListener=null;

    public CKeyBoardListenerView(Context context) {
        this(context,null);
    }

    public CKeyBoardListenerView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CKeyBoardListenerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CKeyBoardListenerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (keyBoardChangeListener!=null){
            keyBoardChangeListener.onChange();
        }
    }

    public KeyBoardChangeListener getKeyBoardChangeListener() {
        return keyBoardChangeListener;
    }

    public void setKeyBoardChangeListener(KeyBoardChangeListener keyBoardChangeListener) {
        this.keyBoardChangeListener = keyBoardChangeListener;
    }

    public interface KeyBoardChangeListener{
      public void onChange();
    }
}
