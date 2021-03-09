package com.padyun.scripttools.module.runtime.test;

import com.padyun.scripttools.content.network.ITestLogCatcher;
import com.padyun.scripttools.content.network.ScriptStringResponse;
import com.padyun.scripttoolscore.compatible.data.model.SEImage;
import com.padyun.scripttoolscore.compatible.plugin.FairyProtocol;
import com.padyun.scripttoolscore.compatible.plugin.StringModule;
import com.padyun.scripttoolscore.compatible.plugin.TypeModule;
import com.padyun.scripttoolscore.compatible.plugin.Utils;
import com.padyun.scripttoolscore.constants.StConfiguration;
import com.padyun.scripttoolscore.content.network.SockResponseUtils;
import com.spring.network.socket.compat2.AbsSockTransactionRequest;
import com.spring.network.socket.compat2.CoReader;
import com.spring.network.socket.compat2.CoWriter;
import com.spring.network.socket.compat2.DataSockRequest;
import com.spring.network.socket.compat2.ISockSendResponse;
import com.spring.network.socket.compat2.OnStateChangedListener;
import com.spring.network.socket.compat2.SockConnState;
import com.spring.network.socket.compat2.StatefulWRSockRequest;
import com.uls.utilites.common.ICCallback;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;

/**
 *
 */
public class TestProxy {
    private final String ip;
    private final String asip;
    private TestingSocketThread logThread;


    public TestProxy(String ip, String asip) {
        this.ip = ip;
        this.asip = asip;
    }

    /**
     * @param cacher
     */
    public void startRetrivingLogs(ITestLogCatcher cacher, ISockSendResponse sockSendResponse) {
        if (cacher != null) {
            if (logThread != null && !logThread.isMarkedStopped()) {
                logThread.stop();
                logThread = null;
            }
            final byte[] data = new TypeModule(FairyProtocol.REQ_CMD_SCRIPT_LOG).toDataWithLen().array();
            logThread = new TestingSocketThread(ip, StConfiguration.Test.PORT_CMD, asip, 0, new CmdConnectErrorHandler(sockSendResponse),
                    new LogScriptRequest(data, sockSendResponse, cacher)
            );
            logThread.start();
        }
    }

    private void requestCmd(int cmdPort, OnStateChangedListener onStateChangedListener, StatefulWRSockRequest... requests) {
        new TestingSocketThread(ip, cmdPort, asip, onStateChangedListener, requests).start();
    }

    public boolean updateImageSync(SEImage image) {
        final StatefulWRSockRequest request = new AbsSockTransactionRequest() {
            @Override
            protected void onTransactions(CoWriter writer, CoReader reader) throws Exception {
                ImageUploadRequest.uploadImageFiles(writer, Collections.singletonList(image));
            }
        };
        new TestingSocketThread(ip, StConfiguration.Test.PORT_CMD, asip, null, request).startSync();
        return request.isSuccess();
    }

    public void updateImage(SEImage image, SockResponseUtils.SimpleOkResponse response) {
        final AbsSockTransactionRequest request = new AbsSockTransactionRequest() {
            @Override
            protected void onTransactions(CoWriter writer, CoReader reader) throws Exception {
                ImageUploadRequest.uploadImageFiles(writer, Collections.singletonList(image));
            }
        };
        request.setResponse(response);
        requestCmd(StConfiguration.Test.PORT_CMD, null, request);
    }


    private static class CmdConnectErrorHandler implements OnStateChangedListener {
        private ISockSendResponse response;

        public CmdConnectErrorHandler(ISockSendResponse response) {
            this.response = response;
        }


        @Override
        public void onConnStateChanged(SockConnState state) {
            switch (state){
                case ERROR:
                case TIMEOUT_CONN:
                case TIMEOUT_READ:
                    if(response != null){
                        response.onSendFail(-1, "网络错误，请稍候重试", null);
                    }
                    break;
                default:
            }
        }
    }

