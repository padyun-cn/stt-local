package com.padyun.scripttools.biz.ui.dialogs.upload;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mon.ui.dialog.AbsDgV2Base;
import com.padyun.scripttools.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by daiepngfei on 2020-06-03
 */
public class DgLiteProgress extends AbsDgV2Base {

    private ProgressBar mProgressBar;
    private TextView mTitle, mSubTitle, mProgressInfo;


    public DgLiteProgress(Context context) {
        super(context);
    }

    @Nullable
    @Override
    protected View onInflatingContentView(@NotNull LayoutInflater inflater) {
        final View v = inflater.inflate(R.layout.dg_upload_progressed, null);
        mProgressBar = v.findViewById(R.id.progressBar);
        mTitle = v.findViewById(R.id.progressTitle);
        mSubTitle = v.findViewById(R.id.progressSubTitle);
        mProgressInfo = v.findViewById(R.id.progressInfo);
        return v;
    }

    @SuppressLint("SetTextI18n")
    public void setProgress(float progress) {
        progress = Math.min(1, progress);
        final int now = (int) (mProgressBar.getMax() * progress);
        mProgressBar.setProgress(now);
        mProgressInfo.setText(((int) (progress * 100)) + "%");
    }

    public void setProgressTitle(String title) {
        mTitle.setText(title);
    }

    public void setProgressSubTitle(String title) {
        mSubTitle.setText(title);
    }

}
