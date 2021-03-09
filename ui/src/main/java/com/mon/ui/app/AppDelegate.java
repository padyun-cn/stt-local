package com.mon.ui.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.uls.utilites.android.AppInfo;
import com.uls.utilites.content.CNet;
import com.uls.utilites.content.PostOffice;

/**
 * Created by daiepngfei on 2021-02-01
 */
public class AppDelegate {
    private static final String SHAREDP_NAME = "_AppDelegate_#_";
    private SharedPreferences delegateSp;
    private Application application;
    private PostOffice postOffice = new PostOffice();

    private static class F {
        @SuppressLint("StaticFieldLeak")
        private static final AppDelegate sInstance = new AppDelegate();
    }

    private static AppDelegate ins() {
        return F.sInstance;
    }

    public static Application app(){
        return ins().application;
    }

    private AppDelegate() {
        // do nothing
    }

    /**
     *
     * @param application
     */
    public static void init(Application application){
        init(application, null);
    }

    /**
     *
     * @param application
     * @param onUpgrade
     */
    public static void init(Application application, OnUpgrade onUpgrade){
        ins()._init(application, onUpgrade);
    }

    /**
     *
     * @param application
     * @param onUpgrade
     */
    private void _init(Application application, OnUpgrade onUpgrade){
        this.application = application;
        this.delegateSp = application.getSharedPreferences(SHAREDP_NAME + application.getPackageName(), Context.MODE_PRIVATE);
        AppInfo.initialize(application);
        CNet.init(application);
        checkUpgrade(onUpgrade);
    }

    /**
     *
     * @param onUpgrade
     */
    private void checkUpgrade(OnUpgrade onUpgrade) {
        if(onUpgrade != null){
            new AppUpgrader().upgradeCheck(onUpgrade);
        }
    }

    /**
     *
     * @return
     */
    private boolean inited(){
        return this.application != null;
    }

    private class AppUpgrader {
        private static final String LAST_VER_CODE = "LAST_VER_CODE";
        private boolean upgradeCheck(OnUpgrade onUpgrade) {
            if(inited()) {
                int code = delegateSp.getInt(LAST_VER_CODE, 0);
                int curCode = AppInfo.versionCode();
                boolean upgrade = false;
                if (code < curCode) {
                    upgrade = onUpgrade.onUpgrade(code, curCode);
                    delegateSp.edit().putInt(LAST_VER_CODE, curCode).apply();
                }
                return upgrade;
            }
            return false;
        }
    }

    /**
     *
     */
    public interface OnUpgrade {
        boolean onUpgrade(int oldVerCode, int newVerCode);
    }

    public static PostOffice postOffice() {
        return ins().postOffice;
    }


}
