package com.padyun.scripttools.biz.ui.holders.logs;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mon.ui.list.compat.adapter.BaseV2RecyclerAdapter;
import com.padyun.scripttools.R;
import com.padyun.scripttools.biz.ui.holders.AbsCoHolder;
import com.padyun.scripttools.common.utils.CoBitmapWorker;
import com.padyun.scripttools.compat.data.CoOffset;
import com.padyun.scripttools.compat.data.logs.CoLogItem;

/**
 * Created by daiepngfei on 2019-12-11
 */
public class CoHolderLogOffset extends AbsCoHolder<CoLogItem<CoOffset>> {
    private ImageView img;
    private TextView label, actionName;
    public CoHolderLogOffset(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void init(View root) {
        img = root.findViewById(R.id.img);
        label = root.findViewById(R.id.label);
        actionName = root.findViewById(R.id.actionName);
    }

    @Override
    protected void onSet(@NonNull Activity act, @NonNull BaseV2RecyclerAdapter adapter, @NonNull CoLogItem<CoOffset> item, int position) {
        label.setText(item.getLabel());
        label.setTextColor(item.getTextColor());
        actionName.setTextColor(item.getTextColor());
        CoBitmapWorker.lruLoad(item.getItemData().getIdentifyImage().getImageCropPath(),  bm -> setImageWhenCurItem(img, bm, item));
    }
}
