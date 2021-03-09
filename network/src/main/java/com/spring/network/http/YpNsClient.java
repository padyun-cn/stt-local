package com.spring.network.http;

import com.spring.network.http.callback.HCOriginal;
import com.spring.network.http.callback.HCResponse;

import java.util.Map;

import okhttp3.FormBody;

/**
 * Created by daiepngfei on 3/19/18
 */

public class YpNsClient {

    /**
     * @param builder
     * @param callback
     */
    public static void restrictGet(UrlBuilder builder, HCOriginal callback) {
        YpNsGetter.get(builder, true, callback);
    }

    /**
     * @param builder
     * @param onGameUploading
     */

    /**
     * @param builder
     * @param callback
     */
    public static void restrictGetSync(UrlBuilder builder, HCOriginal callback) {
        YpNsGetter.get(builder, true, true, callback);
    }


    /**
     * @param builder
     * @param callback
     */
    public static void loosenGet(UrlBuilder builder, HCOriginal callback) {
        YpNsGetter.get(builder, false, callback);
    }


    /**
     * @param path
     * @param params
     * @param callback
     */
    public static void restrictPost(String path, FormBody params, HCOriginal callback) {
        YpNsPoster.post(path, params, null, true, callback);

    }

    /**
     * @param path
     * @param params
     */
    public static <T> HCResponse<T> restrictPostSync(String path, FormBody params) {
        return YpNsPoster.postSync(path, params, null, true);
    }

    /**
     * @param path
     * @param params
     * @param callback
     */
    public static void loosenPost(String path, FormBody params, HCOriginal callback) {
        YpNsPoster.post(path, params, null, false, callback);
    }


    /**
     * @param path
     * @param params
     * @param callback
     */
    public static void restrictPost(String path, FormBody params, Map<String, String> headers, HCOriginal callback) {
        YpNsPoster.post(path, params, headers, true, callback);
    }

    /**
     * @param path
     * @param params
     * @param callback
     */
    public static void loosenPost(String path, FormBody params, Map<String, String> headers, HCOriginal callback) {
        YpNsPoster.post(path, params, headers, false, callback);
    }

}
