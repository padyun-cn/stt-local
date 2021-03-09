package com.padyun.scripttools.biz.ui.holders.logs;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.mon.ui.list.compat.adapter.BaseV2RecyclerAdapter;
import com.padyun.scripttools.R;
import com.padyun.scripttools.biz.ui.holders.AbsCoHolder;
import com.padyun.scripttools.common.utils.CoBitmapWorker;
import com.padyun.scripttools.compat.data.logs.CoLogSpItem;

/**
 * Created by daiepngfei on 2019-12-11
 */
public class CoHolderLogSp extends AbsCoHolder<CoLogSpItem> {
    private ImageView img, label;
    public CoHolderLogSp(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void init(View root) {
        label = root.findViewById(R.id.label);
        img = root.findViewById(R.id.img);
    }

    @Override
    protected void onSet(@NonNull Activity act, @NonNull BaseV2RecyclerAdapter adapter, @NonNull CoLogSpItem item, int position) {
        CoBitmapWorker.lruLoad(item.getItemData().getIdentifyImage().getImageCropPath(), bm -> setImageWhenCurItem(img, bm, item));
        label.setImageResource(item.getTriggled() ? R.drawable.dot_script_log_state_success : R.drawable.dot_script_log_state_failed);
    }
}
