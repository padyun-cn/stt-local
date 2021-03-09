package com.spring.network.socket.compat2;

import android.util.Log;

import com.uls.utilites.content.MarkableThread;
import com.uls.utilites.utils.LogUtil;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by daiepngfei on 10/28/19
 */
class SocketSendingThread extends MarkableThread {

    private final BlockingQueue<Pack> queue;
    private CoWriter stream;
    private boolean isMarkedErrorStop = false;

    static class Pack {
        byte[] data;
        String tag;
        ISockSendResponse callback;

        Pack(byte[] buffer, String tag, ISockSendResponse callback) {
            this.data = buffer;
            this.tag = tag;
            this.callback = callback;
        }
    }

    SocketSendingThread() {
        this.queue = new LinkedBlockingDeque<>();
        setName(getClass() + "-" + getName());
    }

    void setWriter(CoWriter writer) {
        this.stream = writer;
    }

    void put(byte[] buffer, String tag, ISockSendResponse response) {
        LogUtil.i("SocketSending", "out-put-in-putting [" + tag + "] ");
        Log.d("ScriptEditor|7702", "SocketSendingThread||put||sync#put");
        if (isMarkedStopped() || Thread.currentThread().isInterrupted()) {
            Log.d("ScriptEditor|7702", "SocketSendingThread||put||sync#put-failed");
            return;
        }
        try {
            LogUtil.i("SocketSending", "out-put-in-putting-pre [" + tag + "] ");
            queue.put(new Pack(buffer, tag, response));
            Log.d("ScriptEditor|7702", "SocketSendingThread||put||sync#put-done");
            LogUtil.i("SocketSending", "out-put-in-putting-success [" + tag + "] ");
        } catch (InterruptedException e) {
            e.printStackTrace();
            LogUtil.i("SocketSending", "out-put-in-putting-exception [" + tag + "] ");
            Log.d("ScriptEditor|7702", "SocketSendingThread||put||sync#put-error-pre");
            setMarkedStopped();
            Log.d("ScriptEditor|7702", "SocketSendingThread||put||sync#put-error-done");
        }
    }

    @Override
    public void run() {
        while (true) {

            Pack pack = null;

            try {
                // check stream state
                if (stream == null) {
                    try {
                        Thread.sleep(50);
                    } catch (Exception e) {
                        pack = queue.peek();
                        throw e;
                    }
                    continue;
                }
                // check conn state
                if (isMarkedStopped() || isInterrupted()) {
                    pack = queue.peek();
                    throw new InterruptedException();
                }

                // get pack
                pack = queue.take();

                if (pack == null || pack.data == null || pack.data.length == 0) {
                    notifyOnFailed(pack, new IllegalArgumentException(), ISockSendResponse.ERR_EMPTY_BUFFER, "Trying to put an empty buffer array ! ");
                    continue;
                }

                if(pack.tag != null){
                    LogUtil.i("SocketSending", "sending [" + pack.tag + "], data size " + pack.data.length);
                }

                // put data buffer
                stream.write(pack.data, 0, pack.data.length);

                // notify on compete
                if(pack.callback != null){
                    pack.callback.onSendingCompelete();
                }
                // CUtils.weakPerform(pack.callback, callback -> handler.post(callback::onSendingCompelete));

            } catch (Exception e) {
                if (InterruptedException.class.isInstance(e) && isMarkedStopped()) {
                    notifyOnFailed(pack, e, ISockSendResponse.ERR_BE_SHUT_DOWN, "Thread is Shutting down!");
                    return;
                }
                synchronized (this) {
                    isMarkedErrorStop = true;
                    setMarkedStopped();
                }
                notifyOnFailed(pack, e, ISockSendResponse.ERR_EXCEPTION, "Error with socket");
                e.printStackTrace();
            }
        }
    }

    public boolean isMarkedErrorStop() {
        synchronized (this) {
            return isMarkedStopped() && isMarkedErrorStop;
        }
    }

    private void notifyOnFailed(Pack pack, Exception e, int errException, String s) {
        if (pack != null && pack.callback != null) {
            pack.callback.onSendFail(errException, s, e);
            //CUtils.weakPerform(pack.callback, t -> handler.post(() -> t.onSendFail(errException, s, e)));
        }
    }

}
