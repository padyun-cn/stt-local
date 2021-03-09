package com.padyun.scripttoolscore.models;

import com.uls.utilites.un.Useless;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by daiepngfei on 2020-06-29
 */
public class HttpParam {
    String key;
    String value;

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public HttpParam(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static Map<String, String> toMap(HttpParam... params) {
        if (params == null) {
            return null;
        }

        final HashMap<String, String> map = new HashMap<>();
        for (HttpParam p : params) {
            if (p == null || !Useless.noEmptyStr(p.key, p.value)) {
                continue;
            }
            map.put(p.key, p.value);
        }
        return map;
    }

    public static HttpParam[] with(HttpParam... params) {
        return params;
    }
}
