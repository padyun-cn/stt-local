package com.padyun.scripttools.biz.ui.holders;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mon.ui.list.compat.adapter.BaseV2RecyclerAdapter;
import com.padyun.scripttools.R;
import com.padyun.scripttools.biz.ui.content.SECons;
import com.padyun.scripttools.biz.ui.dialogs.edit.DgV3ONActionTouchMove;
import com.padyun.scripttools.common.utils.CoBitmapWorker;
import com.padyun.scripttools.compat.data.CoSlide;
import com.padyun.scripttools.content.la.ISEActivityAttatchable;


/**
 * Created by daiepngfei on 8/19/19
 */
public class CoHolderSlideM extends AbsCoHolder<CoSlide> {
    private ImageView mSymbol, mImgTarget, mStart, mEnd;
    private View mLayoutDetail, mExistClicker;
    private TextView mEditTimeout;
    private ImageView mBrainCounterImageView, mImgWaning;
    private TextView mBrainCounterTextView;

    public CoHolderSlideM(@NonNull View itemView, ISEActivityAttatchable attachable) {
        super(itemView, attachable);
    }

    @Override
    public void init(View root) {
        mSymbol = root.findViewById(R.id.img_symbol);
        mImgTarget = root.findViewById(R.id.img_tar);
        mStart = root.findViewById(R.id.img_start);
        mEnd = root.findViewById(R.id.img_end);
        mLayoutDetail = root.findViewById(R.id.layout_detail);
        mExistClicker = root.findViewById(R.id.text_type_clicker);
        mEditTimeout = root.findViewById(R.id.edittext_timeout);
        mBrainCounterImageView = root.findViewById(R.id.circle);
        mBrainCounterTextView = root.findViewById(R.id.circleText);
        mImgWaning = root.findViewById(R.id.imgWarning);
    }

    @Override
    protected void onSet(@NonNull Activity act, @NonNull BaseV2RecyclerAdapter adapter, @NonNull CoSlide data, int position) {
        CoHolderTools.handleInsertModeState(itemView, adapter, data, position);
        CoHolderTools.handleItemDisableState(itemView, mImgWaning, data);

        CoBitmapWorker.lruLoad(data.getNonExsitExtraImage().getImageCropPath(), t -> setImageWhenCurItem(mSymbol, t, data));
        mSymbol.setOnClickListener(v -> CoHolderTools.editItemNonExistImage(act, getAttachable(), data, () -> {
            CoBitmapWorker.lruLoad(data.getNonExsitExtraImage().getImageCropPath(), t -> setImageWhenCurItem(mSymbol, t, data));
            sendItemMessage(SECons.Ints.SCRIPT_NEED_SAVE);
        }));

        loadIdentifyImage(data, mImgTarget);
        mImgTarget.setOnClickListener(v -> CoHolderTools.editItemImage(act, getAttachable(), data, () -> {
            loadIdentifyImage(data, mImgTarget);
            sendItemMessage(SECons.Ints.SCRIPT_NEED_SAVE);
        }));

        mExistClicker.setOnClickListener(v -> CoHolderTools.changeNonExsitInnerType(act, getAttachable(), adapter, data, () -> sendItemMessage(SECons.Ints.SCRIPT_NEED_SAVE)));
        mLayoutDetail.setOnClickListener(v -> CoHolderTools.changeType(act, getAttachable(), adapter, data, () -> sendItemMessage(SECons.Ints.SCRIPT_NEED_SAVE)));
        CoHolderTools.handleTimeoutEditLayout(mEditTimeout, data);
        CoHolderTools.handleCounterEditLayout(mBrainCounterTextView, mBrainCounterImageView, data);
        View.OnClickListener l = v -> {
            final DgV3ONActionTouchMove clickDg = new DgV3ONActionTouchMove(act);
            clickDg.setAttatchable(getAttachable());
            clickDg.init(act, data.getIdentifyImage().getBounds(), data.getMainSEAction(), (dgActionImg, r, key, data1) -> {
                        data.setMainSEAction(data1);
                        adapter.notifyDataSetChanged();
                    }
            );
            clickDg.show();
        };
        mStart.setOnClickListener(l);
        mEnd.setOnClickListener(l);
        loadCroppedImageFromOrigin(data, data.getMainSEAction().getStartBounds(), mStart, r -> mStart.setImageBitmap(null));
        loadCroppedImageFromOrigin(data, data.getMainSEAction().getEndBounds(), mEnd, r -> mEnd.setImageBitmap(null));
    }

}
