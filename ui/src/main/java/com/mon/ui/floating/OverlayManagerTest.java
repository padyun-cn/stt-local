package com.mon.ui.floating;

import android.content.Context;
import android.view.WindowManager;

/**
 * Created by daiepngfei on 2021-01-13
 */
public class OverlayManagerTest {

    private OverlayContext overlayViewManager;
    private Context context;

    public OverlayManagerTest(Context context) {
        this.context = context;
        this.overlayViewManager = new OverlayContext(context);
    }

    public void addOverlay(Context context, Overlay container){
        if(SystemWindowManager.getRequiredPermmision(context)) {
            overlayViewManager.addOverlay(context, container);
        }
    }

    public void removeView(Overlay container){
        if(SystemWindowManager.getRequiredPermmision(context)) {
            overlayViewManager.removeOverlay(container, true);
        }
    }

    public void reset() {
        if(SystemWindowManager.getRequiredPermmision(context)) {
            overlayViewManager.clearAll();
        }
    }

    public void updateViewLayout(Overlay container, WindowManager.LayoutParams layoutParams){
        if(SystemWindowManager.getRequiredPermmision(context)) {
            overlayViewManager.updateOverlayLayout(container, layoutParams);
        }
    }

}
