package com.mon.ui.anim;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.daimajia.androidanimations.library.BaseViewAnimator;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

/**
 * Created by daiepngfei on 1/2/18
 */
@SuppressWarnings("WeakerAccess")
public class CoreAnimHelper {

    private static final long DUR = 350;

    private static YoYo.AnimatorCallback sEmtpty = new YoYo.AnimatorCallback() {
        @Override
        public void call(Animator animator) {

        }
    };

    public static void translateY(final View view, final int from, final int to) {
        translateY(view, from, to, sEmtpty);
    }

    public static void translateY(final View view, final int from, final int to, YoYo.AnimatorCallback onEnd) {
        YoYo.with(
                new BaseViewAnimator() {
                    @Override
                    protected void prepare(View target) {
                        getAnimatorAgent().playTogether(
                                ObjectAnimator.ofFloat(target, "translationY", from, to)
                        );
                    }
                })
                .duration(DUR)
                .interpolate(new AccelerateDecelerateInterpolator())
                .onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        view.setVisibility(View.VISIBLE);
                    }
                })
                .onEnd(onEnd == null ? sEmtpty : onEnd)
                .playOn(view);
    }

    /**
     *
     * @param view
     * @param dur
     * @param end
     * @return
     */
    public static AnimatorSet rotateSelfInfi(final View view, int dur, Runnable end){
        return rotate(view, 0f, 360f, dur, -1, end);
    }

    /**
     *
     * @param view
     * @param from
     * @param to
     * @param dur
     * @param repeat
     * @param end
     * @return
     */
    public static AnimatorSet rotate(final View view, final float from, final  float to, int dur, int repeat, Runnable end){
        final BaseViewAnimator animator = new BaseViewAnimator() {
            @Override
            protected void prepare(View target) {
                getAnimatorAgent().playTogether(
                        ObjectAnimator.ofFloat(target, "rotation", from, to)
                );
            }
        };

        Animator.AnimatorListener l =  new CoreSimpleEndAnimatorListener(end);
        YoYo.with(animator)
                .withListener(l)
                .duration(dur)
                .interpolate(new LinearInterpolator())
                .repeat(repeat)
                .playOn(view);
        return animator.getAnimatorAgent();
    }

    public static void fadeOut(final View view) {
        techOn(Techniques.FadeOut, view);
    }

    public static void fadeIn(final View view) {
        techOn(Techniques.FadeIn, view);
    }

    public static void slideInUp(final View view) {
        techOn(Techniques.SlideInUp, view);
    }


    public static void techOn(Techniques composer, final View view) {
        techOn(composer, DUR, view);
    }

    public static void techOff(Techniques composer, long dur, final View view, final Runnable onEnd) {
        tech(composer, dur, view, true, onEnd);
    }

    public static void techOff(Techniques composer, final View view) {
        techOff(composer, DUR, view, null);
    }

    public static void techOn(Techniques composer, final View view, final Runnable onEnd) {
        techOn(composer, DUR, view, onEnd);
    }

    public static void techOn(Techniques composer, long dur, final View view) {
        tech(composer, dur, view, false, null);
    }

    public static void techOn(Techniques composer, long dur, final View view, final Runnable onEnd) {
        tech(composer, dur, view, false, onEnd);
    }

    private static void tech(Techniques composer, long dur, final View view, final boolean off, final Runnable onEnd) {
        YoYo.with(composer)
                .duration(dur)
                .interpolate(new AccelerateDecelerateInterpolator())
                .onStart(animator -> view.setVisibility(View.VISIBLE))
                .onEnd(animator -> {
                    if (off) {
                        view.setVisibility(View.GONE);
                    }
                    if (onEnd != null) {
                        onEnd.run();
                    }
                }).playOn(view);
    }


}
