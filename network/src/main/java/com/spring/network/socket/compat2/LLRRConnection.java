package com.spring.network.socket.compat2;

import android.text.TextUtils;
import android.util.Log;

import com.uls.utilites.un.Useless;
import com.uls.utilites.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Long-Looping Request-Response > Enable sync sending > Is keeping alive true > Looping fetch data
 * > auto reconnecting with origin requests Created by daiepngfei on 11/11/19
 */
@SuppressWarnings("WeakerAccess")
public class LLRRConnection implements OnStateChangedListener, ISockResponse {

    private static final String TAG = "ARSocket#";
    private List<SocketSendingThread.Pack> penddingDataList = new ArrayList<>();
    private boolean asyncSendingReady;
    private OnStateChangedListener onStateChangedListener;
    private Runnable onRedayRunnable;
    final Object mSenderLock = new Object();
    private DataSockRequest request = new DataSockRequest() {
        @Override
        protected void onWrittingFinished() {
            super.onWrittingFinished();
            if (onRedayRunnable != null) {
                onRedayRunnable.run();
            }
            enableAsyncSending();
        }
    };

    public void setOnReadyRunnable(Runnable runnable) {
        onRedayRunnable = runnable;
    }

    private void enableAsyncSending() {
        Log.d("ScriptEditor|7702", "LLRRConn#enableAsyncSending||enter");
        synchronized (mSenderLock) {
            Log.d("ScriptEditor|7702", "LLRRConn#enableAsyncSending||sync");
            if (mainSocketThread != null) {
                Log.d("ScriptEditor|7702", "LLRRConn#enableAsyncSending||sync||notnull");
                mainSocketThread.runAsyncSender();
                asyncSendingReady = true;
                Useless.foreach(penddingDataList, t -> mainSocketThread.addRequestDataAsync(t.data, t.tag, t.callback));
                Log.d("ScriptEditor|7702", "LLRRConn#enableAsyncSending||sync||notnull-over");
            }
        }
        Log.d("ScriptEditor|7702", "LLRRConn#enableAsyncSending||sync||exit");
    }

    private ISockResponse response;
    private RequestSocketThread mainSocketThread;

    /**
     *
     */
    public LLRRConnection(String ip, int port, int soTimeout) {
        this.mainSocketThread = new RequestSocketThread();
        BSConfiguration configuration = BSConfiguration.Builder().setIp(ip).setPort(port).setIsKeepAlive(true).autoConnect(true).enableSendingAsync(true).soTimeout(soTimeout).create();
        onConfig(configuration);
        this.mainSocketThread.setConfiguration(configuration);
        this.mainSocketThread.setOnStateChangedListener(new TagStateChangedListener("", this));
        request.setResponse(this);
    }

    public void setThreadName(String name){
        if(mainSocketThread != null && !TextUtils.isEmpty(name)){
            mainSocketThread.setName(name);
        }
    }

    /**
     *
     * @return
     */
    public boolean isConnected() {
        return mainSocketThread != null && mainSocketThread.isConnectted();
    }

    public boolean isStopped(){
        return mainSocketThread != null && mainSocketThread.isStopped();
    }

    /**
     *
     * @param configuration
     */
    protected void onConfig(BSConfiguration configuration) {

    }


    /**
     * @param ip
     * @param port
     * @param params
     */
    public LLRRConnection(String ip, int port, int soTimeout, DataSocketParams params, String tag, ISockResponse response) {
        this(ip, port, soTimeout, 3, params, tag, response);
    }

    /**
     *
     * @param ip
     * @param port
     * @param soTimeout
     * @param retryCount
     * @param params
     * @param tag
     * @param response
     */
    public LLRRConnection(String ip, int port, int soTimeout, int retryCount, DataSocketParams params, String tag, ISockResponse response) {
        this(ip, port, soTimeout);
        setParams(params);
        setTag(tag);
        setResponse(response);
    }

