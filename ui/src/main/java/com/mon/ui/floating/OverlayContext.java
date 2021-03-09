package com.mon.ui.floating;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import androidx.core.util.Consumer;

/**
 * Created by daiepngfei on 2021-01-13
 */
@SuppressWarnings("ALL")
public class OverlayContext {

    static final int UNKNOWN = -1;
    static final int NONE = 0;
    static final int CREATE = 1;
    static final int DESTROY = 10;
    static final int SAVE_STATE = 13;
    static final int RESTORE_STATE = 14;
    static final int ATTACH_WIN = 15;
    static final int DETTACH_WIN = 16;
    private Context context;
    private List<Overlay> overlayContainers = new ArrayList<>();
    private LinkedList<Overlay> overlayStack = new LinkedList<>();
    private WindowManager mWindowManager;

    public OverlayContext(Context context) {
        this.context = context;
        this.mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    /**
     *
     * @param context
     * @param overlayContainer
     */
    private void _addInOrder(Context context, Overlay overlayContainer) {
        this.context = context;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        overlayContainer.setOverlayContext(this);
        overlayContainer.exec(getMessage(CREATE));
    }

    /**
     *
     * @param container
     */
    private void _removeInOrder(Overlay container) {
        container.exec(() -> {
            try{
                System.out.println("current Mode remove " + container.getClass().getSimpleName());
                mWindowManager.removeViewImmediate(container.getDecorView());
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    /**
     *
     * @param overlayContainer
     * @param layoutParams
     * @param rootView
     */
    private void _updateInOrder(Overlay overlayContainer, WindowManager.LayoutParams layoutParams, View rootView) {
        overlayContainer.exec(() -> {
            try {
                mWindowManager.updateViewLayout(rootView, layoutParams);
            } catch (Exception e){
                 e.printStackTrace();
            }
        });
    }

    /**
     *
     * @return
     */
    private WindowManager.LayoutParams _defaultLayoutParams() {
        return OverlayParamFactory.getLayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, Gravity.START | Gravity.TOP);
    }

    public Context getContext() {
        return context;
    }

    void setContentView(Overlay container, int layoutId, WindowManager.LayoutParams params, boolean attachToWindow) {
        setContentView(container, layoutId, params, attachToWindow, false);
    }

    void setContentView(Overlay container, int layoutId, WindowManager.LayoutParams params, boolean attachToWindow, boolean finishOnKeyEevent) {
        if(params == null){
            params = _defaultLayoutParams();
        }
        FrameLayout layout = new FrameLayout(context){
            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
                if (finishOnKeyEevent && (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                        || event.getKeyCode() == KeyEvent.KEYCODE_SETTINGS)) {
                    container.finish();
                }
                return super.dispatchKeyEvent(event);
            }
        };
        layout.setLayoutParams(params);
        LayoutInflater.from(context).inflate(layoutId, layout);
        container.setDecorView(layout);
        View root = layout.getChildAt(0);
        container.setRootView(root);
        container.setLayoutParams(params);
        if(attachToWindow){
            attachToWindow(container);
        }
    }

    void attachToWindow(Overlay container){
        container.exec(() -> mWindowManager.addView(container.getDecorView(), container.getLayoutParams()));
        container.exec(getMessage(ATTACH_WIN));
    }


    void removeOverlay(Overlay overlayContainer, boolean removeFromList) {
        if (overlayContainers.contains(overlayContainer)) {
            _removeInOrder(overlayContainer);
            if (removeFromList) {
                overlayContainers.remove(overlayContainer);
            }
        }
    }

    void updateOverlayLayout(Overlay overlayContainer, WindowManager.LayoutParams layoutParams) {
        if (overlayContainer != null) {
            View rootView = overlayContainer.getDecorView();
            if (rootView != null) {
                overlayContainer.setLayoutParams(layoutParams);
                _updateInOrder(overlayContainer, layoutParams, rootView);
            }
        }
    }

    void addOverlay(Context context, Overlay overlayContainer) {
        if (!overlayContainers.contains(overlayContainer)) {
            overlayContainers.add(overlayContainer);
            System.out.println("current Mode add " + overlayContainer.getClass().getSimpleName());
            _addInOrder(context, overlayContainer);
        }
    }

    public void clearAll() {
        popAllOverlays();
        for (Overlay container : overlayContainers) {
            removeOverlay(container, false);
        }
        overlayContainers.clear();
    }

    private Message getMessage(int what) {
        return getMessage(what, null);
    }

    private Message getMessage(int what, Bundle data) {
        Message message = Message.obtain();
        message.what = what;
        message.setData(data);
        return message;
    }

    public void pushOverlay(Context context, Overlay overlayContainer) {
        if (overlayContainer != null && !overlayContainers.contains(overlayContainer) && !overlayStack.contains(overlayContainer)) {
            overlayStack.push(overlayContainer);
            _addInOrder(context, overlayContainer);
        }
    }

    public void popOverlay() {
        if(overlayStack.size() != 0) {
            Overlay container = overlayStack.pop();
            if (container != null) {
                _removeInOrder(container);
            }
        }
    }

    boolean delete(Overlay container){
        try {
            if (overlayStack.remove(container)) {
                // _removeInOrder(container);
            }
            if (overlayContainers.remove(container)) {
                // _removeInOrder(container);
            }
            _removeInOrder(container);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void popAllOverlays() {
        for(Overlay container : overlayStack){
            if(container != null){
                _removeInOrder(container);
            }
        }
        overlayStack.clear();

    }

    public void showLoading(Context context) {
        pushOverlay(context, OverlayProgressDialog.getInstance());
    }

    public void dismissLoading(){
        delete(OverlayProgressDialog.getInstance());
    }

    private void foreachAllContainers(Consumer<Overlay> containerConsumer){
        if(containerConsumer != null){
            Set<Overlay> containers = new HashSet<>();
            for (Overlay overlayContainer : overlayContainers) {
                if(!containers.contains(overlayContainer)) {
                    containerConsumer.accept(overlayContainer);
                    containers.add(overlayContainer);
                }
            }
            for (Overlay overlayContainer : overlayStack) {
                if(!containers.contains(overlayContainer)) {
                    containerConsumer.accept(overlayContainer);
                    containers.add(overlayContainer);
                }
            }
        }
    }

    void onSreenOrientationConfigChange(){
        final int rotation = mWindowManager.getDefaultDisplay().getRotation();
        Log.d("OverlayManager","onSreenOrientationConfigChange getRotation = " + rotation);
        /*
        switch (rotation){
            case Surface.ROTATION_0:
                //竖屏
                break;
            case Surface.ROTATION_90:
                //顺时针旋转90度
                break;
            case Surface.ROTATION_180:
                //顺时针旋转180度
                break;
            case Surface.ROTATION_270:
                //顺时针旋转270度
                break;
            default:
                break;
        }
        */
        foreachAllContainers(t-> t.onScreenOrientationChanged(rotation));

    }


}
