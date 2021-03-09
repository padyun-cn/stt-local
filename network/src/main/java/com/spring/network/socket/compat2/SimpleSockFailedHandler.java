package com.spring.network.socket.compat2;


import androidx.core.util.Consumer;

/**
 * Created by daiepngfei on 2020-11-06
 */
public class SimpleSockFailedHandler implements OnStateChangedListener {

    private final Consumer<SockConnState> failed;

    public SimpleSockFailedHandler(Consumer<SockConnState> failedConsumer){
        this.failed = failedConsumer;
    }

    @Override
    public final void onConnStateChanged(SockConnState state) {
        switch (state){
            case ERROR:
            case TIMEOUT_CONN:
            case TIMEOUT_READ:
                if(failed != null){
                    failed.accept(state);
                }
                break;
                default:
        }
    }
}
