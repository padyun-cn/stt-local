package com.uls.stlocalservice.ui.floating;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.mon.ui.floating.Overlay;
import com.mon.ui.floating.OverlayParamFactory;
import com.uls.stlocalservice.R;
import com.uls.utilites.content.YpLooper;

import androidx.core.util.Consumer;

/**
 * Created by daiepngfei on 2021-01-13
 */
class OverlayEdgeButton extends Overlay implements View.OnTouchListener {

    private Handler edgeHalfHideHandler = new Handler();
    private boolean isWaitingToHalfHide = false;
    private boolean needHalfHideEdge = false;
    private View.OnClickListener clickListener;

    public void setNeedHalfHideEdge(boolean needHalfHideEdge) {
        this.needHalfHideEdge = needHalfHideEdge;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        WindowManager.LayoutParams params = OverlayParamFactory.wrapContent();
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        setContentView(R.layout.layout_system_window_view, params);
        getDecorView().setOnTouchListener(this);
        getDecorView().setOnClickListener(v -> onButtonClicked());
        onCustomInit();
    }

    protected void onCustomInit() {
    }

    protected void setDisabled(boolean disabled) {
        isDisabled = disabled;
    }

    protected void onButtonClicked(){
        if (getClickListener() != null) {
            getClickListener().onClick(getDecorView());
        }
    }

    protected void setButtonImageRes(int img) {
        ((ImageView) getDecorView().findViewById(R.id.img)).setImageResource(img);
    }

    private float mTouchDownX, mTouchDownY;
    private int wX, wY;
    private boolean isDisabled;
    private static final int TOUCH_SLOP = 5;


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // return detector.onTouchEvent(event);
        if (isDisabled) {
            return false;
        }
        final int action = event.getAction();
        final int disX = (int) (event.getRawX() - mTouchDownX);
        final int disY = (int) (event.getRawY() - mTouchDownY);
        final int scrW = getDecorView().getResources().getDisplayMetrics().widthPixels;
        final int scrH = getDecorView().getResources().getDisplayMetrics().heightPixels;
        System.out.println("scrH/W: " + scrH + "/" + scrW);
        final int limMaxX = scrW - v.getMeasuredWidth();
        final int limMaxY = scrH - v.getMeasuredHeight();
        final WindowManager.LayoutParams params = getLayoutParams();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mTouchDownX = event.getRawX();
                mTouchDownY = event.getRawY();
                wX = params.x;
                wY = params.y;
                if (needHalfHideEdge && isWaitingToHalfHide) {
                    edgeHalfHideHandler.removeCallbacksAndMessages(null);
                    isWaitingToHalfHide = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if ((Math.abs(disX) > TOUCH_SLOP || Math.abs(disY) > TOUCH_SLOP)) {
                    params.x  = Math.min(limMaxX, Math.max(0, wX + disX));
                    params.y  = Math.min(limMaxY, Math.max(0, wY + disY));
                    updateOverlayLayout(params);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_CANCEL:
                if (Math.abs(disX) < TOUCH_SLOP && Math.abs(disY) < TOUCH_SLOP
                        && action == MotionEvent.ACTION_UP && event.getEventTime() - event.getDownTime() < 200) {
                    v.performClick();
                }
                final int nowX = params.x;
                final int distance = (nowX + v.getMeasuredWidth() / 2) <= (scrW / 2) ? -nowX : scrW - nowX - v.getMeasuredWidth();
                horizontalMoveWindowTo(params, nowX, distance, this::doHalfHide);
                break;
            default:

        }

        return true;
    }

    private void horizontalMoveWindowTo(WindowManager.LayoutParams params, int nowX, int distance, Consumer<WindowManager.LayoutParams> onMoveOver) {
        setDisabled(true);
        final YpLooper looper = new YpLooper("", 0.016f, 150,
                new AccelerateDecelerateInterpolator(),
                output -> {
                    params.x = (int) (nowX + distance * output);
                    System.out.println("nnhb: " + params.x);
                    updateOverlayLayout(params);
                    if (output == 1) {
                        setDisabled(false);
                        if (onMoveOver != null) {
                            onMoveOver.accept(params);
                        }
                    }
                });

        looper.resume();
    }

    protected void doHalfHide(WindowManager.LayoutParams params) {
        if (!needHalfHideEdge || isWaitingToHalfHide) {
            return;
        }
        isWaitingToHalfHide = true;
        edgeHalfHideHandler.postDelayed(() -> {
            isWaitingToHalfHide = false;
            setDisabled(true);
            int distance = 0;
            if (params.x <= 0) {
                distance = -getDecorView().getMeasuredWidth() / 2;
            } else if (params.x >= getDecorView().getResources().getDisplayMetrics().widthPixels - getDecorView().getMeasuredWidth()) {
                distance = getDecorView().getMeasuredWidth() / 2;
            }
            if (distance != 0) {
                horizontalMoveWindowTo(params, params.x, distance, null);
            }
        }, 1500);
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.clickListener = listener;
    }

    public View.OnClickListener getClickListener() {
        return clickListener;
    }

}
