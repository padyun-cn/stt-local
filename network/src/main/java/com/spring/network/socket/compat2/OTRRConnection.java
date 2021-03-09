package com.spring.network.socket.compat2;

import java.lang.ref.WeakReference;

/**
 * One-Time-Request-Rsponse socket
 * Created by daiepngfei on 11/4/19
 */
public class OTRRConnection extends RequestSocketThread {

    private byte[] data;
    private WeakReference<ISockResponse> callback;

    public OTRRConnection(String ip, int port, byte[] data) {
        this(ip, port, data, "");
    }

    public OTRRConnection(String ip, int port, byte[] data, String tag) {
        this(ip, port, data, tag, null);
    }

    public OTRRConnection(String ip, int port, byte[] data, String tag, ISockResponse callback) {
        super(new BSConfiguration.Builder(ip, port).autoConnect(false).soTimeout(1000).create(), null);
        if (data == null || data.length == 0) {
            setMarkedStopped();
            callback.onSendFail(ISockSendResponse.ERR_EMPTY_BUFFER, " Trying to sending a empty data array! ", new IllegalArgumentException(" Trying to sending a empty data array! "));
            return;
        }
        this.data = data;
        this.callback = new WeakReference<>(callback);
        addRequest(new DataSockRequest(data, tag, callback));
    }

    @Override
    public synchronized void start() {
        // do nothing
    }

    public void send(){
        super.start();
    }

    public void interrupt(){
        setMarkedStopped();
    }

}
