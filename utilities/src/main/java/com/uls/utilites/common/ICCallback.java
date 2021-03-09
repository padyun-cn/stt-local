package com.uls.utilites.common;

/**
 * Created by daiepngfei on 11/14/19
 */
public interface ICCallback<T> extends ICommErrorCallback {
    void onSuccess(T t);
}
