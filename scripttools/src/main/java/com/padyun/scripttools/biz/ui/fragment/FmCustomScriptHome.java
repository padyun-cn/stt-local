package com.padyun.scripttools.biz.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mon.ui.buildup.CvCoreDrawableText;
import com.mon.ui.list.compat.fragment.FmBase;
import com.mon.ui.list.compat.fragment.IFragmentGenerator;
import com.padyun.scripttools.R;
import com.padyun.scripttools.module.runtime.StBroadcastReceiver;
import com.padyun.scripttools.module.runtime.StContext;
import com.padyun.scripttools.module.runtime.StIntentActions;
import com.uls.utilites.un.Useless;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;

/**
 * Created by daiepngfei on 2020-05-14
 */
public class FmCustomScriptHome extends FmBase implements IFragmentGenerator {

    public static final String TAG = FmCustomScriptHome.class.getSimpleName();
    private CustomScriptHomeProxy mCustomScriptHomeProxy = new CustomScriptHomeProxy();

    private boolean hasNewTitleTaskUpdated = false;
    private boolean hasNewFavoredTaskUpdated = false;

    private View mRootView;
    private CvCoreDrawableText mButtonLocalTask, mButtonFavoredTask;
    private Consumer<Integer> mOnCurFragmentIndexChangedListener;
    private FmCustomScriptTitleList mFmTitleList;
    private FmCustomScriptFavoredList mFmTitleListFavor;
    private final int[] mCheckedColorList = {
            Color.parseColor("#3195FE"),
            Color.parseColor("#AFAFAE")
    };


    public FmCustomScriptHome() {
        this(true);
    }

    public FmCustomScriptHome(boolean isEditable){
        this.isEditable = isEditable;
        mFmTitleList = new FmCustomScriptTitleList(isEditable);
        mFmTitleListFavor = new FmCustomScriptFavoredList(isEditable);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fm_custom_script_home, container, false);
    }

    private void onSwitchFragmentList(boolean local){
        mButtonLocalTask.setTextColor(local ? mCheckedColorList[0] : mCheckedColorList[1]);
        mButtonLocalTask.setImageResouces(local ? R.drawable.ic_script_home_bottom_bar_local_checked : R.drawable.ic_script_home_bottom_bar_local_default);
        mButtonFavoredTask.setTextColor(!local ? mCheckedColorList[0] : mCheckedColorList[1]);
        mButtonFavoredTask.setImageResouces(!local ? R.drawable.ic_script_home_bottom_bar_remote_checked : R.drawable.ic_script_home_bottom_bar_remote_default);
        doSwitchFragmentWithTag(local ? FmCustomScriptTitleList.TAG : FmCustomScriptFavoredList.TAG);
        if(mOnCurFragmentIndexChangedListener != null){
            mOnCurFragmentIndexChangedListener.accept(local ? 0 : 1);
        }
    }

    public void setOnFragmentSwitchListener(Consumer<Integer> tags){
        mOnCurFragmentIndexChangedListener = tags;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mRootView == null) {
            mRootView = view;
            mRootView.setBackgroundColor(Color.WHITE);
            mButtonFavoredTask = mRootView.findViewById(R.id.buttonFavoredTask);
            mButtonLocalTask = mRootView.findViewById(R.id.buttonLocalTask);
            mButtonLocalTask.setOnClickListener(v -> {
                onSwitchFragmentList(true);
                if (hasNewFavoredTaskUpdated) {
                    mFmTitleList.reload();
                    hasNewFavoredTaskUpdated = false;
                }
            });
            mButtonFavoredTask.setOnClickListener(v -> {
                onSwitchFragmentList(false);
                if (hasNewTitleTaskUpdated) {
                    mFmTitleListFavor.reload();
                    hasNewTitleTaskUpdated = false;
                }
            });

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("nihaoma888", mRootView.getWidth() + "," + mRootView.getHeight() + ", " + mRootView.getTop() + "," + mRootView.getLeft());
                }
            }, 5000);
            onSwitchFragmentList(true);
        }
    }

    private StBroadcastReceiver mReceiver = new StBroadcastReceiver(
            StIntentActions.CLEAR_REMOTE_TASKS
    ) {
        @Override
        public void onStReceive(String action, Intent data) {
            if(StIntentActions.CLEAR_REMOTE_TASKS.equals(action) && data != null &&
                    !Useless.equals(data.getStringExtra("flag"), FmCustomScriptHome.class.getSimpleName())){
                mFmTitleList.reload();
                mFmTitleListFavor.reload();
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        // temp code
//        mRootView.findViewById(R.id.bottomBar).setVisibility(View.GONE);
        StContext.regiseterBroadcastReceiver(mReceiver);
        // temp code end
    }

    @Override
    public void onStop() {
        super.onStop();
        StContext.unregiseterBroadcastReceiver(mReceiver);
    }

    @Override
    public int getFragmentContainerId(String tag) {
        return R.id.subContainer;
    }

    @Override
    public Fragment onCreateFragmentWithTag(String tag) {
        if (FmCustomScriptTitleList.TAG.equals(tag)) {
            mFmTitleList.setCustomScriptHomeProxy(mCustomScriptHomeProxy);
            return mFmTitleList;
        } else if(FmCustomScriptFavoredList.TAG.equals(tag)){
            mFmTitleListFavor.setCustomScriptHomeProxy(mCustomScriptHomeProxy);
            return mFmTitleListFavor;
        }
        return null;
    }

    public final class CustomScriptHomeProxy {

        public void markTitleListUpdated() {
            hasNewTitleTaskUpdated = true;
        }

        public void markNewFavoredTaskUpdated() {
            hasNewFavoredTaskUpdated = true;
        }

    }

    public void createNewScript(){
        if(mFmTitleList != null){
            mFmTitleList.createNewScriptWithNewName();
        }
    }

    public void setRunningScriptNameChangeListener(Consumer<String> nameChangeListener){
        if(mFmTitleList != null) {
            mFmTitleList.setRunningScriptNameChangeListener(nameChangeListener);
        }
    }

    public void reloadLocal() {
        if (mFmTitleList != null && !mFmTitleList.isFirstResumed()) {
            mFmTitleList.reload();
        }
    }

    private boolean isEditable;

    public void setEditable(boolean editable){
        this.isEditable = editable;
    }

}
