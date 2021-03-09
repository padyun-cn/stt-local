package com.padyun.scripttools.biz.ui.holders;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mon.ui.list.compat.adapter.BaseV2RecyclerAdapter;
import com.padyun.scripttools.R;
import com.padyun.scripttools.biz.ui.content.SECons;
import com.padyun.scripttools.common.utils.CoBitmapWorker;
import com.padyun.scripttools.compat.data.CoClick;
import com.padyun.scripttools.content.la.ISEActivityAttatchable;


/**
 * Created by daiepngfei on 8/19/19
 */
public class CoHolderClick extends AbsCoHolder<CoClick> {

    private ImageView mSymbol;
    private View mLayoutDetail;
    private TextView mEditTimeout;
    private TextView mBrainCounterTextView;
    private ImageView mBrainCounterImageView;
    private ImageView mImgFlag;


    public CoHolderClick(@NonNull View itemView, ISEActivityAttatchable attachable) {
        super(itemView, attachable);
    }

    @Override
    public void init(View root) {
        mSymbol = root.findViewById(R.id.img_symbol);
        mLayoutDetail = root.findViewById(R.id.layout_detail);
        mEditTimeout = root.findViewById(R.id.edittext_timeout);
        mBrainCounterImageView = root.findViewById(R.id.circle);
        mBrainCounterTextView = root.findViewById(R.id.circleText);
        mImgFlag = root.findViewById(R.id.imgWarning);
    }

    @Override
    protected void onSet(@NonNull Activity act, @NonNull BaseV2RecyclerAdapter adapter, @NonNull CoClick data, int position) {
        CoHolderTools.handleInsertModeState(itemView, adapter, data, position);
        CoHolderTools.handleItemDisableState(itemView, mImgFlag, data);
        CoHolderTools.handleTimeoutEditLayout(mEditTimeout, data);
        CoHolderTools.handleCounterEditLayout(mBrainCounterTextView, mBrainCounterImageView, data);
        // load click-image
        CoBitmapWorker.lruLoad(data.getIdentifyImage().getImageCropPath(), t -> setImageWhenCurItem(mSymbol, t, data));
        mSymbol.setOnClickListener(v -> CoHolderTools.editItemImage(act, getAttachable(), data, () -> {
            CoBitmapWorker.lruLoad(data.getIdentifyImage().getImageCropPath(), t -> setImageWhenCurItem(mSymbol, t, data));
            sendItemMessage(SECons.Ints.SCRIPT_NEED_SAVE);
        }));
        mLayoutDetail.setOnClickListener(v -> CoHolderTools.changeType(act, getAttachable(), adapter, data, () -> sendItemMessage(SECons.Ints.SCRIPT_NEED_SAVE)));
    }

}
