package com.padyun.scripttools.compat.data;

import android.graphics.Rect;

import com.padyun.scripttools.R;
import com.padyun.scripttoolscore.compatible.data.model.SEImage;
import com.padyun.scripttoolscore.compatible.data.model.actions.SEActionImage;
import com.padyun.scripttoolscore.compatible.data.model.coord.SECoordFixed;
import com.padyun.scripttoolscore.compatible.data.model.range.SERangeSize;

import androidx.annotation.NonNull;


/**
 * Created by daiepngfei on 8/16/19
 */
public class CoClick extends AbsCoImage<SEActionImage> {

    public CoClick(@NonNull SEImage image) {
        super(image);
    }

    @Override
    public void setCo_range(Rect co_range) {
        super.setCo_range(co_range);
        if(co_range != null) {
            getMainSEAction().setRange(new SERangeSize(co_range.width(), co_range.height()));
            getMainSEAction().setCoord(new SECoordFixed(co_range.left, co_range.top));
        }
    }

    @Override
    public void setIdentifyImage(@NonNull SEImage img) {
        super.setIdentifyImage(img);
        getMainSEAction().setImage_info(img.getCropFileName());
        getMainSEAction().setImage_detail(img);
    }

    @NonNull
    @Override
    protected SEActionImage genActionWithSEImage(@NonNull SEImage image) {
        SEActionImage actionImage = new SEActionImage();
        actionImage.setImage_info(image.getCropFileName());
        actionImage.setImage_detail(image);
        return actionImage;
    }

    @Override
    protected void onSettingCoTimeout(int co_timeout) {
        getMainSEAction().setDelay(co_timeout);
    }

    @Override
    public String getCo_type() {
        return "click";
    }

    @Override
    public int getTypeItemLayoutId() {
        return hasNonExist() ? R.layout.item_ui_condition_click_m : R.layout.item_ui_condition_click;
    }
}
