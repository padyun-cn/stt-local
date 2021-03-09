package com.spring.network.http;

import com.google.gson.Gson;
import com.spring.network.http.callback.HCOriginal;
import com.spring.network.http.callback.HCResponse;
import com.uls.utilites.un.Useless;

import java.util.Map;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by daiepngfei on 11/26/18
 */
class YpNsPoster {

    /**
     *
     * @param url
     * @param params
     * @param headers
     * @param tokenForce
     */
    @SuppressWarnings("SameParameterValue")
    static <T> HCResponse<T> postSync(String url, FormBody params, Map<String, String> headers, boolean tokenForce) {
        Request r = getPostRequest(url, params, headers, tokenForce, null);
        if(r != null) {
            return SimpleHttpRequestor.requestSync(r);
        } else {
            return null;
        }
    }



    /**
     * @param url
     * @param params
     * @param headers
     * @param tokenForce
     * @param callback
     */
    static void post(String url, FormBody params, Map<String, String> headers, boolean tokenForce, HCOriginal callback) {
        Request r = getPostRequest(url, params, headers, tokenForce, callback);
        if(r != null) SimpleHttpRequestor.request(r, callback);
    }



    private static Request getPostRequest(String url, FormBody params, Map<String, String> headers, boolean tokenForce, HCOriginal callback) {
        url = HttpUrl.defaultWithPath(url);
        final Request.Builder b = YpNsRequestBuilder.genTokenForceBuilder(url, tokenForce, callback);
        if (b != null) {
            YpNsHeaders.addCommonHeader(b);
            Request.Builder builder = b.url(url).post(params);
            if (headers != null) {
                Set<String> set = headers.keySet();
                for (String key : set) {
                    final String value = headers.get(key);
                    if (Useless.isEmpty(value)) continue;
                    builder.addHeader(key, value);
                }
            }
            return b.build();
        }
        return null;
    }


    /**
     * @param path
     * @param callback
     */
    public static void postJson(String path, Map<String, Object> params, boolean tokenForce, HCOriginal callback) {
        final Request.Builder b = YpNsRequestBuilder.genTokenForceBuilder(path, tokenForce, callback);
        if (b != null) {
            SimpleHttpRequestor.request(b.url(path).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                    new Gson().toJson(params))).build(), callback);
        }
    }
}
