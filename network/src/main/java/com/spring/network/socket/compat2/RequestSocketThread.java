package com.spring.network.socket.compat2;


import android.util.Log;

import com.uls.utilites.content.MarkableThread;
import com.uls.utilites.un.Useless;
import com.uls.utilites.utils.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

import static com.spring.network.socket.compat2.StatefulWRSockRequest.State.FINIEDED;
import static com.spring.network.socket.compat2.StatefulWRSockRequest.State.READING;
import static com.spring.network.socket.compat2.StatefulWRSockRequest.State.WRITING;


/**
 * Created by daiepngfei on 10/28/19
 */
@SuppressWarnings("WeakerAccess")
public class RequestSocketThread extends MarkableThread {
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private SockConnState currentState = SockConnState.UNINIT;
    private BSConfiguration configuration;
    private OnStateChangedListener onConnStateChangedListener;
    private LinkedBlockingDeque<StatefulWRSockRequest> requests = new LinkedBlockingDeque<>();
    private SocketSendingThread sendingThread;
    private AtomicInteger retryCount = new AtomicInteger(0);
    private volatile boolean isConnectted = false;


    /**
     *
     */
    public RequestSocketThread() {
        setName(getClass().getSimpleName() + "#" + getName());
    }

    /**
     * @param configuration
     * @param onConnStateChangedListener
     */
    public RequestSocketThread(BSConfiguration configuration, OnStateChangedListener onConnStateChangedListener) {
        this();
        this.configuration = configuration;
        this.onConnStateChangedListener = onConnStateChangedListener;
    }

