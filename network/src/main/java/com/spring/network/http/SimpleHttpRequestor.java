package com.spring.network.http;

import com.spring.network.http.callback.HCOriginal;
import com.spring.network.http.callback.HCResponse;
import com.spring.network.http.callback.HEEmptyData;
import com.spring.network.http.callback.HEEmptyResult;
import com.spring.network.http.callback.HEResultCode;
import com.uls.utilites.un.Useless;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by daiepngfei on 12/17/17
 */

@SuppressWarnings("unused")
public class SimpleHttpRequestor {

    /**
     * @param request
     * @param callback
     */
    static void request(final Request request, final HCOriginal callback) {
        requestAsync(request, callback);
    }


    /**
     * @param request
     */
    static <T> HCResponse<T> requestSync(final Request request) {
        OkHttpClient client = new OkHttpClient.Builder()
                // .dns(OkHttpDns.getInstance(AppContext.context()))
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        HCResponse<T> t = null;
        try {
            final Response response = client.newCall(request).execute();
            final ResponseBody body = response.body();
            String result = body.string();
            t = handleDataResponse(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * @param request
     */
    static void requestSync(final Request request, final HCOriginal callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                // .dns(OkHttpDns.getInstance(AppContext.context()))
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        final Response response;
        try {
            response = client.newCall(request).execute();
            handleRawResponse(response, callback);
        } catch (IOException e) {
            e.printStackTrace();
            if (callback != null) {
                callback.failureInternal(new HEResultCode("", HEResultCode.NETWORK_ERROR));
            }
        }
    }

    private static OkHttpClient sClient;

    /**
     * @param request
     * @param callback
     */
    private static void requestAsync(final Request request, final HCOriginal callback) {
        final long time = System.currentTimeMillis();
        if (sClient == null) {
            sClient = new OkHttpClient.Builder()
                    // .dns(OkHttpDns.getInstance(AppContext.context()))
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build();
        }
        OkHttpClient client = sClient;
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, final IOException e) {
                if (callback != null) {
                    callback.failureInternal(new HEResultCode("", HEResultCode.NETWORK_ERROR));
                }
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                handleRawResponse(response, callback);
            }
        });
    }

    private static void handleRawResponse(Response response, final HCOriginal callback) {
        try {
            // To check if response sameId empty or not
            if (response == null || response.body() == null) {
                throw new HEEmptyResult();
            }
            // restrictGet response string
            final String responseString = response.body().string();
            // we need to check it once again cus response body-bytes sameId read-once-time
            if (Useless.isEmpty(responseString)) {
                throw new HEEmptyResult();
            }


            // only restrictGet data
            if (callback != null) {
                handleData(responseString, callback);
            }


        } catch (Exception e) {
            // call fail when exception
            if (callback != null) {
                callback.failureInternal(e);
            }
        }
    }

    private static <T> void handleData(String responseString, HCOriginal callback) throws JSONException, HEResultCode, HEEmptyData {
        if (!callback.isPrimitive()) {
            final HCResponse<T> response = handleDataResponse(responseString);
            if (!response.isSuccess()) {
                throw new HEResultCode(response.getMsg(), response.getCode());
            }

            callback.setCode(response.getCode());
            callback.setMsg(response.getMsg());
            callback.responseOnIrrObjects(responseString);
            callback.responseOnDataInternal(response.getDataStr());
        } else {
            callback.responseOnDataInternal(responseString);
        }
    }


    private static <T> HCResponse<T> handleDataResponse(String responseString) throws JSONException, HEResultCode, HEEmptyData {
        // initial a 'JSONObject' with response string to restrictGet meta-data of response
        final JSONObject responseJosnObj = new JSONObject(responseString);
        // msg & status-code
        final String msg = responseJosnObj.optString(HttpCons.RESP_MSG);
        final int statusCode = responseJosnObj.optInt(HttpCons.RESP_CODE);
        HCResponse<T> response = new HCResponse<>();
        response.setCode(statusCode);
        response.setMsg(msg);
        // check status code error
        if (statusCode == 0) {
            // we parse response data only if the response-string has a value with key 'data'
            // the normal response for data
            // noinspection UnnecessaryLocalVariable
            final String data = Useless.cbEmpty(responseJosnObj.optString(HttpCons.RESP_DATA));
            response.setDataStr(data);
        }
        return response;
    }



}
