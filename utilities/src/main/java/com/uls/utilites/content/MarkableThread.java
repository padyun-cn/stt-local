package com.uls.utilites.content;

/**
 * Created by daiepngfei on 10/28/19
 */
public class MarkableThread extends Thread {
    private boolean markedStopped;
    private boolean markedStarted;

    @Override
    public void start() {
        synchronized (this) {
            if (!markedStopped && !markedStarted) {
                markedStarted = true;
                super.start();
            }
        }
    }

    public boolean isMarkedStarted() {
        synchronized (this){
            return markedStarted;
        }
    }

    /**
     *
     */
    public void setMarkedStopped() {
        synchronized (this) {
            if (!markedStopped) {
                markedStopped = true;
                interrupt();
            }
        }
    }

    /**
     *
     * @return
     */
    public boolean isMarkedStopped() {
        synchronized (this) {
            return markedStopped;
        }
    }
}
