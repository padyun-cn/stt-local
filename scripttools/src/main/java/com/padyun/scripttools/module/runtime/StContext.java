package com.padyun.scripttools.module.runtime;

import android.content.Intent;

import com.uls.utilites.content.PostOffice;
import com.uls.utilites.un.Useless;

import java.io.File;
import java.util.HashSet;

/**
 * Created by daiepngfei on 2020-05-20
 */
public class StContext {
    private AssetsManager mAssetsManager = new AssetsManager(this);
    private ScriptManager mScriptManager = new ScriptManager(this);
    private PathManager mPathManager = new PathManager(this);
    private NetworkManager mNetworkManager = new NetworkManager(this);
    private StManifest mManifest = new StManifest();
    private StConfig mConfig = new StConfig();
    private StSharedPreferenceManager mSpManager = new StSharedPreferenceManager(this);
    private StSubscriptionManager mStSubsManager = new StSubscriptionManager(this);
    private HashSet<StBroadcastReceiver> mStEvents = new HashSet<>();
    private PostOffice mPostOffice = new PostOffice();
    private boolean isDebugging = false;
    public void setDebugMode(boolean debugMode) {
        isDebugging = debugMode;
    }
    public boolean isDebugging() {
        return isDebugging;
    }

    public void sendStBroadcast(String action, Intent data){
        Useless.runOnUiThread(() -> {
            for (StBroadcastReceiver eventor: mStEvents) {
                if(eventor != null && eventor.contains(action)){
                    eventor.onStReceive(action, data);
                }
            }
        });
    }

    public static void regiseterBroadcastReceiver(StBroadcastReceiver receiver) {
        if (receiver != null) {
            getInstance().mStEvents.add(receiver);
        }
    }

    public static void unregiseterBroadcastReceiver(StBroadcastReceiver receiver) {
        if (receiver != null) {
            getInstance().mStEvents.remove(receiver);
        }
    }


    public static StContext getInstance(){
        return F.sInstance;
    }


    public StSubscriptionManager getStSubsciptionManager() {
        return mStSubsManager;
    }

    public StManifest getManifest() {
        return mManifest;
    }

    private static final class F {
        private static final StContext sInstance = new StContext();
    }

    private StContext() {
    }

    StSharedPreferenceManager spManager(){
        return mSpManager;
    }

    public static StManifest manifest() {
        return getInstance().mManifest;
    }

    public String getCurrentWorkingDir() {
        return mPathManager.getWorkingDir();
    }

    public static NetworkManager network() {
        return getInstance().mNetworkManager;
    }

    public static ScriptManager script(){
        return getInstance().mScriptManager;
    }

    public static PostOffice postOffice(){
        return getInstance().mPostOffice;
    }

    public static AssetsManager assets() {
        return getInstance().mAssetsManager;
    }

    public static StConfig config(){
        return getInstance().mConfig;
    }

    /**
     *
     * @param dataName
     * @return
     */
    public File getScriptData(String dataName) {
        return mScriptManager.getScriptDataFileByName(dataName);
    }

    /**
     *
     * @return
     */
    public File[] getAllScriptFiles() {
        return mScriptManager.getAllScriptFiles();
    }



}
