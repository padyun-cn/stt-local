package com.spring.network.socket.compat2;


import com.uls.utilites.common.ICommErrorCallback;

/**
 * Created by daiepngfei on 11/11/19
 */
public interface ISockResponse extends ICommErrorCallback {
    int ERR_EMPTY_BUFFER = -1;
    int ERR_EXCEPTION = -2;
    int ERR_BE_SHUT_DOWN = -3;
    int ERR_TYPE_ERROR = -4;
    boolean onResponse(CoReader reader) throws Exception;
}
