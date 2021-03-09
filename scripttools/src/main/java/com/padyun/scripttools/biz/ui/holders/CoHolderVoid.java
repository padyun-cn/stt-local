package com.padyun.scripttools.biz.ui.holders;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.view.View;

import com.mon.ui.list.compat.adapter.BaseV2RecyclerAdapter;
import com.mon.ui.list.compat.adapter.IBaseRecyclerModel;
import com.padyun.scripttools.content.la.ISEActivityAttatchable;

/**
 * Created by daiepngfei on 8/19/19
 */
public class CoHolderVoid extends AbsCoHolder<IBaseRecyclerModel> {

    public CoHolderVoid(@NonNull View itemView, ISEActivityAttatchable attachable) {
        super(itemView, attachable);
    }

    @Override
    public void init(View root) {

    }

    @Override
    protected void onSet(@NonNull Activity act, @NonNull BaseV2RecyclerAdapter adapter, @NonNull IBaseRecyclerModel item, int position) {

    }

}
