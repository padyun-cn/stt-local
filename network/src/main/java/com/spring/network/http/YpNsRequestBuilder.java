package com.spring.network.http;


import com.spring.network.http.callback.HCOriginal;

import androidx.annotation.Nullable;
import okhttp3.Request;

/**
 * Created by daiepngfei on 11/26/18
 */
class YpNsRequestBuilder {
    @Nullable
    static Request.Builder genTokenForceBuilder(String url, boolean tokenForce, HCOriginal callback) {
        Request.Builder b = new Request.Builder();
       /* final String token = V2AccountDelegate.getToken()  *//*url.contains("AutoLogin") ? "123" : V2AccountDelegate.getToken()*//* ;
        if (!Cnv.emptysAll(token)) b.header("token", token);
        else if (tokenForce) {
            if (callback != null) {
                callback.onFailure(new HENoToken());
            }
            return null;
        }*/
        return b;
    }

}
