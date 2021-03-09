package com.mon.ui.anim;

import android.animation.Animator;

/**
 * Created by daiepngfei on 12/30/17
 */

public class CoreSimpleEndAnimatorListener implements Animator.AnimatorListener {

    private Runnable mRunnable;

    public CoreSimpleEndAnimatorListener(Runnable runnable){
        this.mRunnable = runnable;
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        runEnding();
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        runEnding();
    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    private void runEnding() {
        if(this.mRunnable != null){
            this.mRunnable.run();
        }
    }

}
