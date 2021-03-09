package com.spring.network.http.callback;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.uls.utilites.content.ToastUtils;
import com.uls.utilites.un.Useless;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;

/**
 * Created by daiepngfei on 12/21/17
 */

@SuppressWarnings({"WeakerAccess", "unchecked"})
public abstract class HCOriginal<T> implements IHttpCallback<T> {
    protected Class<T> cls;
    private Handler handler;
    private boolean isPrimitive;
    private Boolean isIgnoreEmptyDataError = null;
    private int code;
    private String msg;
    private Activity act;
    private T data;
    private boolean mIsCanceled = false;
    private boolean mIsIgnoreDataEmpty = false;
    private Map<String, HCIrrObject<?>> mIrrObjects = null;


    public HCOriginal<T> addIrrObject(String key, HCIrrObject<?> object){
        if(object != null){
            if(mIrrObjects == null){
                mIrrObjects = new HashMap<>();
            }
            mIrrObjects.put(key, object);
        }
        return this;
    }

    public <TYPE> TYPE getIrrObject(String key, Class<TYPE> cls){
        TYPE obj = null;
        if (mIrrObjects != null && mIrrObjects.containsKey(key)) {
            HCIrrObject<?> sObj = mIrrObjects.get(key);
            Object r = sObj.getObj(cls);
            if(r != null){
                obj = (TYPE) r;
            }
        }
        return obj;
    }

    public void responseOnIrrObjects(String irrObjectsString) throws JSONException {
        if(mIrrObjects != null && mIrrObjects.size() > 0){
            Set<String> keySets = mIrrObjects.keySet();
            JSONObject jsonOb = new JSONObject(irrObjectsString);
            for (String key : keySets) {
                HCIrrObject object = mIrrObjects.get(key);
                if(object != null){
                    object.onIrrParse(jsonOb.optString(key));
                }
            }
        }
    }

    public void cancel() {
        mIsCanceled = true;
    }

    public boolean isCanceled() {
        return this.mIsCanceled;
    }

    protected void setContext(Activity act) {
        this.act = act;
    }

    public HCOriginal(Activity act, Class<T> cls) {
        this(act, cls, false);
    }

    public HCOriginal(Class<T> cls) {
        this(null, cls);
    }

    public HCOriginal(Class<T> cls, boolean primitive) {
        this(null, cls, primitive);
    }

    private HCOriginal(Activity act, Class<T> cls, boolean primitive) {
        this.act = act;
        this.cls = cls;
        this.isPrimitive = primitive;
        if(Looper.getMainLooper() == Looper.myLooper()) {
            this.handler = new Handler();
        }
    }

    public void responseOnDataInternal(String data) throws HEEmptyData {
        mayPost(this::onRequestSuccess);
        final T t = onParsingData(data);

        if (t == null) {
            failureInternal(new HEEmptyData());
        } else {
            mayPost(() -> {
                if (isCanceled()){
                    return;
                }
                if (act != null && !Useless.isEmpty(msg)){
                    ToastUtils.show(act, msg);
                }
                onResponse(t);
            });
        }

        this.data = t;
    }

    private void mayPost(Runnable runnable) {
        if(handler != null) {
            handler.post(runnable);
        } else {
            runnable.run();
        }
    }

    protected void onRequestSuccess(){
    }

    public T getData() {
        return this.data;
    }

    private T onParsingData(String data) throws HEEmptyData {
        if (cls == String.class) {
            return onParsingStringData(data);
        }
        else {
            if (!isIgnoreEmptyDataError() && data == null) {
                throw new HEEmptyData();
            }
            else {
                return onParsingGsonData(data);
            }
        }
    }

    public void failureInternal(final Exception e) {
        mayPost(() -> {
            if (isCanceled()) return;
            if (act != null && !Useless.isEmpty(msg)) ToastUtils.show(act, msg);
            onFailure(e);
        });
    }

    public boolean isPrimitive() {
        return isPrimitive;
    }


    private T onParsingGsonData(String data) {
        return new Gson().fromJson(Useless.emptyOr(data, ""), cls);
    }

    protected T onParsingStringData(String data) {
        return (T) data;
    }

    /**
     * @param response
     */
    public void onResponse(@NonNull T response) {
    }


    @Override
    public void onFailure(Exception e) {
        int code = -1;
        String msg = "";
        /*if (act != null && act.getResources() != null) {
            msg = act.getResources().getString(R.string.string_exception_networkhttp_simplehttprequestor_neterror);
        }*/
        if (HEResultCode.class.isInstance(e)) {
            HEResultCode ex = (HEResultCode) e;
            code = ex.getCode();
            msg = ex.getMsg();
        }
        onFailure(e, code, msg);
    }

    @SuppressWarnings("unused")
    public boolean isIgnoreEmptyDataError() {
        return isIgnoreEmptyDataError != null && isIgnoreEmptyDataError;
    }

    public void setIgnoreEmptyDataError(boolean ignoreEmptyDataError) {
        isIgnoreEmptyDataError = ignoreEmptyDataError;
    }

    public abstract void onFailure(Exception e, int code, String msg);

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isIgnoreDataEmpty() {
        return mIsIgnoreDataEmpty;
    }

    public void setIsIgnoreDataEmpty(boolean mIsIgnoreDataEmpty) {
        this.mIsIgnoreDataEmpty = mIsIgnoreDataEmpty;
    }
}
