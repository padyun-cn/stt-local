package com.padyun.scripttools.module.runtime.test;

import com.padyun.scripttools.content.network.ITestLogCatcher;
import com.padyun.scripttoolscore.compatible.plugin.RuntimeInfo;
import com.padyun.scripttoolscore.compatible.plugin.StringModule;
import com.padyun.scripttoolscore.content.network.SockResponseUtils;
import com.spring.network.socket.compat2.CoReader;
import com.spring.network.socket.compat2.DataSockRequest;
import com.spring.network.socket.compat2.DataSocketParams;
import com.spring.network.socket.compat2.ISockSendResponse;
import com.uls.utilites.un.Useless;

import java.lang.ref.WeakReference;

/**
 *
 */
public class LogScriptRequest extends DataSockRequest {
    private WeakReference<ITestLogCatcher> logCatcherWeakReference;
    private ISockSendResponse response;

    LogScriptRequest(byte[] params, ISockSendResponse response, ITestLogCatcher catcher) {
        this.response = response;
        setParams(new DataSocketParams(params));
        setResponse(new ISockSendResponse() {
            @Override
            public boolean onResponse(CoReader reader) throws Exception {
                while (true) {
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                    final byte[] data = SockResponseUtils.readNextData(reader);
                    if (data != null) {
                        StringModule runtimeModule = new StringModule(data, 2, data.length - 2);
                        RuntimeInfo runtimeInfo = new RuntimeInfo(runtimeModule.str);
                        deliverLogInfos(runtimeInfo);
                    } else {
                        break;
                    }
                }
                return false;
            }

            @Override
            public void onSendingCompelete() {
                if (response != null) response.onSendingCompelete();
            }

            private void deliverLogInfos(RuntimeInfo runtimeInfo) {
                synchronized (this) {
                    Useless.weakPerform(logCatcherWeakReference, w -> w.onContinueCachingLogs(runtimeInfo));
                }
            }

            @Override
            public void onSendFail(int errno, String msg, Exception e) {
                if (response != null) response.onSendFail(errno, msg, e);
            }
        });
        logCatcherWeakReference = new WeakReference<>(catcher);
    }

    @Override
    protected void onWrittingFinished() {
        if (response != null) response.onSendingCompelete();
    }
}
