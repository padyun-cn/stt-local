package com.padyun.scripttools.content.network;


import com.spring.network.socket.compat2.CoReader;
import com.uls.utilites.utils.BitUtils;

/**
 * Created by daiepngfei on 11/19/19
 */
public class CoScriptReaderWrapper {

    /**
     *
     * @param reader
     * @return
     * @throws Exception
     */
    public static byte[] readNextData(CoReader reader) throws Exception {
        final byte[] dataLenBuffer = new byte[4];
        final int result = reader.read(dataLenBuffer, 0, dataLenBuffer.length);
        if (result <= 0) {
            return null;
        }
        final int datalen = BitUtils.bytesToInt(dataLenBuffer, 0);
        if (datalen < 0) {
            return null;
        }
        final byte[] data = new byte[datalen];
        final int dataResult = reader.read(data, 0, data.length);

        return dataResult >= 0 ? data : null;
    }

}
