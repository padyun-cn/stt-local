package com.padyun.scripttools.content.la;

import android.content.Intent;

/**
 * Created by daiepngfei on 6/25/19
 */
public interface ISEActivityAttatchable extends ISEActivityStarter {
    void attach(ISEActivityAttatcher activityAttatchable);
    void dettach(ISEActivityAttatcher activityAttatchable);
    void attacherStartActivityForResult(Intent intent, int requestCode);
}
