package com.padyun.scripttools.biz.ui.data;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by daiepngfei on 2020-06-04
 */
public class StRespBaseObj {
    private String msg;
    private int code;
    private int status;
    private String data;


    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }

    public int getStatus() {
        return status;
    }

    public String getDataString() {
        return data;
    }

    public <T> T dataAsObject(Class<T> cls) {
        try {
            if(String.class == cls){
                //noinspection unchecked
                return (T) data;
            }
            return new Gson().fromJson(data, cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> ArrayList<T> dataAsList(Type typeOf) {
        try {
            return new Gson().fromJson(data, typeOf);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T[] dataAsArray(Class<T[]> cls) {
        try {
            return new Gson().fromJson(data, cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static StRespBaseObj parse(Response response) throws Exception {
        if(response == null || response.body() == null || !response.isSuccessful()){
            return null;
        }
        return parse(response.body().string());
    }

    public static StRespBaseObj parse(String json) throws Exception {
        try {
            if (json != null) {
                final StRespBaseObj result = new StRespBaseObj();
                final JSONObject obj = new JSONObject(json);
                result.code = obj.getInt("code");
                result.status = obj.getInt("status");
                result.msg = obj.getString("msg");
                result.data = obj.optString("data");
                return result;
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    public interface StBaseRespCallback {
        void onFailure(@NotNull Call call, @NotNull IOException e);
        void onResponse(StRespBaseObj t);
    }
}
