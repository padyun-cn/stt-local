package com.uls.stlocalservice.ui.floating;

import android.annotation.SuppressLint;
import android.view.Gravity;

import com.uls.stlocalservice.R;
import com.uls.utilites.ui.DensityUtils;

/**
 * Created by daiepngfei on 2021-01-18
 */
public class OverlayEdgeButton_Editor extends OverlayEdgeButton {

    private static class F {
        @SuppressLint("StaticFieldLeak")
        private static final OverlayEdgeButton_Editor sInstance = new OverlayEdgeButton_Editor();
    }

    public static OverlayEdgeButton_Editor getInstance() {
        return F.sInstance;
    }

    private OverlayEdgeButton_Editor() {
        // do nothing
    }

    @Override
    protected void onCustomInit() {
        setButtonImageRes(R.drawable.ic_floating_button_yellow);
        getLayoutParams().gravity = Gravity.START | Gravity.TOP;
        getLayoutParams().y = getDecorView().getResources().getDisplayMetrics().heightPixels / 2 - DensityUtils.dip2px(getDecorView().getContext(), 30);
        updateOverlayLayout(getLayoutParams());
    }

    @Override
    public void onScreenOrientationChanged(int degree) {
        onCustomInit();
    }

    @Override
    protected void onButtonClicked() {
        super.onButtonClicked();
    }

}
