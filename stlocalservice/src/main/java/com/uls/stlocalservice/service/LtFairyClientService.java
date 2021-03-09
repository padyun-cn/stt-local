package com.uls.stlocalservice.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import androidx.annotation.Nullable;

/**
 * Created by daiepngfei on 2021-02-23
 */
public class LtFairyClientService extends Service {

    public static final int STATE_ERROR = -4;
    public static final int STATE_FILE_COPYED_FAILED = -3;
    public static final int STATE_ROOT_FAILED = -2;
    public static final int STATE_IDLE = -1;
    public static final int STATE_STARTING = 0;
    public static final int STATE_SUED = 1;
    public static final int STATE_COPYED = 2;
    public static final int STATE_RUNNING = 3;
    public static final int STATE_OVERED = 4;
    private int state = STATE_IDLE;

    private ExecutorService executorService = null;
    private Future<Boolean> future;
    private FairyBinder binder = new FairyBinder();

    public class FairyBinder extends Binder {
        public LtFairyClientService getService() {
            return LtFairyClientService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public interface OnStartingListner {
        void onStartingStateChanged(int state);
    }

    private Set<OnStartingListner> listners = new HashSet<>();

    public void start(OnStartingListner l) {
        registerListener(l);
        if (executorService == null || future.isDone() || executorService.isTerminated()) {
            executorService = Executors.newSingleThreadExecutor();
            future = executorService.submit(this::onStartFairyService);
        } else {
            synchronized (this) {
                l.onStartingStateChanged(state);
            }
        }
    }

    public void registerListener(OnStartingListner l) {
        listners.add(l);
    }


    public void unregisterListener(OnStartingListner l) {
        listners.remove(l);
    }

    private void setState(int state) {
        synchronized (this) {
            this.state = state;
            for (OnStartingListner l : listners) {
                if (l != null) {
                    l.onStartingStateChanged(state);
                }
            }
        }
    }

    private volatile boolean success;
    private static final String SUCCESS_FLAG = "PROCESS_SUCCESS";
    private static final String TAG = "PROCESS_SUCCESS";

    private boolean onStartFairyService() {
        Runtime runtime = Runtime.getRuntime();
        final Thread currentThread = Thread.currentThread();
        try {
            final Process process = runtime.exec("su");
            final Thread threadInput = new Thread(() -> {
                BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while (true) {
                    String line;
                    try {
                        Log.e(TAG, "读取开始---");
                        line = br.readLine();
                        Log.e(TAG, "读取：" + line);
                        if (line == null) {
                            break;
                        }
                        if (line.equals(SUCCESS_FLAG)) {
                            success = true;
                            break;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "读取失败", e);
                        break;
                    }
                }
                currentThread.interrupt();
            });

            threadInput.start();
            final Thread threadOutput = new Thread(() -> {
                try {
                    Log.e(TAG, "写入开始");
                    process.getOutputStream().write(("echo " + SUCCESS_FLAG + "\n").getBytes());
                    Log.e(TAG, "写入开始2");
                    process.getOutputStream().flush();
                    Log.e(TAG, "写入开始3");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "写入失败", e);
                }
            });
            threadOutput.start();

            try {
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();

            }

            if (!success) {
                setState(STATE_ROOT_FAILED);
            } else {
                setState(STATE_SUED);

                /*------Deprecated------*/
                /*LtBaseServiceManager.copyOrNot(this);
                File file = LtBaseServiceManager.getServiceFile(this);
                Log.e("ESESE", file.getAbsolutePath());*/
                /*if (!file.exists()) {
                    setState(STATE_ROOT_FAILED);
                } else {*/
                //setState(STATE_COPYED);
                /*------Deprecated-----*/

                String cmd = "export CLASSPATH=" + /*file.getAbsolutePath()*/ getApplicationInfo().sourceDir;
                cmd += ";";
                cmd += "app_process /system/bin com.padyun.baseservice.MainActivity ";
                cmd += getApplicationInfo().nativeLibraryDir + "\n";
                Log.e("ESESE", cmd);
                process.getOutputStream().write(cmd.getBytes());
                process.getOutputStream().flush();
                //noinspection ResultOfMethodCallIgnored
                Thread.interrupted();
                process.waitFor();
                process.destroy();
                return true;
                /*}*/
            }

            process.destroy();
        } catch (IOException e) {
            e.printStackTrace();
            setState(STATE_ERROR);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }
}
