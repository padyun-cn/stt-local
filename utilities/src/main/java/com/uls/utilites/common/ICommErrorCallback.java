package com.uls.utilites.common;

/**
 * Created by daiepngfei on 11/11/19
 */
public interface ICommErrorCallback {
    void onSendFail(int errno, String msg, Exception e);
}
