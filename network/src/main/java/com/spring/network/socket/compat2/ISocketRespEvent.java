package com.spring.network.socket.compat2;

/**
 * Created by daiepngfei on 11/12/19
 */
public interface ISocketRespEvent {
    int WRITTING_FINNISHED = 1;
    void onRespEvent(int event);
}
