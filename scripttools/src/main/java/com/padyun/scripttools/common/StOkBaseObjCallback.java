package com.padyun.scripttools.common;

import android.accounts.NetworkErrorException;

import com.padyun.scripttools.biz.ui.data.StRespBaseObj;
import com.uls.utilites.un.Useless;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by daiepngfei on 2020-06-22
 */
public class StOkBaseObjCallback implements Callback {


    public void onStResponseFailure(int code, String msg, Exception e) {

    }

    public void onBaseResponse(StRespBaseObj t) {

    }

    @Override
    public final void onFailure(@NotNull Call call, @NotNull IOException e) {
        Useless.runOnUiThread(() -> onNetworkError(call, e));
    }

    public void onStRequestSuccess() {

    }

    public void onNetworkError(@NotNull Call call, @NotNull IOException e){

    }

    @Override
    public final void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        try {
            StRespBaseObj obj = StRespBaseObj.parse(response);
            if (obj != null) {
                if (obj.getCode() != 0) {
                    postFailure(obj.getCode(), obj.getMsg(), new NetworkErrorException());
                } else {
                    Useless.runOnUiThread(() -> {
                        onStRequestSuccess();
                        onBaseResponse(obj);
                    });
                }
            } else {
                postFailure(-1, null, new NetworkErrorException());
            }
        } catch (Exception e) {
            e.printStackTrace();
            postFailure(-1, null, e);
        }
    }

    private void postFailure(int code, String msg, Exception e) {
        Useless.runOnUiThread(() -> onStResponseFailure(code, msg, e));
    }


}
