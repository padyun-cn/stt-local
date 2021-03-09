package com.spring.network.socket.compat2;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by daiepngfei on 11/6/19
 */
public class CoWriter {

    private OutputStream outputStream;

    /**
     *
     * @param outputStream
     */
    CoWriter(OutputStream outputStream){
        this.outputStream = outputStream;
    }

    /**
     *
     * @param data
     * @param off
     * @param len
     * @throws IOException
     */
    public void write(byte[] data, int off, int len) throws IOException {
        if(outputStream != null){
            outputStream.write(data, off, len);
        }
    }

    /**
     *
     * @param data
     * @throws IOException
     */
    public void write(byte[] data) throws IOException {
        write(data, 0, data.length);
    }

    /**
     *
     * @param data
     * @throws IOException
     */
    public void write(int data) throws IOException {
        if(outputStream != null) {
            outputStream.write(data);
        }
    }

}
