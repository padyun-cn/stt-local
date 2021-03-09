package com.mon.ui.floating;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

/**
 * Created by daiepngfei on 2021-01-13
 */
@SuppressWarnings({"WeakerAccess", "unused", "SameParameterValue"})
public class Overlay implements Handler.Callback {
    private Handler mHandler = new Handler(this);
    private View decorView, rootView;
    private OverlayContext overlayContext;
    private int mState = OverlayContext.NONE;
    private WindowManager.LayoutParams layoutParams;


    public void setLayoutParams(WindowManager.LayoutParams params) {
        this.layoutParams = params;
    }

    public WindowManager.LayoutParams getLayoutParams() {
        return layoutParams;
    }

    public interface OnFinishedListener {
        void onOverlayFinished();
    }

    protected Context getContext() {
        return getOverlayContext().getContext();
    }


    public void setRootView(View rootView) {
        this.rootView = rootView;
    }

    /**
     * @param decorView
     */
    void setDecorView(View decorView) {
        this.decorView = decorView;
    }


    /**
     * @return
     */
    public View getDecorView() {
        return decorView;
    }

    public View getRootView() {
        return rootView;
    }

    /**
     * @param id
     *
     * @return
     */
    protected View findViewById(int id) {
        return decorView == null ? null : decorView.findViewById(id);
    }

    /**
     * @param msg
     */
    void exec(Message msg) {
        mHandler.sendMessage(msg);
    }

    /**
     *
     * @param runnable
     */
    void exec(Runnable runnable){
       mHandler.post(runnable);
    }

    /**
     * @param overlayManager
     */
    void setOverlayContext(OverlayContext overlayManager) {
        this.overlayContext = overlayManager;
    }

    /**
     * @param layoutId
     */
    protected void setContentView(int layoutId) {
        overlayContext.setContentView(this, layoutId, null, true);
    }

    /**
     * @param layoutId
     * @param params
     */
    protected void setContentView(int layoutId, WindowManager.LayoutParams params) {
        overlayContext.setContentView(this, layoutId, params, true);
    }

    protected void updateOverlayLayout(WindowManager.LayoutParams oa) {
        overlayContext.updateOverlayLayout(this, oa);
    }

    protected void onCreate() {
    }


    protected void onSaveInstanceState(@NonNull Bundle outState) {
    }

    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
    }

    protected void onAttachToWindow() {

    }

    protected void onDettachFromWindow() {

    }

    protected void onDestroy() {

    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        mState = msg.what;
        switch (msg.what) {
            case OverlayContext.CREATE:
                onCreate();
                break;
            case OverlayContext.DESTROY:
                onDestroy();
                break;
            case OverlayContext.ATTACH_WIN:
                onAttachToWindow();
                break;
            case OverlayContext.DETTACH_WIN:
                onDettachFromWindow();
                break;
            case OverlayContext.SAVE_STATE:
                onSaveInstanceState(msg.getData());
                break;
            case OverlayContext.RESTORE_STATE:
                onRestoreInstanceState(msg.getData());
                break;
            default:
                mState = OverlayContext.UNKNOWN;
        }
        return true;
    }

    public void showOverlay() {
        mHandler.post(() -> {
            if (rootView != null) {
                rootView.setVisibility(View.VISIBLE);
            }
        });
    }

    public void dismissOverlay() {
        mHandler.post(() -> {
            if (rootView != null) {
                rootView.setVisibility(View.GONE);
            }
        });
    }

    public void toggleOverlay() {
        mHandler.post(() -> {
            if (rootView != null) {
                if (rootView.getVisibility() != View.GONE) {
                    rootView.setVisibility(View.GONE);
                } else {
                    rootView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void finish() {
        overlayContext.delete(this);
    }

    public void onScreenOrientationChanged(int degree) {
        // do nothing
    }

    public void showLoading() {
         overlayContext.showLoading(getContext());
    }

    public void dismissLoading()
    {
        overlayContext.dismissLoading();
    }

    public OverlayContext getOverlayContext() {
        return overlayContext;
    }

    public void post(Runnable runnable){
        post(runnable, 0);
    }

    public void post(Runnable runnable, int time){
        mHandler.postDelayed(runnable, time);
    }
}
