package com.uls.utilites.content;

import android.content.Intent;

/**
 * Created by daiepngfei on 12/25/17
 */

public interface ILifeAttachable {
    boolean attachable();
    void onCreate();
    void onStart();
    void onResume();
    void onPause();
    void onStop();
    void onDestroy();
    void onIntent(Intent intent);
}
