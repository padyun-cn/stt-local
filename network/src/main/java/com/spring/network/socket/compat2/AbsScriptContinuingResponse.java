package com.spring.network.socket.compat2;

/**
 * Created by daiepngfei on 11/11/19
 */
public abstract class AbsScriptContinuingResponse extends AbsScriptResponse {

    private byte[] dataLenBuffer = new byte[4];

    public final boolean onResponse(CoReader reader) throws Exception {
        while (true) {
            if(!super.onResponse(reader)) {
                break;
            }
        }
        return true;
    }

}
