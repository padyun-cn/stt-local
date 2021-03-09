package com.padyun.scripttools.biz.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.padyun.scripttools.R;


/**
 * Created by daiepngfei on 1/25/19
 */
public class DgSimpleBase extends Dialog {
    private FrameLayout mContainer;
    public DgSimpleBase(Context context){
        super(context, R.style.options);
        super.setContentView(R.layout.dg_simple_base);
        mContainer = findViewById(R.id.container);
        setContentView(getContentLayoutId());
    }

    @Override
    public final void setContentView(int layoutResID) {
        // super.setContentView(layoutResID);
        if(layoutResID != 0) {
            LayoutInflater.from(getContext()).inflate(layoutResID, mContainer, true);
        }
    }

    @Override
    public final void setContentView(@NonNull View view) {
        // super.setContentView(view);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.addView(view, params);
    }

    @Override
    public void setContentView(@NonNull View view, @Nullable ViewGroup.LayoutParams params) {
        // super.setContentView(view, params);
        mContainer.addView(view, params);
    }

    protected int getContentLayoutId(){
        return 0;
    }
}