    /**
     * @param ip
     * @param port
     * @param params
     * @param tag
     */
    public LLRRConnection(String ip, int port, int soTimeout, byte[][] params, String tag, ISockResponse response) {
        this(ip, port, soTimeout, new DataSocketParams(params), tag, response);
    }

    /**
     * @param params
     */
    public void setParams(byte[]... params) {
        this.request.setParams(params);
    }

    /**
     * @param params
     */
    public void setParams(DataSocketParams params) {
        this.request.setParams(params);
    }

    /**
     * @param tag
     */
    public void setTag(String tag) {
        if (!Useless.isEmpty(tag)) {
            this.mainSocketThread.setName(tag + "-" + this.mainSocketThread.getName());
            this.request.setTag(tag);
            this.mainSocketThread.setOnStateChangedListener(new TagStateChangedListener(tag, this));
        }
    }

    /**
     * @param response
     */
    public void setResponse(ISockResponse response) {
        this.response = response;
    }

    /**
     * @param requests
     */
    private void addRequests(StatefulWRSockRequest... requests) {
        mainSocketThread.addRequests(requests);
    }

    /**
     * @param onStateChangedListener
     */
    public void setOnStateChangedListener(OnStateChangedListener onStateChangedListener) {
        this.onStateChangedListener = onStateChangedListener;
    }

    @Override
    public void onConnStateChanged(SockConnState state) {
        switch (state) {
            case ERROR:
                if (response != null) {
                    response.onSendFail(ISockResponse.ERR_EXCEPTION, "", new SReadInterruptException("LLRRConnection onStatechanged::ERROR "));
                }
                break;
            case TERMINATED:
                break;
            case CONNECTTED:
                mainSocketThread.clearRequests();
                mainSocketThread.addRequests(request);
                break;
        }
        if (onStateChangedListener != null) {
            onStateChangedListener.onConnStateChanged(state);
        }
    }

    @Override
    public boolean onResponse(CoReader reader) throws Exception {
        LogUtil.d(TAG, "requestTag{" + Useless.nonNullStr(request.getTag()) + "} onResponsing START ");
        if (response != null) {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    LogUtil.d(TAG, "requestTag{" + Useless.nonNullStr(request.getTag()) + "} onResponsingReading INTERRUPT ");
                    throw new InterruptedException();
                }
                //LogUtil.d(TAG, "requestTag{" + CUtils.nonNullStr(request.getTag()) + "} onResponsing READ ");
                if (!response.onResponse(reader) && !onHandleLoopResponse(reader)) {
                    break;
                }
            }
        }
        LogUtil.d(TAG, "requestTag{" + Useless.nonNullStr(request.getTag()) + "} onResponsing END ");
        return false;
    }

    @Override
    public void onSendFail(int errno, String msg, Exception e) {
        if (response != null) {
            response.onSendFail(errno, msg, e);
        }
    }

    protected boolean onHandleLoopResponse(CoReader reader) {
        return false;
    }


    public void close() {
        if (mainSocketThread != null) {
            mainSocketThread.setMarkedStopped();
        }
    }

    public void addRequestDataAsync(byte[] data, String tag, ISockSendResponse response) {
        Log.d("ScriptEditor|7702", "LLRRConn#addRequestDataAsync||enter");
        synchronized (mSenderLock) {
            Log.d("ScriptEditor|7702", "LLRRConn#addRequestDataAsync||sync||enter");
            if (mainSocketThread != null && asyncSendingReady) {
                Log.d("ScriptEditor|7702", "LLRRConn#addRequestDataAsync||sync||normal");
                mainSocketThread.addRequestDataAsync(data, tag, response);
            } else {
                Log.d("ScriptEditor|7702", "LLRRConn#addRequestDataAsync||sync||wait");
                penddingDataList.add(new SocketSendingThread.Pack(data, tag, response));
            }
        }
        Log.d("ScriptEditor|7702", "LLRRConn#addRequestDataAsync||exit");
    }

    public void connect() {
        if (mainSocketThread != null) {
            mainSocketThread.start();
        }
    }

    public boolean isStarted() {
        return mainSocketThread != null && mainSocketThread.isStarted();
    }
}
