package com.padyun.scripttools.biz.ui.holders;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mon.ui.list.compat.adapter.BaseV2RecyclerAdapter;
import com.padyun.scripttools.R;
import com.padyun.scripttools.biz.ui.content.SECons;
import com.padyun.scripttools.biz.ui.dialogs.edit.DgV3ONActionRegionClick;
import com.padyun.scripttools.compat.data.CoTap;
import com.padyun.scripttools.content.la.ISEActivityAttatchable;


/**
 * Created by daiepngfei on 8/19/19
 */
public class CoHolderTap extends AbsCoHolder<CoTap> {
    private ImageView mSymbol, mImgTap;
    private View mLayoutDetail;
    private TextView mEditTimeout;
    private ImageView mBrainCounterImageView;
    private TextView mBrainCounterTextView;

    public CoHolderTap(@NonNull View itemView, ISEActivityAttatchable attachable) {
        super(itemView, attachable);
    }

    @Override
    public void init(View root) {
        mSymbol = root.findViewById(R.id.img_symbol);
        mLayoutDetail = root.findViewById(R.id.layout_detail);
        mImgTap = root.findViewById(R.id.img_tap);
        mEditTimeout = root.findViewById(R.id.edittext_timeout);
        mBrainCounterImageView = root.findViewById(R.id.circle);
        mBrainCounterTextView = root.findViewById(R.id.circleText);
    }

    @Override
    protected void onSet(@NonNull Activity act, @NonNull BaseV2RecyclerAdapter adapter, @NonNull CoTap data, int position) {
        loadIdentifyImage(data, mSymbol);
        mSymbol.setOnClickListener(v -> CoHolderTools.editItemImage(act, getAttachable(), data, () -> {
            loadIdentifyImage(data, mSymbol);
            sendItemMessage(SECons.Ints.SCRIPT_NEED_SAVE);
        }));
        mLayoutDetail.setOnClickListener(v -> CoHolderTools.changeType(act, getAttachable(), adapter, data, () -> sendItemMessage(SECons.Ints.SCRIPT_NEED_SAVE)));
        CoHolderTools.handleTimeoutEditLayout(mEditTimeout, data);
        CoHolderTools.handleCounterEditLayout(mBrainCounterTextView, mBrainCounterImageView, data);
        loadCroppedImageFromOrigin(data, data.getTapRect(), mImgTap);
        mImgTap.setOnClickListener(v -> {
            DgV3ONActionRegionClick dgV3ONActionRegionClick = new DgV3ONActionRegionClick(act);
            dgV3ONActionRegionClick.setAttatchable(getAttachable());
            dgV3ONActionRegionClick.init(act, data.getMainSEAction(), (dg, r, key, dat) -> {
                data.setMainSEAction(dat);
                data.setTapRect(dat.getBounds());
                dg.dismiss();
                adapter.notifyDataSetChanged();
            });
            dgV3ONActionRegionClick.show();
        });
    }


}
