package com.padyun.scripttools.compat.data;

import androidx.annotation.NonNull;

import com.padyun.scripttools.R;
import com.padyun.scripttoolscore.compatible.data.model.SEImage;


/**
 * Created by daiepngfei on 8/16/19
 */
public class CoOffset extends CoClick {

    private int co_offset_x;
    private int co_offset_y;

    public CoOffset(@NonNull SEImage image) {
        this(image, 0, 0);
    }

    public CoOffset(@NonNull SEImage image, int offsetX, int offsetY) {
        super(image);
        setOffset(offsetX, offsetY);
    }

    public void setOffset(int offsetX, int offsetY) {
        this.co_offset_x = offsetX;
        this.co_offset_y = offsetY;
        getMainSEAction().setShift_x(offsetX);
        getMainSEAction().setShift_y(offsetY);
    }

    public int offsetX(){
        return getMainSEAction().getShift_x();
    }

    public int offsetY(){
        return getMainSEAction().getShift_y();
    }


    @Override
    public String getCo_type() {
        return "offset";
    }

    @Override
    public int getTypeItemLayoutId() {
        return hasNonExist() ? R.layout.item_ui_condition_offset_click_m :  R.layout.item_ui_condition_offset_click;
    }
}
