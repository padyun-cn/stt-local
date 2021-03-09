package com.uls.utilites.content;

import android.util.Log;

import androidx.core.util.Consumer;

/**
 * Created by daiepngfei on 2020-05-21
 */
public class SProgressor {

    private final long fMaxProgress;
    private String logTag;
    private long currentProgress = 0L;
    private final long fMinimumTimeInterval;
    private static final long DEFAULT_MINIMUMTIMEINTERVAL = 500;
    private long lastProgressingTimeInMills, startProgressingTimeInMills;
    private Consumer<Float> onProgressingComsumer;

    public SProgressor(long maxProgress, Consumer<Float> onProgressingComsumer) {
        this(maxProgress, DEFAULT_MINIMUMTIMEINTERVAL, onProgressingComsumer);
    }

    public SProgressor(long maxProgress, long minimumTimeIntervalMills, Consumer<Float> onProgressingComsumer) {
        this(maxProgress, minimumTimeIntervalMills, null, onProgressingComsumer);
    }

    public SProgressor(long maxProgress, long minimumTimeIntervalMills, String logTag, Consumer<Float> onProgressingComsumer) {
        this.fMaxProgress = maxProgress;
        this.fMinimumTimeInterval = minimumTimeIntervalMills;
        this.onProgressingComsumer = onProgressingComsumer;
        this.logTag = logTag;
    }

    private void onUpdatingProgress() {
        if (fMaxProgress <= 0 || onProgressingComsumer == null) {
            return;
        }
        final long now = System.currentTimeMillis();
        if(startProgressingTimeInMills == 0) {
            startProgressingTimeInMills = now;
            if(logTag != null) {
                Log.d("s-Progressor: ", this.hashCode() + "/" + logTag + " *开始* ");
            }
        }
        final float currentProgressFloat = Math.min(1.0F, currentProgress * 1.0F / fMaxProgress);
        final long timeInterval = now - lastProgressingTimeInMills;

        if(currentProgressFloat == 1.0F || timeInterval >= fMinimumTimeInterval){
            onProgressingComsumer.accept(currentProgressFloat);
            lastProgressingTimeInMills = now;
            if(logTag != null) {
                Log.d("s-Progressor: ", logTag + " " + (int)(currentProgressFloat * 100) + "%" + " ** (" + currentProgress + "/" + fMaxProgress + ")");
            }
        }


        if(currentProgressFloat == 1.0F){
            if(logTag != null) {
                Log.d("s-Progressor: ", logTag + " 总耗时： " + ((now - startProgressingTimeInMills) / 1000F) + "s");
                Log.d("s-Progressor: ", logTag + " *结束* (" + currentProgress + "/" + fMaxProgress + ")");
            }
        }
    }

    public void setLogTag(String logTag) {
        this.logTag = logTag;
    }

    public void updateProgressTo(long number) {
        currentProgress = number;
        onUpdatingProgress();
    }

    public void updateProgressBy(long number) {
        currentProgress += number;
        onUpdatingProgress();
    }




}
