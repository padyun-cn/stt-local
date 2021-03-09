package com.padyun.scripttools.content.network;


import com.padyun.scripttoolscore.content.network.SockResponseUtils;
import com.spring.network.socket.compat2.CoReader;
import com.spring.network.socket.compat2.ISockResponse;
import com.uls.utilites.common.ICCallback;

import androidx.annotation.NonNull;

/**
 * Created by daiepngfei on 11/21/19
 */
public class ScriptStringResponse implements ISockResponse {

    private final ICCallback<String> callback;

    public ScriptStringResponse(ICCallback<String> callback) {
        this.callback = callback;
    }

    @Override
    public final boolean onResponse(CoReader reader) throws Exception {
        final byte[] resp = SockResponseUtils.readNextData(reader);
        if (resp != null && resp.length > 0) {
            if (callback != null) {
                callback.onSuccess(getStringBody(resp));
            }
            return true;
        }
        return false;
    }

    @NonNull
    protected String getStringBody(byte[] resp) throws Exception {
        return new String(resp, "UTF-8");
    }

    @Override
    public final void onSendFail(int errno, String msg, Exception e) {
        if (callback != null){
            callback.onSendFail(errno, msg, e);
        }
    }
}