    /**
     * @param configuration
     */
    public void setConfiguration(BSConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * @param onConnStateChangedListener
     */
    public void setOnStateChangedListener(OnStateChangedListener onConnStateChangedListener) {
        this.onConnStateChangedListener = onConnStateChangedListener;
    }

    /**
     * @param request
     */
    public void addRequest(StatefulWRSockRequest request) {
        try {
            requests.put(request);
        } catch (InterruptedException e) {
            setMarkedStopped();
            e.printStackTrace();
        }
    }

    /**
     * @param requests
     */
    public void addRequests(StatefulWRSockRequest... requests) {
        Useless.foreach(requests, this::addRequest);
    }

    /**
     *
     */
    public void clearRequests() {
        if (requests != null) {
            requests.clear();
        }
    }

    /**
     * @param data
     * @param tag
     * @param response
     */
    public void addRequestDataAsync(byte[] data, String tag, ISockSendResponse response) {
        Log.d("ScriptEditor|7702", "RequestSocketThread||addRequestDataAsync||tag=" + tag);
        synchronized (this) {
            Log.d("ScriptEditor|7702", "addRequestDataAsync||sync#START");
            if (sendingThread != null && isConnectted()) {
                if (sendingThread.isMarkedErrorStop()) {
                    Log.d("ScriptEditor|7702", "addRequestDataAsync||sync#ERROR-STOP");
                    setCurrentStateWithError(SockConnState.ERROR, new SockSendingError());
                    sendingThread = null;
                    closeSocket();
                    setCurrentState(SockConnState.TERMINATED);
                } else {
                    Log.d("ScriptEditor|7702", "addRequestDataAsync||sync#Normal");
                    sendingThread.put(data, tag, response);
                }
            }
        }
        Log.d("ScriptEditor|7702", "addRequestDataAsync||sync#END");
    }

    public boolean syncRun(){
        this.run();
        return getCurrentState() == SockConnState.COMPLETE;
    }

    /**
     *
     */
    private void makeConnectionAndDealRequests() {
        StatefulWRSockRequest request = null;
        try {
            // configuration
            if (configuration == null || Useless.isEmpty(configuration.getIp())) {
                return;
            }
            // get a start-time for timeout checking
            final long startedTime = System.currentTimeMillis();
            // establish a connection
            while (true) {
                try {
                    // timeout
                    if (System.currentTimeMillis() - startedTime > configuration.getConnectTimeout()) {
                        throw new SoConnTimeoutException();
                    }

                    // update state
                    setCurrentState(SockConnState.CONNECTTING);

                    // toCreate a new socket
                    final Socket socket = new Socket();
                    SocketAddress address = new InetSocketAddress(configuration.getIp(), configuration.getPort());
                    socket.connect(address);
                    socket.setSoTimeout(configuration.getSoTimeout());
                    // check connection state
                    if (socket.isConnected()) {
                        this.inputStream = socket.getInputStream();
                        this.outputStream = socket.getOutputStream();
                        this.socket = socket;
                        setCurrentState(SockConnState.CONNECTTED);
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Thread.sleep(50);
                }
            }

            // check socket connection state
            if (getCurrentState() != SockConnState.CONNECTTED) {
                setCurrentState(SockConnState.RECONNECTING);
                throw new IOException();
            }

            // set isConnectted true
            this.isConnectted = true;

            // networking
            // interrupt check
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }

            // gen a reader & writer
            final CoReader reader = new CoReader(inputStream, this::setCurrentState);
            final CoWriter writer = new CoWriter(outputStream);

            // async sender
            if (configuration.isAsyncSender()) {
                sendingThread = new SocketSendingThread();
                sendingThread.setWriter(writer);
            }

            // ready to work
            setCurrentState(SockConnState.READY_TO_WORK);

            //retryCount.set(0);


            // looping & taking request from queue
            while (true) {
                // thread interrupted & stop
                if (Thread.currentThread().isInterrupted() || isMarkedStopped()) {
                    throw new InterruptedException();
                }
                // is keep alive
                if (requests.isEmpty() && !configuration.isKeepAlive()) {
                    break;
                }
                // take request
                request = requests.take();

                // writting & reading
                request.setState(WRITING);
                request.onWriting(writer);
                // reading
                request.setState(READING);
                request.onReading(reader);
                // over
                request.setState(FINIEDED);
                request = null;
            }

            // set complete
            setCurrentState(SockConnState.COMPLETE);
        } catch (Exception e) {

            this.isConnectted = false;

            // mark state as disconnectted
            setCurrentState(SockConnState.DISCONNECTED);

            // check out SocketTimeoutException
            if (e instanceof SoConnTimeoutException) {
                setCurrentState(SockConnState.TIMEOUT_CONN);
                setMarkedStopped();
            }

            // check out SocketTimeoutException
            if (e instanceof SocketTimeoutException) {
                setCurrentState(SockConnState.TIMEOUT_READ);
                //if(retryCount.intValue() >= configuration.getRetryCount()) {
                setMarkedStopped();
                //}
            }

            // check out SReadInterruptException
            if (e instanceof SReadInterruptException) {
                setCurrentStateWithError(SockConnState.ERROR, e);
                Log.e("RequesetSocketThread", ((SReadInterruptException)e).getCmsg(), e);
                setMarkedStopped();
                if (request != null) {
                    request.onSendFail(ISockResponse.ERR_EXCEPTION, "Unexpected Result When Reading !" , e);
                }
            }

            // other wise
            if (!isMarkedStopped()) {
                // error
                if (!(e instanceof InterruptedException)
                        && !(e instanceof IOException)
                        && !(e instanceof SocketException)) {
                    e.printStackTrace();
                    setCurrentStateWithError(SockConnState.ERROR, e);
                    if (request != null) {
                        request.onSendFail(ISockResponse.ERR_EXCEPTION, "Unexpected Result !", e);
                    }
                } else {
                    // set current state
                    setCurrentState(SockConnState.RECONNECTING);
                    // put back request if possible
                    if (request != null) {
                        if (!requests.contains(request)) {
                            requests.addFirst(request);
                        }
                    }
                    // restart network again
                    final StatefulWRSockRequest r = request;
                    restart(() -> {
                        setCurrentStateWithError(SockConnState.ERROR, e);
                        if (r != null) {
                            r.onSendFail(ISockResponse.ERR_EXCEPTION, "Unexpected Result !", e);
                        }
                    });
                    return;
                }
            } else {
                if (request != null) {
                    request.setError(new StatefulWRSockRequest.Error(e, -1, ""));
                }
            }

        }

        // set disconnected
        this.isConnectted = false;

        // close socket
        closeSocket();
        // temrinate
        setCurrentState(SockConnState.TERMINATED);
    }


    /**
     * @param runnable
     */
    private void restart(Runnable runnable) {
        if (retryCount.getAndIncrement() < configuration.getRetryCount()) {
            closeSocket();
            try {
                Thread.sleep(retryCount.intValue() * 500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            makeConnectionAndDealRequests();
        } else if (runnable != null) {
            runnable.run();
        }
    }

    /**
     *
     */
    private void closeSocket() {
        synchronized (this) {
            setCurrentState(SockConnState.CLOSING_SOCKET);
            if (socket != null) {
                try {
                    socket.close();
                    sendingThread = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                setCurrentState(SockConnState.CLOSED);
            }
        }
    }


    /**
     * @param state
     */
    void setCurrentState(SockConnState state) {
        setCurrentStateWithError(state, null);
    }

    /**
     * @param state
     * @param e
     */
    void setCurrentStateWithError(SockConnState state, Exception e) {
        synchronized (this) {
            this.currentState = state;
            final String msg = "[" + Thread.currentThread().getName() + "] Current state is " + state.name();
            final String intervalTag = state == SockConnState.START_READING || state == SockConnState.STOP_READING ? msg : null;
            if (e != null) {
                LogUtil.e("AbsSockConnThread", msg, e, intervalTag, 5000);
            } else {
                LogUtil.d("AbsSockConnThread", msg, intervalTag, 5000);
            }
            if (onConnStateChangedListener != null) {
                onConnStateChangedListener.onConnStateChanged(state);
            }
        }
    }

    @Override
    public void setMarkedStopped() {
        synchronized (this) {
            super.setMarkedStopped();
            if (sendingThread != null) {
                sendingThread.setMarkedStopped();
            }
            if (getCurrentState() == SockConnState.START_READING || getCurrentState() == SockConnState.ON_READING_LOOP) {
                setCurrentState(SockConnState.FORCE_CLOSING);
                closeSocket();
                setCurrentState(SockConnState.TERMINATED);
            }
        }
    }

    /**
     * @return
     */
    private SockConnState getCurrentState() {
        synchronized (this) {
            return currentState;
        }
    }

    /**
     * @return
     */
    OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public void run() {
        makeConnectionAndDealRequests();
    }

    /**
     *
     * @return
     */
    public boolean isConnectted() {
        synchronized (this) {
            return !isMarkedStopped() && isMarkedStarted() && isConnectted;
        }
    }

    public boolean isStopped(){
        synchronized (this) {
            return isMarkedStopped() && isMarkedStarted();
        }
    }

    public boolean isDisconnectted() {
        synchronized (this) {
            return isStopped() && !isConnectted;
        }
    }

    public void runAsyncSender() {
        if (sendingThread != null) {
            sendingThread.start();
        }
    }

    public boolean isStarted() {
        synchronized (this) {
            return isMarkedStarted() && !isMarkedStopped();
        }
    }
}
