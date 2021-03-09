package com.spring.network.socket.compat2;


import com.uls.utilites.utils.BitUtils;

/**
 * Created by daiepngfei on 11/11/19
 */
public abstract class AbsScriptResponse implements ISockResponse {

    private byte[] dataLenBuffer = new byte[4];

    public boolean onResponse(CoReader reader) throws Exception {

        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }

        final int result = reader.read(dataLenBuffer, 0, dataLenBuffer.length);

        if (result <= 0) {
            return false;
        }

        final int datalen = BitUtils.bytesToInt(dataLenBuffer, 0);
        if (datalen < 0) {
            return false;
        }

        final byte[] data = new byte[datalen];
        final int dataResult = reader.read(data, 0, data.length);

        return dataResult >= 0 && onScriptResponse(data, datalen);
    }

    public abstract boolean onScriptResponse(byte[] data, int len) throws Exception;
}