    /**
     * @param script
     */
    public void sendStartCmd(final String script, List<SEImage> images, SockResponseUtils.SimpleOkResponse response) {

        StringModule stringModule = new StringModule(FairyProtocol.TYPE_START2);
        stringModule.str = script;
        requestCmd(StConfiguration.Test.PORT_CMD2, new CmdConnectErrorHandler(response), new DataSockRequest(stringModule.toDataWithLen().array(), response));

        /*requestCmd(StConfiguration.Test.PORT_CMD, new CmdConnectErrorHandler(response), new ImageUploadRequest(images, new SockResponseUtils.SimpleResponse() {

            @Override
            public void onSendFail(int errno, String msg, Exception e) {
                if (response != null) {
                    response.onSendFail(errno, msg, e);
                }
            }

            @Override
            public void onSendingCompelete() {
                // request start with script text
                if (!Useless.isEmpty(script)) {
                    StringModule stringModule = new StringModule(FairyProtocol.TYPE_START2);
                    stringModule.str = script;
                    requestCmd(StConfiguration.Test.PORT_CMD2, new CmdConnectErrorHandler(response), new DataSockRequest(stringModule.toDataWithLen().array(), response));
                } else if (response != null) {
                    response.onSendFail(response.ERR_EMPTY_BUFFER, "Null Script text", new IllegalArgumentException());
                }
            }
        }));*/
    }

    /**
     * @param response
     */
    public void sendCmdStop(SockResponseUtils.SimpleOkResponse response) {
        final byte[] data = new TypeModule(FairyProtocol.TYPE_STOP2).toDataWithLen().array();
        final StatefulWRSockRequest request = new DataSockRequest(data, "cmd script stop", response);
        requestCmd(StConfiguration.Test.PORT_CMD2, new CmdConnectErrorHandler(response), request);
    }

    /**
     * @param response
     */
    public void sendCmdPause(SockResponseUtils.SimpleOkResponse response) {
        final byte[] data = new TypeModule(FairyProtocol.TYPE_PAUSE2).toDataWithLen().array();
        requestCmd(StConfiguration.Test.PORT_CMD2, new CmdConnectErrorHandler(response), new DataSockRequest(data, "cmd script pause", response));
    }

    /**
     *
     */
    public void sendCmdResume(SockResponseUtils.SimpleOkResponse response) {
        final TypeModule stringModule = new TypeModule(FairyProtocol.TYPE_RESUME2);
        requestCmd(StConfiguration.Test.PORT_CMD2, new CmdConnectErrorHandler(response), new DataSockRequest(stringModule.toDataWithLen().array(), "cmd script resume", response));
    }

    /**
     * @param callback
     */
    public void getCurrentRunningScript(ICCallback<String> callback) {
        final TypeModule stringModule = new TypeModule(FairyProtocol.REQ_CMD_CHECK_RUNNING_SCRIPT_STATE);
        requestCmd(StConfiguration.Test.PORT_CMD, null, new DataSockRequest(stringModule.toDataWithLen().array(), "cmd script currentRunning", new ScriptStringResponse(callback) {
            @NonNull
            @Override
            protected String getStringBody(byte[] resp) throws Exception {
                final short respCmd = Utils.bytesToShort(resp, 0);
                if (respCmd == FairyProtocol.RESP_TYPE_CHECK_RUNNING_SCRIPT_STATE) {
                    StringModule module = new StringModule(resp, 2, resp.length - 2);
                    return module.str;
                }
                throw new UnsupportedEncodingException();
            }
        }));
    }

    /**
     *
     */
    public void stopRetrivingLogs() {
        release();
    }

    private void release() {
        synchronized (this) {
            if (!isStopped()) {
                logThread.stop();
                logThread = null;
            }
        }
    }


    boolean isStopped() {
        synchronized (this) {
            return logThread == null || logThread.isMarkedStopped();
        }
    }
}
