package com.padyun.scripttools.biz.ui.holders.imports;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mon.ui.list.compat.adapter.BaseV2RecyclerAdapter;
import com.padyun.scripttools.R;
import com.padyun.scripttools.biz.ui.data.CoImportSlide;
import com.padyun.scripttools.biz.ui.holders.CoHolderTools;
import com.padyun.scripttools.compat.data.CoSlide;


/**
 * Created by daiepngfei on 8/19/19
 */
public class CoHolderImportSlide extends AbsCoHolderImport<CoImportSlide> {
    private ImageView mSymbol, mStart, mEnd;
    private View mLayoutDetail;
    private TextView mEditTimeout;
    private ImageView mBrainCounterImageView, mImgWaning;
    private TextView mBrainCounterTextView;

    public CoHolderImportSlide(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void init(View root) {
        mSymbol = root.findViewById(R.id.img_symbol);
        mStart = root.findViewById(R.id.img_start);
        mEnd = root.findViewById(R.id.img_end);
        mLayoutDetail = root.findViewById(R.id.layout_detail);
        mEditTimeout = root.findViewById(R.id.edittext_timeout);
        mBrainCounterImageView = root.findViewById(R.id.circle);
        mBrainCounterTextView = root.findViewById(R.id.circleText);
        mImgWaning = root.findViewById(R.id.imgWarning);
    }

    @Override
    protected void onSet(@NonNull Activity act, @NonNull BaseV2RecyclerAdapter adapter, @NonNull CoImportSlide item, int position) {
        CoSlide data = item.getCondition();
        if(data != null) {
            CoHolderTools.handleItemDisableState(itemView, mImgWaning, data);
            loadIdentifyImage(item, data.getIdentifyImage().getImageCropPath(), mSymbol);
            CoHolderTools.handleTimeoutEditLayout(mEditTimeout, data);
            CoHolderTools.handleCounterEditLayout(mBrainCounterTextView, mBrainCounterImageView, data);
            loadCroppedImageFromOrigin(item, data.getIdentifyImage().getImageOriginal(), data.getMainSEAction().getStartBounds(), mStart);
            loadCroppedImageFromOrigin(item, data.getIdentifyImage().getImageOriginal(), data.getMainSEAction().getEndBounds(), mEnd);
        }
    }

}
