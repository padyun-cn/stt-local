package com.padyun.scripttools.biz.ui.holders.imports;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mon.ui.list.compat.adapter.BaseV2RecyclerAdapter;
import com.padyun.scripttools.R;
import com.padyun.scripttools.biz.ui.data.CoImportOffset;
import com.padyun.scripttools.biz.ui.holders.CoHolderTools;
import com.padyun.scripttools.compat.data.CoOffset;

/**
 * Created by daiepngfei on 8/19/19
 */
public class CoHolderImportOffset extends AbsCoHolderImport<CoImportOffset> {
    private ImageView mSymbol, mOffset;
    private View mLayoutDetail;
    private TextView mEditTimeout;
    private ImageView mBrainCounterImageView;
    private TextView mBrainCounterTextView;
    private ImageView mImgWaning;

    public CoHolderImportOffset(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void init(View root) {
        mSymbol = root.findViewById(R.id.img_symbol);
        mLayoutDetail = root.findViewById(R.id.layout_detail);
        mOffset = root.findViewById(R.id.img_offset);
        mEditTimeout = root.findViewById(R.id.edittext_timeout);
        mBrainCounterImageView = root.findViewById(R.id.circle);
        mBrainCounterTextView = root.findViewById(R.id.circleText);
        mImgWaning = root.findViewById(R.id.imgWarning);
    }

    @Override
    protected void onSet(@NonNull Activity act, @NonNull BaseV2RecyclerAdapter adapter, @NonNull CoImportOffset item, int position) {
        CoOffset data = item.getCondition();
        if(data != null){

            CoHolderTools.handleTimeoutEditLayout(mEditTimeout, data);
            CoHolderTools.handleCounterEditLayout(mBrainCounterTextView, mBrainCounterImageView, data);

            loadIdentifyImage(item, data.getIdentifyImage().getImageCropPath(), mSymbol);

            if (data.getMainSEAction().isOffset()) {
                loadCroppedImageFromOrigin(item, data.getIdentifyImage().getImageOriginal(), data.getMainSEAction().getOffsetBounds(), mOffset, e -> clearOffsetAndWarning(adapter, data));
            }

            // img warning
            mImgWaning.setVisibility(View.GONE);
            CoHolderTools.handleItemDisableState(itemView, mImgWaning, data);

            if (data.getMainSEAction().isOffset()) {
                loadCroppedImageFromOrigin(item, data.getIdentifyImage().getImageOriginal(), data.getMainSEAction().getOffsetBounds(), mOffset, e -> clearOffsetAndWarning(adapter, data));
            } else {
                if (!data.isDisabled()) {
                    mImgWaning.setVisibility(View.VISIBLE);
                    mImgWaning.setImageResource(R.drawable.ic_script_warning);
                }
                mOffset.setImageBitmap(null);
                // mImgWaning.setOnClickListener(v -> mOffset.performClick());
            }

        }
    }

    private void clearOffsetAndWarning(BaseV2RecyclerAdapter adapter, @NonNull CoOffset data) {
        data.setOffset(0, 0);
        adapter.notifyDataSetChanged();
    }

}
