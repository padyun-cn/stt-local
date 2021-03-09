package com.mon.ui.floating;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.view.WindowManager;

import androidx.annotation.Nullable;

/**
 * Created by daiepngfei on 2021-01-13
 */
public class SimpleOverlayManagerService extends Service {

    private final OverlayServiceBinder overlayServiceBinder = new OverlayServiceBinder();
    private OverlayContext overlayViewManager;
    private static Class<? extends Activity> cls;
    private static int iconRes;
    public static void setStartedClass(Class<? extends Activity> clz, int _iconRes){
        cls = clz;
        iconRes = _iconRes;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // return super.onStartCommand(intent, flags, startId);//
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        overlayViewManager = new OverlayContext(getApplicationContext());
        registerConfigChangeReceiver();
        if(cls != null) {
            Intent activityIntent = new Intent(this, cls);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplication(), 0, activityIntent, 0);
            Notification notification = new Notification.Builder(getApplication()).setAutoCancel(true).
                    setSmallIcon(iconRes).setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), iconRes))
                    .setTicker("本地AI助手").setContentTitle("AI助手").
                    setContentText("本地AI助手运行中，点击启动主页面").setWhen(System.currentTimeMillis()).setContentIntent(pendingIntent).build();
            startForeground(1, notification);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return overlayServiceBinder;
    }

    public void showLoading() {
        if(overlayViewManager != null) {
            overlayViewManager.showLoading(getApplicationContext());
        }
    }

    public void dismissLoading() {
        if(overlayViewManager != null){
            overlayViewManager.dismissLoading();
        }
    }

    public class OverlayServiceBinder extends Binder {
        public SimpleOverlayManagerService getService(){
            return SimpleOverlayManagerService.this;
        }
    }

    public final void pushOverlay(Context context, Overlay container){
        if(SystemWindowManager.getRequiredPermmision(this)) {
            overlayViewManager.pushOverlay(context, container);
        }
    }

    public final void popOverlay(){
        if(SystemWindowManager.getRequiredPermmision(this)) {
            overlayViewManager.popOverlay();
        }
    }

    public final void popAllOverlays(){
        if(SystemWindowManager.getRequiredPermmision(this)) {
            overlayViewManager.popAllOverlays();
        }
    }

    public final void addOverlay(Context context, Overlay container){
        if(SystemWindowManager.getRequiredPermmision(this)) {
             overlayViewManager.addOverlay(context, container);
        }
    }

    public final void removeView(Overlay container){
        if(SystemWindowManager.getRequiredPermmision(this)) {
            overlayViewManager.removeOverlay(container, true);
        }
    }

    public final void closeAll() {
        if(SystemWindowManager.getRequiredPermmision(this)) {
            overlayViewManager.clearAll();
        }
    }

    public final void updateViewLayout(Overlay container, WindowManager.LayoutParams layoutParams){
        if(SystemWindowManager.getRequiredPermmision(this)) {
            overlayViewManager.updateOverlayLayout(container, layoutParams);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        boolean landscape = newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        System.out.println("overlaylandscape # " + landscape);
    }

    private void registerConfigChangeReceiver(){
        IntentFilter configChangeFilter = new IntentFilter();
        configChangeFilter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        configChangeFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mConfigChangeReceiver, configChangeFilter);
    }

    private BroadcastReceiver mConfigChangeReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(Intent.ACTION_CONFIGURATION_CHANGED.equals(action)) {
                onServiceConfigureChanged();
            } else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
                // do nothing
            }
        }
    };

    protected void onServiceConfigureChanged() {
        overlayViewManager.onSreenOrientationConfigChange();
    }


}
