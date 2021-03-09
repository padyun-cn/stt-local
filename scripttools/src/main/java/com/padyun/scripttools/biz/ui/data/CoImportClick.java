package com.padyun.scripttools.biz.ui.data;

import com.padyun.scripttools.R;
import com.padyun.scripttools.compat.data.CoClick;

/**
 * Created by daiepngfei on 2019-12-31
 */
public class CoImportClick extends AbsCoImportCondition<CoClick> {

    public CoImportClick(CoClick condition, boolean isChecked) {
        super(condition, isChecked);
    }

    @Override
    public int getTypeItemLayoutId() {
        return getCondition().hasNonExist() ?
                R.layout.item_script_import_detail_click_m :
                R.layout.item_script_import_detail_click;
    }
}
