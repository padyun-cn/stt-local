package com.uls.stlocalservice.ui.floating;

import android.annotation.SuppressLint;
import android.view.View;

import com.mon.ui.floating.Overlay;
import com.mon.ui.floating.OverlayParamFactory;
import com.uls.stlocalservice.R;

/**
 * Created by daiepngfei on 2021-01-18
 */
public class OverlayButtonBack extends Overlay {

    private static class F {
        @SuppressLint("StaticFieldLeak")
        private static final OverlayButtonBack sInstance = new OverlayButtonBack();
    }

    public static OverlayButtonBack getInstance() {
        return F.sInstance;
    }

    private OverlayButtonBack() {
        // do nothing
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        setContentView(R.layout.layout_system_window_button_back, OverlayParamFactory.wrapContent());
    }

    public void setOnClickListener(View.OnClickListener l) {
        post(() -> findViewById(R.id.button).setOnClickListener(l));
    }
}
