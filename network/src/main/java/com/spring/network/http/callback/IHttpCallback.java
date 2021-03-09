package com.spring.network.http.callback;

import androidx.annotation.NonNull;

/**
 * Created by daiepngfei on 1/12/18
 */

public interface IHttpCallback<T> {
    /**
     * @param response
     */
    void onResponse(@NonNull T response);

    /**
     * @param e
     */
    void onFailure(Exception e);
}
