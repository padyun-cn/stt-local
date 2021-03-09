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
import com.padyun.scripttools.compat.data.CoSlide;
import com.padyun.scripttools.content.la.ISEActivityAttatchable;


/**
 * Created by daiepngfei on 8/19/19
 */
public class CoHolderSlide extends AbsCoHolder<CoSlide> {
    private ImageView mSymbol, mStart, mEnd;
    private View mLayoutDetail;
    private TextView mEditTimeout;
    private ImageView mBrainCounterImageView, mImgWaning;
    private TextView mBrainCounterTextView;

    public CoHolderSlide(@NonNull View itemView, ISEActivityAttatchable attachable) {
        super(itemView, attachable);
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
    protected void onSet(@NonNull Activity act, @NonNull BaseV2RecyclerAdapter adapter, @NonNull CoSlide data, int position) {
        CoHolderTools.handleInsertModeState(itemView, adapter, data, position);
        CoHolderTools.handleItemDisableState(itemView, mImgWaning, data);
        loadIdentifyImage(data, mSymbol);
        mSymbol.setOnClickListener(v -> CoHolderTools.editItemImage(act, getAttachable(), data, () -> {
            loadIdentifyImage(data, mSymbol);
            sendItemMessage(SECons.Ints.SCRIPT_NEED_SAVE);
        }));
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
        loadCroppedImageFromOrigin(data, data.getMainSEAction().getStartBounds(), mStart);
        loadCroppedImageFromOrigin(data, data.getMainSEAction().getEndBounds(), mEnd);
    }

}
