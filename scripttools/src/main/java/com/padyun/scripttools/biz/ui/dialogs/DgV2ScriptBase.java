package com.padyun.scripttools.biz.ui.dialogs;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.padyun.scripttools.R;


/**
 * Created by daiepngfei on 7/25/19
 */
public class DgV2ScriptBase extends DgV2Base {

    public DgV2ScriptBase(@NonNull Context context) {
        super(context);
    }

    public DgV2ScriptBase(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    public void onInitialize() {
        final View closeButton = findViewById(R.id.buttonClose);
        closeButton.setOnClickListener(view -> dismiss());
        final FrameLayout container = findViewById(R.id.layout_content_container);
        container.setClickable(true);
        if(getContentLayout() != 0) {
            final View v = LayoutInflater.from(getContext()).inflate(getContentLayout(), null, false);
            if (v != null) {
                container.addView(v);
            }
        }

    }

    protected void setMasked(boolean mask){
        findViewById(R.id.layoutMask).setVisibility(mask ? View.VISIBLE : View.GONE);
    }

    @Override
    protected final int getLayoutId() {
        return R.layout.dg_v2_script_base;
    }

    protected int getContentLayout() {
        return 0;
    }
}
