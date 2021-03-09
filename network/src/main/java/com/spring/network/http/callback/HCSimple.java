package com.spring.network.http.callback;

import android.app.Activity;

import androidx.annotation.NonNull;

/**
 * Created by daiepngfei on 1/30/18
 */

public class HCSimple extends HCPrimary<String> {

    public HCSimple() {
        super(String.class);
        setIgnoreEmptyDataError(true);
    }

    public HCSimple(Activity act){
        super(act, String.class);
        setIgnoreEmptyDataError(true);
    }

    @Override
    public void onFailure(Exception e, int code, String msg) {
    }

    @Override
    public final void onResponse(@NonNull String response) {
        super.onResponse(response);
    }

    @Override
    protected void onRequestSuccess() {
        super.onRequestSuccess();
        onSuccess();
    }

    public void onSuccess(){
    }

}