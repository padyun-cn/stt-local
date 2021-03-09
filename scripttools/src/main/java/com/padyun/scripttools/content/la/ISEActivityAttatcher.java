package com.padyun.scripttools.content.la;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by daiepngfei on 1/22/19
 */
public interface ISEActivityAttatcher {
    void onCreate(Bundle savedInstanceState);
    void onStart();
    void onRestart();
    void onResume();
    void onPause();
    void onStop();
    void onDestroy();
    void onActivityResult(int requestCode, int resultCode, Intent data);
    boolean isRecyled();
}
