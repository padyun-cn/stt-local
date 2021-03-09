package com.padyun.scripttools.biz.ui.dialogs;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.mon.ui.buildup.CoreCvSimpleWheelSelector;
import com.padyun.scripttools.R;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * Created by daiepngfei on 10/30/17
 */

public class DgV2SimpleWheel extends DgV2Base implements CoreCvSimpleWheelSelector.OnItemSelectedListener, View.OnClickListener {

    private String mSlelectedText = "";
    private int mSlelectedPosition = 0;
    private boolean mIsDismissing;
    private CoreCvSimpleWheelSelector.OnItemSelectedListener mOnItemSelectedListener;
    private int mSettledPositon;

    public DgV2SimpleWheel(@NonNull Context context) {
        super(context);
    }

    public DgV2SimpleWheel init(List<String> data, CoreCvSimpleWheelSelector.OnItemSelectedListener l) {
        return init(data, 0, l);
    }

    public DgV2SimpleWheel init(List<String> data, int selection, CoreCvSimpleWheelSelector.OnItemSelectedListener l) {
        mSettledPositon = selection;
        ((CoreCvSimpleWheelSelector) findViewById(R.id.selector)).init(data, this);
        ((CoreCvSimpleWheelSelector) findViewById(R.id.selector)).setOnItemSelectedListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);
        findViewById(R.id.confirm).setOnClickListener(this);
        mOnItemSelectedListener = l;
        return this;
    }

    @Override
    public void onItemSelected(String text, int position) {
        mSlelectedPosition = position;
        mSlelectedText = text;
    }

    @Override
    public boolean isTouchOutside() {
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
//        animPushBottomIn();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus && mSettledPositon > 0){
            ((CoreCvSimpleWheelSelector) findViewById(R.id.selector)).setSelection(mSettledPositon, false);
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dg_v2_wheel;
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if(id == R.id.cancel){
            dismiss();
        } else if(id == R.id.confirm){
            if (mOnItemSelectedListener != null) {
                mOnItemSelectedListener.onItemSelected(mSlelectedText, mSlelectedPosition);
            }
            dismiss();
        }
    }

   /* private void animPushBottomOut() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.push_bottom_out);
        animation.setAnimationListener(mDismissAnimaitionListener);
        findViewById(R.id.root).startAnimation(animation);
    }*/

    /*private void animPushBottomIn() {
        new Handler().post(() -> {
            findViewById(R.id.root).setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.push_bottom_in);
            findViewById(R.id.root).startAnimation(animation);
        });
    }*/

   /* private Animation.AnimationListener mDismissAnimaitionListener = new SimpleAnimListener() {
        @Override
        public void onAnimationEnd(Animation animation) {
            SimpleWheelDialog.super.dismiss();
            mIsDismissing = false;
        }
    };*/

    private void setWindowStatusBarColor(int colorResId) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getContext().getResources().getColor(colorResId));

                //底部导航栏
                //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
