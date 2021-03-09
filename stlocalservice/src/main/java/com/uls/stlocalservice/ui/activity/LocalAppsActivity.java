package com.uls.stlocalservice.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.mon.ui.activities.AbsAcFmSimple;
import com.mon.ui.dialog.CoreProgressDialog;
import com.padyun.scripttools.biz.ui.views.CvScriptToolCommonNaviBar;
import com.uls.stlocalservice.R;
import com.uls.stlocalservice.ui.fragment.FmGameList;
import com.uls.utilites.content.AppPermissionManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by daiepngfei on 2021-01-14
 */
public class LocalAppsActivity extends AbsAcFmSimple {

    static {
        System.loadLibrary("opencv_java3");
    }

    CoreProgressDialog progressDialog;
    private AppPermissionManager mAppPermissionManager = new AppPermissionManager(this, this::doInitWithPermissionRequest, null);


    private void doInitWithPermissionRequest() {
        mAppPermissionManager.requestPerminssions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mAppPermissionManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mAppPermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doInitWithPermissionRequest();
    }


    @Override
    protected void onResume() {
        super.onResume();
//        doInitWithPermissionRequest();
    }

    /*private void initOnResume() {
        if (progressDialog == null) {
            progressDialog = new CoreProgressDialog(this);
        }
        progressDialog.setCancelable(false);
        if (progressDialog.isShowing()) {
            return;
        }
        progressDialog.show();
        CoreWorkers
                .on(() -> LtBaseServiceManager.copyOrNot(LocalAppsActivity.this))
                .then(r -> {
                    if (r) {
                        floatingPermissionCheck();
                    } else {
                        ToastUtils.show(LocalAppsActivity.this, "初始化应用失败。请稍候重试！");
                    }
                    progressDialog.dismiss();
                });
    }*/



    @Override
    protected Fragment onCreateFragment() {
        return new FmGameList();
    }

    @Override
    protected void onCreateTopView(@NonNull FrameLayout frameLayout, int resId) {
        CvScriptToolCommonNaviBar v = new CvScriptToolCommonNaviBar(this);
        v.init(this, findViewById(R.id.top_bar), getString(R.string.string_title_local_apps));
        v.setBackEnbale(false);
    }

}
