package com.mon.ui.floating;

import android.graphics.Color;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;

import com.uls.utilites.ui.DensityUtils;

/**
 * Created by daiepngfei on 2021-01-19
 */
public abstract class OverlayDialog extends Overlay {
    private boolean isShowing, isCreated, isDismissed;
    public OverlayDialog(OverlayContext context) {
        setOverlayContext(context);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
    }

    public void create() {
        synchronized (this) {
            if (!isCreated) {
                getOverlayContext().pushOverlay(getContext(), this);
                isCreated = true;
            }
        }
    }

    public void show() {
        synchronized (this) {
            if(isDismissed){
                throw new IllegalStateException("OverlayDialog has been released.");
            }
            if (!isCreated) {
                create();
            }
            if (!isShowing) {
                getOverlayContext().attachToWindow(this);
                isShowing = true;
            }
        }
    }

    public boolean isShowing() {
        synchronized (this) {
            return isShowing;
        }
    }

    @Override
    protected void setContentView(int layoutId) {
        WindowManager.LayoutParams params = OverlayParamFactory.matchParent(Gravity.CENTER);
        params.format = PixelFormat.TRANSLUCENT;
        getOverlayContext().setContentView(this, layoutId, params, false);
        getDecorView().setBackgroundColor(Color.parseColor("#1f000000"));
        final int dp = DensityUtils.dip2px(getContext(), 5);
        getDecorView().setPadding(dp, dp, dp, dp);
        //getRootView().setBackgroundResource(R.drawable.bg_frame_overlay_dialog_white);
    }

    @Override
    protected void setContentView(int layoutId, WindowManager.LayoutParams params) {
        setContentView(layoutId);
    }

    public void dismiss() {
        if(isCreated) {
            finish();
            isCreated = false;
            isShowing = false;
            isDismissed = true;
        }
    }

    public interface OnClickListener {
        void onClick(OverlayDialog dialog, int which);
    }
}
