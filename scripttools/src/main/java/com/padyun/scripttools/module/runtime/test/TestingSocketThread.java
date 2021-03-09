package com.padyun.scripttools.module.runtime.test;

import com.padyun.scripttoolscore.compatible.plugin.Utils;
import com.padyun.scripttoolscore.constants.StConfiguration;
import com.spring.network.socket.compat2.BSConfiguration;
import com.spring.network.socket.compat2.DataSocketParams;
import com.spring.network.socket.compat2.OnStateChangedListener;
import com.spring.network.socket.compat2.RequestSocketThread;
import com.spring.network.socket.compat2.StatefulWRSockRequest;

/**
 *
 */
public class TestingSocketThread {
    private RequestSocketThread socketThread;
    public static final String TAG = "TestingSocketThread#";

    /**
     * @param asIp
     */
    TestingSocketThread(String ip, int port, String asIp, OnStateChangedListener onStateChangedListener, StatefulWRSockRequest... requests) {
        this(ip, port, asIp, 1000, onStateChangedListener, requests);
    }

    TestingSocketThread(String ip, int port, String asIp, int soTimeout, OnStateChangedListener onStateChangedListener, StatefulWRSockRequest... requests) {
        BSConfiguration configuration = new BSConfiguration();
        configuration.setIp(ip);
        configuration.setPort(StConfiguration.Test.PORT_CMD);
        configuration.setSoTimeout(soTimeout);
        configuration.setAsyncSender(false);
        configuration.setIsKeepAlive(false);
        configuration.setAutoReconnectting(true);
        this.socketThread = new RequestSocketThread();
        this.socketThread.setOnStateChangedListener(onStateChangedListener);
        this.socketThread.setName("TestingSocketThread#" + socketThread.getName());
        this.socketThread.setConfiguration(configuration);
//        this.socketThread.addRequests(new DataSockRequest(genIpPortParams(asIp, port), null));
        this.socketThread.addRequests(requests);
    }

    void addRequests(StatefulWRSockRequest... requests) {
        if (socketThread != null) {
            socketThread.addRequests(requests);
        }
    }

    private DataSocketParams genIpPortParams(String asip, final int port) {
        DataSocketParams _params = new DataSocketParams();
        _params.addParam(Utils.ipToBytesByReg(asip));
        byte[] ports = new byte[2];
        ports[0] = (byte) (port >>> 8);
        ports[1] = (byte) (port & 0x00FF);
        _params.addParam(ports);
        return _params;
    }


    boolean isMarkedStopped() {
        return socketThread == null || socketThread.isMarkedStopped();
    }

    void start() {
        if (socketThread != null) {
            socketThread.start();
        }
    }

    boolean startSync() {
        if (socketThread != null) {
            return socketThread.syncRun();
        }
        return false;
    }

    void stop() {
        synchronized (this) {
            if (socketThread != null) {
                socketThread.setMarkedStopped();
            }
        }
    }
}
