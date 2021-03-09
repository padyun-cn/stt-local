package com.padyun.scripttools.module.runtime;

import android.app.Activity;
import android.util.Base64;

import com.padyun.scripttools.biz.ui.content.Constants;
import com.padyun.scripttools.biz.ui.data.StRespBaseObj;
import com.padyun.scripttools.common.StOkBaseObjCallback;
import com.padyun.scripttools.content.alert.StAlertFactory;
import com.padyun.scripttoolscore.models.HttpParam;
import com.uls.utilites.un.Useless;
import com.uls.utilites.utils.CSystemUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;

/**
 * Created by daiepngfei on 2020-06-23
 */
public class StSubscriptionManager {
    private StContext stContext;

    StSubscriptionManager(StContext context) {
        this.stContext = context;
    }

    private boolean isCheckingSubs;
    public void checkNewSubsWithClipboard(final Activity activity, String userId) {

        if(isCheckingSubs){
            return;
        }

        if (activity == null) {
            return;
        }

        final String currentClipCode = CSystemUtils.Clipboard.getText(activity);

        if (Useless.isEmpty(currentClipCode)) {
            return;
        }

        final String validCode = getValidCode(currentClipCode);
        if(!isValidSubsCode(validCode)){
            return;
        }

        final StSharedPreferenceManager.SpBehavior behavior = stContext.spManager().getStBehavior(activity, userId);
        if (behavior == null || behavior.equalsLastSubscriptionCode(validCode)) {
            return;
        }

        isCheckingSubs = true;
        StContext.network().http().post(Constants.ApiPath.Phrase.POST_SUBSCRIPTION, new StOkBaseObjCallback() {

            @Override
            public void onBaseResponse(StRespBaseObj t) {
                behavior.setLastSubscriptionCode(validCode);
                StAlertFactory.showStDefaultTips(activity, t.getMsg());
                isCheckingSubs = false;
            }

            @Override
            public void onNetworkError(@NotNull Call call, @NotNull IOException e) {
                StAlertFactory.showStDefaultTips(activity, Constants.Tips.NET_ERROR);
                isCheckingSubs = false;
            }

            @Override
            public void onStResponseFailure(int code, String msg, Exception e) {
                behavior.setLastSubscriptionCode(validCode);
                /*if(code == 40004){ // 已经订阅了
                }*/
                if(msg != null) {
                    StAlertFactory.showStDefaultTips(activity, msg, Constants.Tips.NET_ERROR);
                }
                isCheckingSubs = false;
            }
        }, new HttpParam("code", validCode));
    }

    private static final String PARTTERN = "￥"; //这里不要自己打，编码不一样 ¥￥可以对比下。

    private boolean isValidSubsCode(String step0){
        String[] sarray = null;
        try {
            if(step0 != null) {
                final String step1 = new String(Base64.decode(step0.replaceAll(PARTTERN, ""), Base64.DEFAULT));
                final String step2 = step1.substring(0, step1.length() - 3);
                final String step3 = new StringBuilder(step2).reverse().toString();
                final String step4 = new String(Base64.decode(step3, Base64.DEFAULT));
                sarray = step4.split("#");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return sarray != null && sarray.length == 5;
    }


    private String getValidCode(String clipData) {
        final int fIndex = clipData.indexOf(PARTTERN);
        final int lIndex = clipData.lastIndexOf(PARTTERN);
        if(fIndex < 0 || lIndex < 0 || fIndex >= lIndex){
            return null;
        }

        try {
            return clipData.trim().substring(fIndex, lIndex + 1);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
