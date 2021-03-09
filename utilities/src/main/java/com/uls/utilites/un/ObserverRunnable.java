package com.uls.utilites.un;

import android.os.Handler;

/**
 * Created by daiepngfei on 10/16/19
 */
public class ObserverRunnable {

    public interface IObserverRunnable {
        boolean when();
        void then();
    }

    private Handler handler = new Handler();
    private long interval = 1000;
    private long maxTime = 3000;
    private IObserverRunnable observerHandler;

    private ObserverRunnable(){}
    private long collapseTime;
    private boolean isCanceled;

    /**
     *
     * @param intervalTime
     * @param maxTime
     * @param handler
     */
    public static ObserverRunnable run(int intervalTime, int maxTime, IObserverRunnable handler){
        if(handler == null) return null;
        ObserverRunnable observerHandler = new ObserverRunnable();
        observerHandler.maxTime = maxTime;
        observerHandler.observerHandler = handler;
        observerHandler.interval = intervalTime;
        observerHandler.run();
        return observerHandler;
    }

    public void cancel(){
        isCanceled = true;
        handler.removeCallbacksAndMessages(null);
    }

    /**
     *
     */
    public void run(){
        if(collapseTime == 0) {
            collapseTime = System.currentTimeMillis();
        }
        if(!isCanceled && System.currentTimeMillis() - collapseTime > maxTime){
            observerHandler.then();
            return;
        }
        handler.postDelayed(() -> {
            if(isCanceled){
                return;
            }
            if(observerHandler.when()) {
                observerHandler.then();
            } else {
                run();
            }
        }, interval);
    }

}
