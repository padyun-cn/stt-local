package com.mon.ui.floating;

import android.annotation.SuppressLint;

import com.mon.ui.R;

/**
 * Created by daiepngfei on 2021-01-19
 */
public class OverlayProgressDialog extends Overlay {


    private static class F {
        @SuppressLint("StaticFieldLeak")
        private static final OverlayProgressDialog sInstance = new OverlayProgressDialog();
    }

    public static OverlayProgressDialog getInstance() {
        return F.sInstance;
    }

    private OverlayProgressDialog() {
        // do nothing
    }


    @Override
    protected void onCreate() {
        super.onCreate();
        setContentView(R.layout.overlay_progress_dialog, OverlayParamFactory.matchParent());
    }
}
