package com.mon.ui.dialog;

import android.content.Context;

import com.mon.ui.R;

import org.jetbrains.annotations.NotNull;

/**
 * Created by daiepngfei on 2020-12-15
 */
public class CoreProgressDialog extends AbsDgFullScreen {

    public CoreProgressDialog(@NotNull Context context) {
        this(context, null);
    }

    public CoreProgressDialog(@NotNull Context context, Integer color) {
        super(context, color);
        setCancelable(true);
    }


    @Override
    public int getLayoutId() {
        return R.layout.dg_progress;
    }
}
