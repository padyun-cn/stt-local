package com.padyun.scripttools.content.alert;

import android.app.Activity;

import com.padyun.core.dialogs.CoreDgV2Alert;

import com.uls.utilites.un.Useless;

/**
 * Created by daiepngfei on 2020-06-23
 */
public class StAlertFactory {

    public static void showStdTips(Activity activity, String title, String msg, String natural){
        CoreDgV2Alert.toCreate(activity).setTitle(title).setMessage(msg)
                .setExclusiveNaturalButton(natural, (dialog, which) -> dialog.dismiss()).show();
    }

    public static void showStDefaultTips(Activity activity, String msg) {
        showStDefaultTips(activity, msg, "");
    }

    public static void showStDefaultTips(Activity activity, String msg, String defaultMsg) {
        showStdTips(activity, "温馨提示", Useless.isEmpty(msg) ? defaultMsg : msg, "知道了");
    }
}
