package com.padyun.scripttools.biz.ui.fragment;

import com.mon.ui.list.compat2.fragment.FmSimpleList;
import com.padyun.core.dialogs.CoreDgV2Alert;
import com.padyun.scripttools.module.runtime.StContext;
import com.padyun.scripttools.module.runtime.StManifest;

import java.util.Map;
import java.util.Objects;

import okhttp3.Callback;

/**
 * Created by daiepngfei on 2020-06-05
 */
public abstract class FmStSimpleList extends FmSimpleList {

    /**
     * @param apiPath
     * @param parameters
     * @param callback
     */
    protected void httpGet(String apiPath, Map<String, String> parameters, Callback callback) {
        StContext.network().http().get(apiPath, parameters, callback);
    }

    protected StManifest manifest() {
        return StContext.getInstance().getManifest();
    }

    private CoreDgV2Alert mSimpleTipsDialog = null;

    protected void showSimpleTipsDialog(String s) {
        this.showSimpleTipsDialog(s, null);
    }

    protected void showSimpleTipsDialog(String s, Runnable runnable) {
        if (mSimpleTipsDialog == null) {
            mSimpleTipsDialog = CoreDgV2Alert.toCreate(Objects.requireNonNull(getActivity()));
            mSimpleTipsDialog.setTitle("温馨提示")
                    .setExclusiveNaturalButton("知道了", (d, x) -> d.dismiss());
        }
        if (runnable != null) {
            mSimpleTipsDialog.setTitle("温馨提示")
                    .setExclusiveNaturalButton("知道了", (d, x) -> {
                        d.dismiss();
                        runnable.run();
                    });
        } else {
            mSimpleTipsDialog.setTitle("温馨提示")
                    .setExclusiveNaturalButton("知道了", (d, x) -> d.dismiss());
        }
        mSimpleTipsDialog.setMessage(s).show();
    }

    protected void dismissSimpleTipsDialog() {
        if (mSimpleTipsDialog != null) {
            mSimpleTipsDialog.dismiss();
        }
    }

}
