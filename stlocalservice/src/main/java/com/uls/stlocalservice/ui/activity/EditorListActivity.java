package com.uls.stlocalservice.ui.activity;

import android.os.Bundle;
import android.view.Window;

import com.mon.ui.activities.AbsAcFmSimple;
import com.uls.stlocalservice.ui.floating.ScriptOverlays;
import com.uls.stlocalservice.ui.fragment.FmOverlayScriptEditor;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by daiepngfei on 2021-01-20
 */
public class EditorListActivity extends AbsAcFmSimple {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
    }

    @NotNull
    @Override
    protected Fragment onCreateFragment() {
        return FmOverlayScriptEditor.newInstance(getIntent().getExtras());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("xxedito: EditorListActivity.onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("xxedito: EditorListActivity.onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("xxedito: EditorListActivity.onResumed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("xxedito: EditorListActivity.onPaused");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("xxedito: EditorListActivity.onStopped");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        ScriptOverlays.Agent.onGameNormal(getApplicationContext());
    }
}
