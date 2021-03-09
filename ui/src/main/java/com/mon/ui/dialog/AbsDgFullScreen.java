package com.mon.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.mon.ui.R;
import com.uls.utilites.content.WindowUtils;

import androidx.annotation.NonNull;


/**
 * Created by daiepngfei on 5/14/18
 */
public abstract class AbsDgFullScreen extends Dialog {

    protected void onCustomInit() {
    }

    public AbsDgFullScreen(@NonNull Context context, Integer color) {
        super(context, R.style.CoreCommonDg);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (color != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && getWindow() != null) {
            WindowUtils.setWindowStatusBarColor(color, getWindow());
        }
        initialize(LayoutInflater.from(context));
    }

    public AbsDgFullScreen(@NonNull Context context) {
        this(context, 1);
    }

    private void initialize(LayoutInflater inflater) {
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        if (getLayoutId() > 0) {
            View root = inflater.inflate(getLayoutId(), null);
            if (root != null) setContentView(root);
        }
        onCustomInit();
    }

    protected abstract int getLayoutId();

}
