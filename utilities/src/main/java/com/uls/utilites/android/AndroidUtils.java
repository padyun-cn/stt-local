package com.uls.utilites.android;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.uls.utilites.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by daiepngfei on 2021-01-14
 */
public class AndroidUtils {


    public static List<ResolveInfo> queryInstalledUserAppPackages(Context context){
        final long startTime = System.currentTimeMillis();
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> infos = context.getPackageManager().queryIntentActivities(intent, 0);
        LogUtil.d("BSPackage:: ", "get all packages(" + infos.size() + ") resolve info cast " + (System.currentTimeMillis() - startTime) + "ms");
        final List<ResolveInfo> appInfos = new ArrayList<>();
        final Set<String> packs = new HashSet<>();
        for (ResolveInfo i : infos) {
            if(i == null || i.activityInfo == null || i.activityInfo.applicationInfo == null){
                continue;
            }
            if((i.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != ApplicationInfo.FLAG_SYSTEM){
                if(!packs.contains(i.activityInfo.applicationInfo.packageName)) {
                    packs.add(i.activityInfo.applicationInfo.packageName);
                    appInfos.add(i);
                }
            }
        }
        return appInfos;
    }

    public static PackageInfo getPackageInfoFromFile(Context context, String filePath){
        try {
            PackageManager packageManager = context.getPackageManager();
            return packageManager.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }
}
