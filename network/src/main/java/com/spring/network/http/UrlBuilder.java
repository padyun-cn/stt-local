package com.spring.network.http;

import com.uls.utilites.un.Useless;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by daiepngfei on 11/26/18
 */
public class UrlBuilder {
    private HashMap<String, String> params = new HashMap<>();
    private String url;

    private UrlBuilder(String url) {
        this.url = url;
    }

    /*public static UrlBuilder path(String path) {
        return url(apiUrl(path));
    }

    private static String apiUrl(String path) {
        return Cons.API.URL(path);
    }*/

    public static UrlBuilder url(String url) {
        return new UrlBuilder(url);
    }

    public UrlBuilder add(String key, String value) {
        params.put(key, value);
        return this;
    }

    public UrlBuilder add(String key, int value) {
        params.put(key, String.valueOf(value));
        return this;
    }

    String build() {
        return processGetUrl(url, params);
    }

    String getRawUrlString(){
        return url;
    }

    private String processGetUrl(String url, Map<String, String> params) {
        final StringBuilder builder = new StringBuilder(url);
        if (params != null) {
            Set<String> keys = params.keySet();
            if (keys.size() > 0) {
                if (builder.charAt(builder.length() - 1) == '/') {
                    builder.delete(builder.length() - 1, builder.length());
                }
                builder.append("?");
                for (String key : keys) {
                    final String value = params.get(key);
                    if (!Useless.isEmpty(value)) {
                        builder.append(key).append("=").append(value).append("&");
                    }
                }
                builder.delete(builder.length() - 1, builder.length());
            }
        }
        return builder.toString();
    }
}
