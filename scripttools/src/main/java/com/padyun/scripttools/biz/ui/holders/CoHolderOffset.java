package com.padyun.scripttools.biz.ui.holders;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mon.ui.list.compat.adapter.BaseV2RecyclerAdapter;
import com.uls.utilites.un.Useless;
import com.padyun.core.dialogs.CoreDgV2Alert;
import com.padyun.scripttools.R;
import com.padyun.scripttools.biz.ui.content.SECons;
import com.padyun.scripttools.biz.ui.dialogs.edit.DgV3ONActionOffset;
import com.padyun.scripttools.biz.ui.dialogs.edit.DgV3ONImgEditCrop;
import com.padyun.scripttools.compat.data.CoOffset;
import com.padyun.scripttoolscore.compatible.data.model.SEImage;
import com.padyun.scripttools.content.la.ISEActivityAttatchable;

/**
 * Created by daiepngfei on 8/19/19
 */
public class CoHolderOffset extends AbsCoHolder<CoOffset> {
    private ImageView mSymbol, mOffset;
    private View mLayoutDetail;
    private TextView mEditTimeout;
    private ImageView mBrainCounterImageView;
    private TextView mBrainCounterTextView;
    private ImageView mImgWaning;

    public CoHolderOffset(@NonNull View itemView, ISEActivityAttatchable attachable) {
        super(itemView, attachable);
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
    protected void onSet(@NonNull Activity act, @NonNull BaseV2RecyclerAdapter adapter, @NonNull CoOffset data, int position) {
        CoHolderTools.handleInsertModeState(itemView, adapter, data, position);
        CoHolderTools.handleTimeoutEditLayout(mEditTimeout, data);
        CoHolderTools.handleCounterEditLayout(mBrainCounterTextView, mBrainCounterImageView, data);

        mSymbol.setOnClickListener(v -> {
            DgV3ONImgEditCrop dgCrop = new DgV3ONImgEditCrop(act);
            dgCrop.setAttatchable(getAttachable());
            dgCrop.init(act, data.getMainSEItem(), (dg, r, key, _data) -> {
                        final SEImage image = _data.getImage_detail();
                        if (Useless.contains(key, "bounds")) {
                            data.setOffset(0, 0);
                        }
                        data.setIdentifyImage(image);
                        data.setMainSEItem(_data);
                        dg.dismiss();
                        loadIdentifyImage(data, mSymbol);
                        adapter.notifyDataSetChanged();
                        sendItemMessage(SECons.Ints.SCRIPT_NEED_SAVE);
                    }
            );
            dgCrop.setOnDoneClickListener((r, key, image, path) -> {
                        if (!Useless.contains(key, "bounds")) {
                            return false;
                        }
                        CoreDgV2Alert.toCreate(act)
                                .setTitle("????????????")
                                .setMessage("????????????????????????????????????????????????????????????????????????")
                                .setExclusiveNaturalButton("?????????",
                                        (dialog, which) -> {
                                            dialog.dismiss();
                                            dgCrop.callbackWithSaving(r, key, image, path);
                                        }
                                )
                                .show();
                        return true;
                    }
            );
            dgCrop.show();
        });

        mLayoutDetail.setOnClickListener(v -> CoHolderTools.changeType(act, getAttachable(),
                adapter, data, () -> sendItemMessage(SECons.Ints.SCRIPT_NEED_SAVE)));

        loadIdentifyImage(data, mSymbol);

        if (data.getMainSEAction().isOffset()) {
            loadCroppedImageFromOrigin(data, data.getMainSEAction().getOffsetBounds(), mOffset, e -> clearOffsetAndWarning(adapter, data));
        }

        // img warning
        mImgWaning.setVisibility(View.GONE);
        CoHolderTools.handleItemDisableState(itemView, mImgWaning, data);

        if (data.getMainSEAction().isOffset()) {
            loadCroppedImageFromOrigin(data, data.getMainSEAction().getOffsetBounds(), mOffset, e -> clearOffsetAndWarning(adapter, data));
        } else {
            if (!data.isDisabled()) {
                mImgWaning.setVisibility(View.VISIBLE);
                mImgWaning.setImageResource(R.drawable.ic_script_warning);
            }
            mOffset.setImageBitmap(null);
            mImgWaning.setOnClickListener(v -> mOffset.performClick());
        }

        mOffset.setOnClickListener(v -> {
            DgV3ONActionOffset offset = new DgV3ONActionOffset(act);
            offset.setAttatchable(getAttachable());
            offset.init(act, data.getMainSEAction(), (dg, r, key, dat) -> {
                data.setOffset(dat.getShift_x(), dat.getShift_y());
                data.setMainSEAction(dat);
                adapter.notifyDataSetChanged();
                sendItemMessage(SECons.Ints.SCRIPT_NEED_SAVE);
            });
            offset.show();
        });

    }

    private void clearOffsetAndWarning(BaseV2RecyclerAdapter adapter, @NonNull CoOffset data) {
        data.setOffset(0, 0);
        adapter.notifyDataSetChanged();
    }

}
