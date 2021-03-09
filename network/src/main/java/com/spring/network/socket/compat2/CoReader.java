package com.spring.network.socket.compat2;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by daiepngfei on 11/6/19
 */
public class CoReader {
    private InputStream inputStream;
    private OnStateChangedListener onStateChangedListener;

    /**
     *
     * @param inputStream
     * @param stateChangedListener
     */
    CoReader(InputStream inputStream, OnStateChangedListener stateChangedListener) {
        this.inputStream = inputStream;
        this.onStateChangedListener = stateChangedListener;
    }

    /**
     *
     * @param data
     * @return
     * @throws IOException
     */
    public int read(byte[] data) throws Exception {
        return read(data, 0, data.length);
    }

    /**
     *
     * @param data
     * @param offset
     * @param len
     * @return
     * @throws IOException
     */
    public int read(byte[] data, int offset, int len) throws Exception {
        if(onStateChangedListener != null) {
            onStateChangedListener.onConnStateChanged(SockConnState.START_READING);
        }
        int result = -1;
        if (inputStream != null) {
            int readLen = 0;
            while (true){
                // interruption
                if(Thread.currentThread().isInterrupted()){
                    throw new SReadInterruptException("CoReader interruptted at while start");
                }

                if(onStateChangedListener != null) {
                    onStateChangedListener.onConnStateChanged(SockConnState.ON_READING_LOOP);
                }

                // reading
                final int rl = inputStream.read(data, offset + readLen, len - readLen);
                // Dealing with the negative result
                if(rl < 0){
                    throw new SReadInterruptException("CoReader read result <0 as " + rl);
                }
                // sum the readlen
                readLen += rl;
                // check len
                if(readLen == len){
                    break;
                }
            }
            result = readLen;
        }
        if(onStateChangedListener != null) {
            onStateChangedListener.onConnStateChanged(SockConnState.STOP_READING);
        }
        return result;
    }
}
