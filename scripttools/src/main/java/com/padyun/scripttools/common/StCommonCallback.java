package com.padyun.scripttools.common;

import com.padyun.scripttools.biz.ui.data.StRespBaseObj;
import com.uls.utilites.un.Useless;


/**
 * Created by daiepngfei on 2020-06-22
 */
public class StCommonCallback<T> extends StOkBaseObjCallback {

    private Class<T> cls;
    public StCommonCallback(Class<T> cls){
        this.cls = cls;
    }

    @Override
    public final void onBaseResponse(StRespBaseObj t) {
        if (!Useless.isEmpty(t.getDataString())
                && !t.getDataString().equals("null")) {
            onParsedResponse(t.dataAsObject(cls));
        }
    }

    public void onParsedResponse(T t) {

    }

}
