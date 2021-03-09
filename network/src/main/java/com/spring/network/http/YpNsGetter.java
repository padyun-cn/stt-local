package com.spring.network.http;

import com.spring.network.http.callback.HCOriginal;

import okhttp3.Request;

/**
 * Created by daiepngfei on 11/26/18
 */
class YpNsGetter {
    /**
     * @param builder
     * @param callback
     */
    public static void restrictGet(UrlBuilder builder, HCOriginal callback) {
        get(builder, true, callback);
    }


    /**
     * @param builder
     * @param callback
     */
    public static void loosenGet(UrlBuilder builder, HCOriginal callback) {
        get(builder, false, callback);
    }

    /**
     * @param builder
     * @param tokenForce
     * @param callback
     */
     static void get(UrlBuilder builder, boolean tokenForce, HCOriginal callback) {
        get(builder, tokenForce, false, callback);
    }



    /**
     *
     * @param builder
     * @param tokenForce
     * @param sync
     * @param callback
     */
     static <T> void get(UrlBuilder builder, boolean tokenForce, boolean sync, HCOriginal<T> callback) {
        final Request.Builder b = YpNsRequestBuilder.genTokenForceBuilder(builder.getRawUrlString(), tokenForce, callback);
        if (b != null) {
            YpNsHeaders.addCommonHeader(b);
            Request request = b.url(builder.build()).build();
            if(sync){
                SimpleHttpRequestor.requestSync(request, callback);
            } else {
                SimpleHttpRequestor.request(request, callback);
            }
        }
    }




}
