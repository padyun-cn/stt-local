package com.uls.stlocalservice.data.vhmodels;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import com.mon.ui.list.compat.adapter.IBaseRecyclerModel;
import com.uls.stlocalservice.R;

import java.io.File;

/**
 * Created by daiepngfei on 2020-11-11
 */
public class MdGame implements IBaseRecyclerModel {

    private Drawable drawable;
    private String name;
    private File apkFile;
    private String packageName;
    private String versionCode;

    public static MdGame fromPackageInfo(PackageManager pm, ResolveInfo pi){
        if(pi != null && pm != null){
            final String name = pi.activityInfo.applicationInfo.loadLabel(pm).toString();
            final String pkgName = pi.activityInfo.applicationInfo.packageName;
            MdGame model = new MdGame();
            model.setDrawable(pi.activityInfo.applicationInfo.loadIcon(pm));
            model.setName(name);
            try {
                model.setVersionCode(pm.getPackageInfo(pkgName, PackageManager.GET_CONFIGURATIONS).versionCode + "");
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
            model.setApkFile(new File(pi.activityInfo.applicationInfo.publicSourceDir));
            model.setPackageName(pkgName);
            return model;
        }
        return null;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    private transient boolean isLocalInstalled;

    public boolean isLocalInstalled() {
        return isLocalInstalled;
    }

    public void setLocalInstalled(boolean localInstalled) {
        isLocalInstalled = localInstalled;
    }

    private transient boolean isMarkedRemoved;

    public void setApkFile(File apkFile) {
        this.apkFile = apkFile;
    }

    public File getApkFile() {
        return apkFile;
    }


    public boolean isMarkedRemoved() {
        return isMarkedRemoved;
    }

    public void setMarkedRemoved(boolean markedRemoved) {
        isMarkedRemoved = markedRemoved;
    }




    public MdGame() {
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getTypeItemLayoutId() {
        return R.layout.item_game_list;
    }

}
