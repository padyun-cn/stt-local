package com.spring.network.http;

import com.spring.network.BuildConfig;

/**
 * Created by daiepngfei on 2021-01-12
 */
public class HttpUrl {

    private static final String SCHEME_DEBUG = "http://";
    private static final String SCHEME_RELEASE = "https://";
    private static final String HOST_DEBUG = "xxxx";
    private static final String HOST_RELEASE = "xxxx";
    private static final String PORT_DEBUG = ":8080";
    private static final String PORT_RELEASE = "";
    private static boolean isUsingHttps = !BuildConfig.DEBUG;

    public static void setIsUsingHttps(boolean isUsingHttps) {
        HttpUrl.isUsingHttps = isUsingHttps;
    }

    public static String defaultWithPath(String path){
        final String scheme = isUsingHttps ? SCHEME_RELEASE : SCHEME_DEBUG;
        final String host = BuildConfig.DEBUG ? HOST_DEBUG : HOST_RELEASE;
        final String port = BuildConfig.DEBUG ? PORT_DEBUG : PORT_RELEASE;
        return scheme + host + port + (path.startsWith("/") ? path :  "/" + path);
    }
}
