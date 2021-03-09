package com.uls.utilites.content;

import android.animation.TimeInterpolator;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import com.uls.utilites.un.Useless;


/**
 * Created by daiepngfei on 3/6/18
 */

public class YpLooper {

    private Runnable mRunnable;
    private final int mFrameTime;
    private final Handler mLooper;
    private boolean mIsPaused;
    private boolean mIsConsumePaused;
    private long mDelayTime;
    private long mConsumePausedTime;
    private float mDuration = -1;
    private boolean mDurationMode;
    private TimeInterpolator mTimeInterpolator;
    private OnInterpolatorLoop mOnInterpolatorLoop;
    private long mInterpolatorStartTime;
    private boolean mIsEnable = true;
    private int mRepeatTimes;
    private final String mName;


    public interface OnInterpolatorLoop {
        void onLoop(float output);
    }

    public YpLooper(Looper looper, String name, float frameTime, Runnable runnable) {
        mName = Useless.isEmpty(name) ? "noname" : name;
        mFrameTime = (int) (frameTime * 1000);
        mLooper = new Handler(looper == null ? Looper.getMainLooper() : looper);
        mRunnable = runnable;
        initStartDelayTime();
    }

    public YpLooper(String name, float frameTime, Runnable runnable) {
        this(null, name, frameTime, runnable);
    }

    public YpLooper(String name, float frameTime, int duration, TimeInterpolator interpolator, OnInterpolatorLoop onInterpolatorLoop) {
        this(name, frameTime, duration, 0, interpolator, onInterpolatorLoop);
    }

    public YpLooper(String name, float frameTime, int duration, int repeat, TimeInterpolator interpolator, OnInterpolatorLoop onInterpolatorLoop) {
        this(name, frameTime, null);
        mRepeatTimes = repeat;
        mDurationMode = true;
        mDuration = duration;
        mOnInterpolatorLoop = onInterpolatorLoop;
        mTimeInterpolator = interpolator == null ? new LinearInterpolator() : interpolator;
    }


    private void initStartDelayTime() {
        mDelayTime = SystemClock.uptimeMillis();
    }

    public void start() {
        if (!mIsPaused && mIsEnable) {
            mDelayTime += mFrameTime + consumedPauseTime();
            mLooper.postAtTime(() -> {
                boolean repeat = true;
                if (mDurationMode) {
                    if (mOnInterpolatorLoop != null) {
                        if(isConsumePaused()){
                            resetInterpolatorStartTime();
                        }
                        final long delta = SystemClock.uptimeMillis() - mInterpolatorStartTime;
                        final float input = delta >= mDuration ? 1f :  delta / mDuration;
                        float output = mTimeInterpolator.getInterpolation(input);
                        mOnInterpolatorLoop.onLoop(output);
                        if(output >= 1) {
                            if(!repeat()) repeat = false;
                            else resetInterpolatorStartTime();
                        }

                    }
                } else run();
                if(repeat) start();
            }, mDelayTime);

        }
    }

    private void run() {
        final Runnable run = mRunnable;
        if(run != null) {
            Log.i("YPLO -> " + mName + ", " + this.toString() + ", " + getRunnableInfo(), " RUN");
            run.run();
        } else {
            Log.i("YPLO -> " + mName + ", " + this.toString() + ", " + getRunnableInfo() , " RUN-FAIL");
            //opengl_render_release();
        }
        // else opengl_render_release();
    }

    private String getRunnableInfo(){
        return mRunnable == null ? "noname" : mRunnable.toString();
    }

    private boolean repeat() {
        if(mRepeatTimes > 0) mRepeatTimes --;
        return mRepeatTimes != 0;
    }

    private void resetInterpolatorStartTime() {
        mInterpolatorStartTime = SystemClock.uptimeMillis();
    }

    public void restart(){
        pause();
        resetInterpolatorStartTime();
        resume();
    }

    public void resume(boolean immedatelyStartOnce) {
        mIsPaused = false;
        Log.i("YPLO -> " + mName + ", " + this.toString() + ", " + getRunnableInfo() ,  " resume");
        if(immedatelyStartOnce) run();
        if(isLoopeEnable()) {
            initStartDelayTime();
            resetInterpolatorStartTime();
            start();
        }
    }

    public void resume() {
        resume(false);
    }

    public void pause() {
        pause(-1);
    }


    public void release(){
        pause();
        setLoopEnable(false);
        Log.i("YPLO -> " + mName + ", " + this.toString() + ", " + getRunnableInfo() , " opengl_render_release");
        mRunnable = null;
    }

    public void pause(float time) {
        if (time >= 0) {
            mIsConsumePaused = true;
            mConsumePausedTime = (long) (time * 1000);
        } else {
            mIsPaused = true;
            mLooper.removeCallbacksAndMessages(null);
        }
    }

    public void setLoopEnable(boolean enable) {
        mIsEnable = enable;
    }

    public boolean isLoopeEnable() {
        return mIsEnable;
    }
    private boolean isConsumePaused() {
        final boolean consumePaused = mIsConsumePaused;
        mIsConsumePaused = false;
        return consumePaused;
    }

    private long consumedPauseTime() {
        final long consume = mConsumePausedTime;
        mConsumePausedTime = 0;
        return consume;
    }

}
