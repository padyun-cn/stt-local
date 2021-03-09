package com.spring.network.socket.compat2;

/**
 * Created by daiepngfei on 11/11/19
 * step1: write
 * step2: reading
 */
public interface IWRSockRequest extends ISockResponse {

    /**
     *
     * @param writer
     * @throws Exception
     */
    void onWriting(CoWriter writer) throws Exception;

    /**
     *
     * @param reader
     * @throws Exception
     */
    void onReading(CoReader reader) throws Exception;

}
