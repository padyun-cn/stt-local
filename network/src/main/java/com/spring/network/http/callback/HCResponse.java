package com.spring.network.http.callback;


import com.uls.utilites.un.Useless;

import java.util.List;

/**
 * Created by daiepngfei on 11/26/18
 */
public class HCResponse<T> {
    T data;
    int code;
    String msg;
    Exception e;
    String dataStr;



    public HCResponse() {
    }

    public HCResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    void throwable(Exception e) {
        this.e = e;
    }

    public String getDataStr() {
        return this.dataStr;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    public T getData() {
        return data;
    }

    public Exception getE() {
        return e;
    }

    public T parseObject(Class<T> cls){
        return Useless.JSONObjParse(dataStr, cls);
    }

    public List<T> parseArray(Class<T> cls) {
        return Useless.JSONArrayParse(dataStr, cls);
    }

    public boolean isSuccess(){
        return code == 0 && dataStr != null;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setE(Exception e) {
        this.e = e;
    }

    public void setDataStr(String dataStr) {
        this.dataStr = dataStr;
    }
}
