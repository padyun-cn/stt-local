package com.padyun.scripttools.biz.ui.holders.imports;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mon.ui.list.compat.adapter.BaseV2RecyclerAdapter;
import com.padyun.scripttools.R;
import com.padyun.scripttools.biz.ui.data.CoImportFinish;
import com.padyun.scripttools.biz.ui.holders.CoHolderTools;
import com.padyun.scripttools.common.utils.CoBitmapWorker;
import com.padyun.scripttools.compat.data.CoFinish;


/**
 * Created by daiepngfei on 8/19/19
 */
public class CoHolderImportFinish extends AbsCoHolderImport<CoImportFinish> {

    private ImageView mSymbol;
    private View mLayoutDetail;
    private TextView mEditTimeout;
    private ImageView mBrainCounterImageView;
    private ImageView mImgWaning;


    public CoHolderImportFinish(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void init(View root) {
        mSymbol = root.findViewById(R.id.img_symbol);
        mLayoutDetail = root.findViewById(R.id.layout_detail);
        mEditTimeout = root.findViewById(R.id.edittext_timeout);
        mBrainCounterImageView = root.findViewById(R.id.circle);
        mImgWaning = root.findViewById(R.id.imgWarning);
    }

    @Override
    protected void onSet(@NonNull Activity act, @NonNull BaseV2RecyclerAdapter adapter, @NonNull CoImportFinish item, int position) {
        CoFinish data = item.getCondition();
        if(data != null){
            CoHolderTools.handleItemDisableState(itemView, mImgWaning, data);
            CoBitmapWorker.lruLoad(data.getIdentifyImage().getImageCropPath(), t -> setImageWhenCurItem(mSymbol, t, item));
        }
    }

}
