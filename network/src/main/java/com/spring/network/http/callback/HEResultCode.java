package com.spring.network.http.callback;

/**
 * Created by daiepngfei on 12/17/17
 */

public class HEResultCode extends Exception {

    public static final int NETWORK_ERROR = -100;

    public static String getMsg(Exception e){
        String msg = null;
        if(HEResultCode.class.isInstance(e)){
            HEResultCode ce = (HEResultCode) e;
            msg = ce.getMsg();
        }
        return msg;
    }

    private String msg;
    private int code;

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }

    public HEResultCode(String msg, int code){
        this.msg = msg;
        this.code = code;
    }
}
