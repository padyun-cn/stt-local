package com.padyun.scripttools.content.network;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daiepngfei on 11/20/19
 */
public class ScriptRequestParam {
    private String ip;
    private String asip;
    private List<byte[]> params = new ArrayList<>();
    public ScriptRequestParam(String ip, String asip, byte[]... bytes) {
        this.ip = ip;
        this.asip = asip;
    }

    /*public byte[][] getParams() {
        DataSocketParams _params = new DataSocketParams();
        _params.addParam(Utils.ipToBytesByReg(asip));
        byte[] ports = new byte[2];
        ports[0] = (byte) (ScriptTestConfig.TEST_SCRIPT_PORT >>> 8);
        ports[1] = (byte) (ScriptTestConfig.TEST_SCRIPT_PORT & 0x00FF);
        _params.addParam(ports);
        return _params;
    }*/

}
