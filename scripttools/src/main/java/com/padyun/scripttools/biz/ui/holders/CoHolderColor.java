package com.padyun.scripttools.biz.ui.holders;

import android.app.Activity;
import android.graphics.Color;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mon.ui.list.compat.adapter.BaseV2RecyclerAdapter;
import com.padyun.scripttools.R;
import com.padyun.scripttools.biz.ui.dialogs.edit.DgV3ONColorSet;
import com.padyun.scripttools.compat.data.CoColor;
import com.padyun.scripttoolscore.compatible.data.model.item.SEItemColor;
import com.padyun.scripttools.content.la.ISEActivityAttatchable;


/**
 * Created by daiepngfei on 8/19/19
 */
public class CoHolderColor extends AbsCoHolder<CoColor> {
    private TextView mTextColorDesc;
    private TextView mEditTimeout;
    private ImageView mBrainCounterImageView;
    private TextView mBrainCounterTextView;
    public CoHolderColor(@NonNull View itemView, ISEActivityAttatchable attachable) {
        super(itemView, attachable);
    }

    @Override
    public void init(View root) {
        mTextColorDesc = root.findViewById(R.id.text_color_v3);
        mEditTimeout = root.findViewById(R.id.edittext_timeout);
        mBrainCounterImageView = root.findViewById(R.id.circle);
        mBrainCounterTextView = root.findViewById(R.id.circleText);
    }

    @Override
    protected void onSet(@NonNull Activity act, @NonNull BaseV2RecyclerAdapter adapter, @NonNull CoColor data, int position) {
        final int color = data.getMainSEItem().getColor();
        CoHolderTools.handleCounterEditLayout(mBrainCounterTextView, mBrainCounterImageView, data);
        mTextColorDesc.setBackgroundColor(color);
        String builder =
                Integer.toHexString(Color.red(color)) +
                        Integer.toHexString(Color.green(color)) +
                        Integer.toHexString(Color.blue(color)).toUpperCase();
        mTextColorDesc.setText(builder);
        mTextColorDesc.setOnClickListener(v -> {
            DgV3ONColorSet dgV2ONColorSet = new DgV3ONColorSet(act);
            dgV2ONColorSet.setAttatchable(getAttachable());
            dgV2ONColorSet.init(act, data.getMainSEItem(), (dg, r, key, resultData) -> {
                if(SEItemColor.class.isInstance(resultData)) {
                    data.setMainSEItem((SEItemColor) resultData);
                    adapter.notifyDataSetChanged();
                }
                dg.dismiss();
            });
            dgV2ONColorSet.show();
        });
        CoHolderTools.handleTimeoutEditLayout(mEditTimeout, data);
    }


}
