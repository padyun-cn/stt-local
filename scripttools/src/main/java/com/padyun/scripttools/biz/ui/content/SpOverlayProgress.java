package com.padyun.scripttools.biz.ui.content;

import android.view.View;

/**
 * Created by daiepngfei on 10/18/19
 */
public class SpOverlayProgress implements IOverlayProgress{

    private final View overlay;

    public SpOverlayProgress(View overlay){
        this.overlay = overlay;
    }

    @Override
    public void showProgress() {
        if(overlay != null) overlay.setVisibility(View.VISIBLE);
    }

    @Override
    public void dissmissProgress() {
        if(overlay != null) overlay.setVisibility(View.INVISIBLE);
    }
}
