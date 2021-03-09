package com.spring.network.socket.compat2;

/**
 * Created by daiepngfei on 11/19/19
 * 使用这个类，你必须自己负责response的onResponse在onTransaction中的调用
 */
public abstract class AbsSockTransactionRequest extends StatefulWRSockRequest {

    private CoWriter writer;
    private ISockResponse response;

    public void setResponse(ISockResponse response) {
        this.response = response;
    }

    @Override
    public final void onWriting(CoWriter writer) throws Exception {
        this.writer = writer;
    }

    @Override
    public final void onReading(CoReader reader) throws Exception {
        try {
            onTransactions(this.writer, reader);
        } catch (Exception e){
            onSendFail(ERR_EXCEPTION, "", e);
            throw e;
        }
    }

    protected abstract void onTransactions(CoWriter writer, CoReader reader) throws Exception;

    @Override
    public final boolean onResponse(CoReader reader) throws Exception {
        /*if(response != null){
            response.onBaseResponse(reader);
        }*/
        return false;
    }

    @Override
    public final void onSendFail(int errno, String msg, Exception e) {
        if(response != null){
            response.onSendFail(errno, msg, e);
        }
    }
}
