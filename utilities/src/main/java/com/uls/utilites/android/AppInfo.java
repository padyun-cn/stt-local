package com.uls.utilites.android;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.util.Log;

import java.util.Locale;

/**
 * Created by daiepngfei on 3/9/18
 */

public class AppInfo {
    private static final String TAG = "AppSelf#";

    private String language;
    private String version_name;
    private int version_code;


    private static class F {
        @SuppressLint("StaticFieldLeak")
        private static final AppInfo sInstance = new AppInfo();
    }

    public static AppInfo ins(){
        return F.sInstance;
    }

    private AppInfo() {
        // do nothing
    }


    public static void initialize(Context context) {
        ins().init(context);
    }

    private void init(Context context) {
        initVersion(context);
        getLang(context);
    }

    public static String versionName() {
        return ins().version_name;
    }

    public static String language() {
        return ins().language;
    }


    public static int versionCode() {
        return ins().version_code;
    }

    private void initVersion(Context ctx) {
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            this.version_name = packageInfo.versionName;
            this.version_code = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }



    private void getLang(Context context) {
        try {
            Locale locale;
            final Configuration configuration = context.getResources().getConfiguration();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                locale = configuration.getLocales().get(0);
            } else locale = configuration.locale;

            language = locale.getLanguage() + "-" + locale.getCountry();
        } catch (Exception e) {
            Log.e(TAG, "getLang faile", e);
        }
    }
}
