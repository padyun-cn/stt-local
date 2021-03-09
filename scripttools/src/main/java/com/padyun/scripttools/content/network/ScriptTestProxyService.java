package com.padyun.scripttools.content.network;

import android.app.Activity;

import com.padyun.scripttools.module.runtime.test.TestProxy;

import java.util.WeakHashMap;

import androidx.fragment.app.Fragment;

/**
 * Created by daiepngfei on 11/1/19
 */
@SuppressWarnings("unused")
public class ScriptTestProxyService {

    private WeakHashMap<Fragment, TestProxy> testFragClients = new WeakHashMap<>();
    private WeakHashMap<Activity, TestProxy> testActClients = new WeakHashMap<>();

    private static class F {
        private static final ScriptTestProxyService sIns = new ScriptTestProxyService();
    }

    /*public static ScriptTestingService ins(){
        return F.sIns;
    }*/

    private ScriptTestProxyService() {
    }

    public static TestProxy apply(String ip, String asip) {
        return new TestProxy(ip, asip);
    }


}
