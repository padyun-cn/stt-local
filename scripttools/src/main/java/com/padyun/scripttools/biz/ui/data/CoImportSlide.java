package com.padyun.scripttools.biz.ui.data;

import com.padyun.scripttools.R;
import com.padyun.scripttools.compat.data.CoSlide;

/**
 * Created by daiepngfei on 2019-12-31
 */
public class CoImportSlide extends AbsCoImportCondition<CoSlide> {

    public CoImportSlide(CoSlide condition, boolean isChecked) {
        super(condition, isChecked);
    }

    @Override
    public int getTypeItemLayoutId() {
        return getCondition().hasNonExist() ? R.layout.item_script_import_detail_slide_m
                : R.layout.item_script_import_detail_slide;
    }
}
