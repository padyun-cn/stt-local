package com.spring.network.http.callback;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Created by daiepngfei on 1/12/18
 */

public abstract class HCDataList<T> extends HCPrimary<String> {

    private Class<T> dataCls;
    private List<T> mDataList;
    private Exception exception;

    public HCDataList(Class<T> cls) {
        this(cls, true);
    }

    public HCDataList(Class<T> cls, boolean invalidTokenVerify) {
        super(String.class, invalidTokenVerify);
        this.dataCls = cls;
        setIgnoreEmptyDataError(false);
    }

    @Override
    public final void onResponse(@NonNull String t) {
        if(exception != null) {
            onFailure(exception, 0, null);
        } else {
            onHandledSuccess(mDataList);
        }
    }

    @Override
    protected String onParsingStringData(String data) {
        try {
            this.mDataList = onCustomParssingArray(data);
        } catch (Exception e) {
            this.exception = e;
        }
        return super.onParsingStringData(data);
    }

    protected List<T> onCustomParssingArray(String t) throws JSONException, HEEmptyData {
        final List<T> list = new ArrayList<>();
        final JSONArray arr = new JSONArray(t);
        if (arr.length() > 0) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject object = arr.getJSONObject(i);
                if (object != null) {
                    list.add(new Gson().fromJson(object.toString(), dataCls));
                }
            }
        }
        return list;
    }

    public List<T> getDataList() {
        return mDataList;
    }

    public abstract void onHandledSuccess(List<T> ts);
}
