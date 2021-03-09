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
import com.padyun.scripttools.compat.data.CoSlide;
import com.padyun.scripttools.compat.data.logs.CoLogItem;

/**
 * Created by daiepngfei on 2019-12-11
 */
public class CoHolderLogSlide extends AbsCoHolder<CoLogItem<CoSlide>> {

    private TextView label, actionName, actionName2;
    private ImageView imgStart, imgEnd;

    public CoHolderLogSlide(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void init(View root) {
        label = root.findViewById(R.id.label);
        imgStart = root.findViewById(R.id.img);
        imgEnd = root.findViewById(R.id.img2);
        actionName = root.findViewById(R.id.actionName);
        actionName2 = root.findViewById(R.id.actionName2);
    }

    @Override
    protected void onSet(@NonNull Activity act, @NonNull BaseV2RecyclerAdapter adapter, @NonNull CoLogItem<CoSlide> item, int position) {
        label.setText(item.getLabel());
        label.setTextColor(item.getTextColor());
        actionName.setTextColor(item.getTextColor());
        actionName2.setTextColor(item.getTextColor());
        CoBitmapWorker.lruLoad(item.getItemData().getIdentifyImage().getImageOriginal(), item.getItemData().getMainSEAction().getStartBounds(), bm -> setImageWhenCurItem(imgStart, bm, item));
        CoBitmapWorker.lruLoad(item.getItemData().getIdentifyImage().getImageOriginal(), item.getItemData().getMainSEAction().getEndBounds(), bm -> setImageWhenCurItem(imgEnd, bm, item));
    }
}
