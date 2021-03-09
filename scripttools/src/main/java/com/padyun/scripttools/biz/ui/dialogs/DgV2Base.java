package com.padyun.scripttools.biz.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.mon.ui.anim.CoreAnimHelper;
import com.padyun.scripttools.R;
import com.padyun.scripttools.content.la.ISEActivityAttatchable;
import com.padyun.scripttools.content.la.SESimpleActivityLifeAttacher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * Created by daiepngfei on 6/20/19
 */
public abstract class DgV2Base extends Dialog {

    private View mContentView;
    private boolean mFirst = true;
    private ISEActivityAttatchable mIActivityAttatchable;
    private OnDismissListener mOnDismissListener;
    public enum Result {
        CANCEL, SELECTED, ACTION, UPDATE, CREATE
    }

    public interface Callback<B> {
        void onCallback(DgV2Base dg, Result r, String key, B data);
    }

    public DgV2Base(@NonNull Context context) {
        super(context, R.style.selector_dialog);
        initialize();
    }

    public DgV2Base(@NonNull Context context, int themeResId) {
        super(context, R.style.selector_dialog);
        initialize();
    }

    public void setAttatchable(ISEActivityAttatchable attatchable) {
        if (attatchable != null) {
            mIActivityAttatchable = attatchable;
            attatchable.attach(mThisAttacher);
        }
    }

    protected void attacherStartActivityForResult(Intent intent, int reqCode){
        if(getIActivityAttatchable() != null){
            getIActivityAttatchable().attacherStartActivityForResult(intent, reqCode);
        }
    }

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        mOnDismissListener = listener;
    }

    protected ISEActivityAttatchable getIActivityAttatchable() {
        return mIActivityAttatchable;
    }
    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
    }

    private void initialize() {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        getWindow().setAttributes(lp);
        final View root = LayoutInflater.from(getContext()).inflate(R.layout.dg_script_v2_base, (ViewGroup) getWindow().getDecorView(), false);
        if(isTouchOutside()) root.setOnClickListener(view -> dismiss());
        setContentView(root);
        final FrameLayout container = root.findViewById(R.id.contentLayout);
        final View v = LayoutInflater.from(getContext()).inflate(getLayoutId(), null, false);
        if(v != null) {
            container.addView(v);
            mContentView = v;
            mContentView.setVisibility(View.INVISIBLE);
        }
        super.setOnDismissListener(dialog -> {
            dettach();
            if (mOnDismissListener != null) mOnDismissListener.onDismiss(dialog);
        });

        onInitialize();
    }

    protected void onInitialize() {}

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus && mFirst) {
            mFirst = false;
            CoreAnimHelper.techOn(Techniques.SlideInUp, mContentView, () -> findViewById(R.id.contentMask).setClickable(false));
        }
    }

    @Override
    public void dismiss() {
        findViewById(R.id.contentMask).setClickable(true);
        CoreAnimHelper.techOn(Techniques.SlideOutDown, mContentView, super::dismiss);
    }

    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    protected abstract int getLayoutId();

    public boolean isTouchOutside() {
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    private void dettach() {
        if (mIActivityAttatchable != null) {
            mIActivityAttatchable.dettach(mThisAttacher);
        }
        mIActivityAttatchable = null;
    }
    private final SESimpleActivityLifeAttacher mThisAttacher = new SESimpleActivityLifeAttacher() {
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            DgV2Base.this.onActivityResult(requestCode, resultCode, data);
        }

    };
}
