package com.uls.stlocalservice.content;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.os.Bundle;

import com.mon.ui.app.AppDelegate;
import com.uls.utilites.android.AndroidUtils;
import com.uls.utilites.io.Files;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * Created by daiepngfei on 2021-02-22
 */
@Deprecated
public class LtBaseServiceManager {
    private static final String BASE_SERVICE = "baseservice.apk";

    /**
     * 现在不用copy apk文件了
     * @param context
     * @return
     */
    @Deprecated
    public static boolean copyOrNot(Context context) {
        File serviceFile = getServiceFile(context);
        if (!Files.exists(serviceFile)) {
            return copyApk(context, serviceFile);
        }
        final PackageInfo localPackageInfo = AndroidUtils.getPackageInfoFromFile(context, serviceFile.getAbsolutePath());
        System.out.println("pkgInfo-verCode::( " + serviceFile.getAbsolutePath() + ") " + (localPackageInfo == null ? "null" : localPackageInfo.versionCode));
        final Bundle b = AppDelegate.postOffice().checkAndGetFromLostAndFound(bbc -> "onAppUpgrade".equals(bbc.getString("type")));
                    /*final PackageInfo localPackageInfo = AndroidUtils.getPackageInfoFromFile(context, serviceFile.getAbsolutePath());
                    final PackageInfo assetPackageInfo = AndroidUtils.getPackageInfoFromFile(context, "file:///android_asset/" + BASE_SERVICE);*/
                    /*System.out.println("pkgInfo-verCode::( " + serviceFile.getAbsolutePath() + ") " + (localPackageInfo == null ? "null" : localPackageInfo.versionCode));
                    System.out.println("pkgInfo-verCode::( " + "file:///android_asset/" + BASE_SERVICE + ") " + (assetPackageInfo == null ? "null" : assetPackageInfo.versionCode));
                    if (assetPackageInfo != null && localPackageInfo != null && assetPackageInfo.versionCode > localPackageInfo.versionCode) {
                        return copyApk(serviceFile);
                    }*/
        if(b != null){
            System.out.println("pkgInfo-verCode::( " + serviceFile.getAbsolutePath() + ") " + (localPackageInfo == null ? "null" : localPackageInfo.versionCode) + " / copyed ");
            return copyApk(context, serviceFile);
        }
        return false;
    }

    @NotNull
    public static File getServiceFile(Context context) {
        return new File(context.getFilesDir(), BASE_SERVICE);
    }


    @NotNull
    private static boolean copyApk(Context context, File serviceFile) {
        AssetManager assetManager = context.getAssets();
        try {
            return Files.writeBytes(assetManager.open(BASE_SERVICE),
                    serviceFile, true, null, null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            final PackageInfo localPackageInfo = AndroidUtils.getPackageInfoFromFile(context, serviceFile.getAbsolutePath());
            System.out.println("pkgInfo-verCode222::( " + serviceFile.getAbsolutePath() + ") " + (localPackageInfo == null ? "null" : localPackageInfo.versionCode));
        }

        return false;
    }
}
