package com.uls.utilites.common;

import com.uls.utilites.un.Useless;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by daiepngfei on 2020-10-28
 */
@SuppressWarnings("unused")
public class BlockingQueueConsumer<T> implements Runnable {
    private ExecutorService executorService = null;
    private final LinkedBlockingQueue<T> queue;
    private Runnable onTerminated;
    private Future consumerFuture;
    private final IBlockingConsumer<T> onWorkConsumer;
    private boolean isRunning;

    public BlockingQueueConsumer(int cap, IBlockingConsumer<T> onWorkRunnable) {
        this.queue = new LinkedBlockingQueue<>(cap);
        this.onWorkConsumer = onWorkRunnable;
    }

    public void start() {
        if (onWorkConsumer == null) {
            return;
        }
        synchronized (this) {
            if (executorService == null) {
                isRunning = true;
                executorService = Executors.newSingleThreadExecutor();
                consumerFuture = executorService.submit(this);
            }
        }
    }

    public boolean isAlive() {
        synchronized (this) {
            return executorService != null && !executorService.isTerminated();
        }
    }

    public void shutdown() {
        shutdown(0, null);
    }

    public void shutdown(Runnable onTemrminated) {
        shutdown(0, onTemrminated);
    }

    public void shutdown(int secondsAwaitToTermined, Runnable onTemrminated) {
        if (onWorkConsumer == null) {
            return;
        }
        synchronized (this) {
            if (executorService == null) {
                return;
            }
            this.isRunning = false;
            this.onTerminated = onTemrminated;
            this.consumerFuture.cancel(true);
            this.executorService.shutdown();
            if (secondsAwaitToTermined > 0) {
                try {
                    executorService.awaitTermination(secondsAwaitToTermined, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void consume(T t) {
        synchronized (this) {
            if (!isRunning || Useless.nulls(executorService, onWorkConsumer, consumerFuture)) {
                return;
            }
        }
        try {
            queue.put(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                Useless.assertThreadInterruption();
                if (onWorkConsumer == null) {
                    break;
                }
                if(!onWorkConsumer.onBlockingConsumed(queue.take())){
                    synchronized (this){
                        isRunning = false;
                    }
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        queue.clear();
        if (onTerminated != null) {
            onTerminated.run();
        }
    }

    public interface IBlockingConsumer<T> {
        boolean onBlockingConsumed(T t);
    }

}
