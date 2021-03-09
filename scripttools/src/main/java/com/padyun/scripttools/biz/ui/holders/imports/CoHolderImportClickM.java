package com.padyun.scripttools.biz.ui.holders.imports;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mon.ui.list.compat.adapter.BaseV2RecyclerAdapter;
import com.padyun.scripttools.R;
import com.padyun.scripttools.biz.ui.data.CoImportClick;
import com.padyun.scripttools.biz.ui.holders.CoHolderTools;
import com.padyun.scripttools.common.utils.CoBitmapWorker;
import com.padyun.scripttools.compat.data.CoClick;


/**
 * Created by daiepngfei on 8/19/19
 */
public class CoHolderImportClickM extends AbsCoHolderImport<CoImportClick> {

    private ImageView mSymbol, mImgClick;
    private View mLayoutDetail;
    private TextView mEditTimeout;
    private TextView mBrainCounterTextView;
    private ImageView mBrainCounterImageView;
    private ImageView mImgFlag;

    public CoHolderImportClickM(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void init(View root) {
        mSymbol = root.findViewById(R.id.img_symbol);
        mLayoutDetail = root.findViewById(R.id.layout_detail);
        mEditTimeout = root.findViewById(R.id.edittext_timeout);
        mBrainCounterImageView = root.findViewById(R.id.circle);
        mBrainCounterTextView = root.findViewById(R.id.circleText);
        mImgFlag = root.findViewById(R.id.imgWarning);
        mImgClick = root.findViewById(R.id.img_click);
    }

    @Override
    protected void onSet(@NonNull Activity act, @NonNull BaseV2RecyclerAdapter adapter, @NonNull CoImportClick importData, int position) {
        CoClick data = importData.getCondition();
        if(data != null) {
            CoHolderTools.handleItemDisableState(itemView, mImgFlag, data);
            CoHolderTools.handleTimeoutEditLayout(mEditTimeout, data);
            CoHolderTools.handleCounterEditLayout(mBrainCounterTextView, mBrainCounterImageView, data);

            // load non-exist
            CoBitmapWorker.lruLoad(data.getNonExsitExtraImage().getImageCropPath(), t -> setImageWhenCurItem(mSymbol, t, importData));
            CoBitmapWorker.lruLoad(data.getIdentifyImage().getImageCropPath(), t -> setImageWhenCurItem(mImgClick, t, importData));
        }
    }

}
