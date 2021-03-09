package com.padyun.scripttools.module.runtime;

import android.content.Intent;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by daiepngfei on 2020-06-22
 */
public abstract class StBroadcastReceiver {
    public HashSet<String> actions;
    public StBroadcastReceiver(String... action) {
        this.actions = new HashSet<>();
        if(action != null) {
            this.actions.addAll(Arrays.asList(action));
        }
    }

    public boolean contains(String action) {
        return actions.contains(action);
    }

    public abstract void onStReceive(String action, Intent data);

}
