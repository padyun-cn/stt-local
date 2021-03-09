package com.padyun.scripttools.biz.ui.data;

import com.padyun.scripttools.R;
import com.padyun.scripttools.compat.data.CoFinish;

/**
 * Created by daiepngfei on 2019-12-31
 */
public class CoImportFinish extends AbsCoImportCondition<CoFinish> {

    public CoImportFinish(CoFinish condition, boolean isChecked) {
        super(condition, isChecked);
    }

    @Override
    public int getTypeItemLayoutId() {
        return R.layout.item_script_import_detail_finish;
    }
}
