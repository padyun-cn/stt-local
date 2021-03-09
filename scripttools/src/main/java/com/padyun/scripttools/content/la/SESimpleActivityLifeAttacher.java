package com.padyun.scripttools.content.la;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by daiepngfei on 1/22/19
 */
public class SESimpleActivityLifeAttacher implements ISEActivityAttatcher {
    private boolean isRecycled = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public boolean isRecyled() {
        return isRecycled;
    }

    public void setRecycled(boolean recycled){
        this.isRecycled = recycled;
    }

}
