package com.uls.stlocal;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.mon.ui.app.AppDelegate;
import com.mon.ui.floating.SimpleOverlayManagerService;
import com.uls.stlocalservice.ui.activity.LocalAppsActivity;

import androidx.multidex.MultiDex;

/**
 * Created by daiepngfei on 2021-01-14
 */
public class BaseApplication extends Application implements AppDelegate.OnUpgrade {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppDelegate.init(this, this);
        Log.e("AxxxBaseApplication: ", "app start");
        SimpleOverlayManagerService.setStartedClass(LocalAppsActivity.class, R.mipmap.ic_launcher_n);
    }

    @Override
    public boolean onUpgrade(int oldVerCode, int newVerCode) {
        if(newVerCode > oldVerCode){
            Bundle bundle = new Bundle();
            bundle.putString("type", "onAppUpgrade");
            bundle.putInt("ovc", oldVerCode);
            bundle.putInt("nvc", newVerCode);
            AppDelegate.postOffice().toLostAndFound(bundle);
        }
        return false;
    }
}
