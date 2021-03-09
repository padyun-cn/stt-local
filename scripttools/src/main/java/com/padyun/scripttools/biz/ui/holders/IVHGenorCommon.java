package com.padyun.scripttools.biz.ui.holders;

import androidx.annotation.NonNull;
import android.view.View;

import  com.mon.ui.list.compat.adapter.BaseRecyclerHolder;
import com.mon.ui.list.compat.adapter.BaseV2RecyclerAdapter;
import com.padyun.scripttools.R;

/**
 * Created by daiepngfei on 6/24/19
 */
public class IVHGenorCommon implements BaseV2RecyclerAdapter.VHComponent {
    BaseV2RecyclerAdapter.VHComponent genor;
    public IVHGenorCommon(){
        this(null);
    }
    public IVHGenorCommon(BaseV2RecyclerAdapter.VHComponent genor){
        this.genor = genor;
    }

    @Override
    public BaseRecyclerHolder generateVHByType(@NonNull View v, int type) {
        if(type == R.layout.item_dg_v2_text){
            return new VHText(v, true, false);
        }
        return genor != null ? genor.generateVHByType(v, type) : null;
    }
}
