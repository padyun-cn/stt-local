package com.uls.stlocalservice.ui.floating;

import android.view.Gravity;
import android.view.WindowManager;

import com.uls.stlocalservice.R;
import com.uls.utilites.ui.DensityUtils;

/**
 * Created by daiepngfei on 2021-01-18
 */
public class OverlayEdgeButton_Scripts extends OverlayEdgeButton {
    private OverlayEdgeButton_Scripts(){}

    public static OverlayEdgeButton_Scripts getInstance(){
        return F.sIns;
    }

    private static final class F {
        private static final OverlayEdgeButton_Scripts sIns = new OverlayEdgeButton_Scripts();
    }

    @Override
    protected void onCustomInit() {
        setNeedHalfHideEdge(true);
        setButtonImageRes(R.drawable.ic_floating_button_script_list);
        getLayoutParams().flags |= WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        getLayoutParams().gravity = Gravity.START | Gravity.TOP;
        getLayoutParams().x = getDecorView().getResources().getDisplayMetrics().widthPixels - DensityUtils.dip2px(getDecorView().getContext(), 60);
        getLayoutParams().y = getDecorView().getResources().getDisplayMetrics().heightPixels / 2 - DensityUtils.dip2px(getDecorView().getContext(), 30);
        updateOverlayLayout(getLayoutParams());
    }

    @Override
    protected void onButtonClicked() {
        if(getLayoutParams().x < 0 || getLayoutParams().x > getDecorView().getResources().getDisplayMetrics().widthPixels -
                DensityUtils.dip2px(getDecorView().getContext(), 60)){
            return;
        }
        super.onButtonClicked();
    }

    @Override
    public void onScreenOrientationChanged(int degree) {
        onCustomInit();
    }
}
