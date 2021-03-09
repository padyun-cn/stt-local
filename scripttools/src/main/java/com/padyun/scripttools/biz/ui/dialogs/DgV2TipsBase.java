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
public class DgV2TipsBase extends DgV2ScriptBase {

    public DgV2TipsBase(@NonNull Context context) {
        super(context);
    }

    public DgV2TipsBase(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        findViewById(R.id.buttonCloseTips).setOnClickListener(v -> {
            findViewById(R.id.title_bar).setVisibility(View.GONE);
        });

        final FrameLayout container = findViewById(R.id.layout_tips_content_container);
        if(getTipsContentLayout() != 0) {
            final View v = LayoutInflater.from(getContext()).inflate(getTipsContentLayout(), null, false);
            if (v != null) {
                container.addView(v);
            }
        }

    }

    protected void setMasked(boolean mask){
        findViewById(R.id.layoutMask).setVisibility(mask ? View.VISIBLE : View.GONE);
    }

    @Override
    protected final int getContentLayout() {
        return R.layout.dg_v2_tips_base;
    }

    protected int getTipsContentLayout() {
        return 0;
    }
}
