package com.uls.stlocalservice.ui.floating;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.WindowManager;

import com.mon.ui.floating.Overlay;
import com.mon.ui.floating.OverlayParamFactory;
import com.uls.stlocalservice.R;

import androidx.core.util.Consumer;

/**
 * Created by daiepngfei on 2021-01-18
 */
public class OverlayScriptList extends Overlay {
    private OverlayScriptList_View mScriptList;
    private View.OnClickListener onCloseListener;
    private static class F {
        @SuppressLint("StaticFieldLeak")
        private static final OverlayScriptList sInstance = new OverlayScriptList();
    }

    public static OverlayScriptList getInstance() {
        return F.sInstance;
    }

    private OverlayScriptList() {
        // do nothing
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        WindowManager.LayoutParams params = OverlayParamFactory.matchParent();
        params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING;
        setContentView(R.layout.view_script_list, params);
        findViewById(R.id.newButton).setOnClickListener(v -> createNewScript());
        findViewById(R.id.closeButton).setOnClickListener(v -> {
            finish();
            if(onCloseListener != null){
                onCloseListener.onClick(v);
            }
        });
        mScriptList = (OverlayScriptList_View) findViewById(R.id.scriptList);
        mScriptList.setOverlay(this);
        mScriptList.refresh();
    }

    public void setOnCloseClickLisener(View.OnClickListener onClickListener){
        this.onCloseListener = onClickListener;
    }


    public void createNewScript(){
        if(mScriptList != null){
            mScriptList.createNewScriptWithNewName();
        }
    }

    public void setRunningScriptNameChangeListener(Consumer<String> nameChangeListener){
        if(mScriptList != null) {
            mScriptList.setRunningScriptNameChangeListener(nameChangeListener);
        }
    }

    public void reloadLocal() {
        if (mScriptList != null /*&& !mScriptList.isFirstResumed()*/) {
            mScriptList.refresh();
        }
    }
    
}
