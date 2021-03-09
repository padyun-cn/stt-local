package com.spring.network.http.callback;

import android.app.Activity;

import androidx.annotation.NonNull;

/**
 * Created by daiepngfei on 1/12/18
 */

public abstract class HCPrimary<T> extends HCOriginal<T> {

    private boolean mIsInvalidTokenVerify;
    private HEChecker.OnNeedRelogin mOnNeedRelogin;

    public HCPrimary(@NonNull Class<T> cls) {
        this(cls, true);
    }

    public HCPrimary(@NonNull Class<T> cls, boolean invalidTokenVerify) {
        super(cls);
        this.mIsInvalidTokenVerify = invalidTokenVerify;
    }

    public HCPrimary(Activity activity, @NonNull Class<T> cls) {
        this(cls);
        setContext(activity);
    }

    public HCPrimary(@NonNull Class<T> cls, HEChecker.OnNeedRelogin relogin) {
        super(cls);
        mOnNeedRelogin = relogin;
    }

    @Override
    public final void onFailure(Exception e) {
        if(mIsInvalidTokenVerify) {
            final HEChecker.Info info = HEChecker.getInfo(e, mOnNeedRelogin);
            onFailure(e, info.code, info.msg);
        } else super.onFailure(e);

    }

    @Override
    public void onFailure(Exception e, int code, String msg) {
        // do nothing
    }
}
