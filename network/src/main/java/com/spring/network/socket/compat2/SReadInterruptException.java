package com.spring.network.socket.compat2;

/**
 * Created by daiepngfei on 11/15/19
 */
public class SReadInterruptException extends InterruptedException {
    private String cmsg;
    public SReadInterruptException(String s){
        this.cmsg = s;
    }

    public String getCmsg() {
        return cmsg == null ? " NON " : cmsg;
    }
}
