package com.spring.network.http.callback;

import java.util.List;

/**
 * Created by daiepngfei on 11/26/18
 */
public class HCSimpleDataList<T> extends HCDataList<T> {
    public HCSimpleDataList(Class cls) {
        super(cls);
    }

    @Override
    public void onHandledSuccess(List list) {

    }
}
