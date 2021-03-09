package com.padyun.scripttools.compat.data;

import androidx.annotation.NonNull;

import com.padyun.scripttools.R;
import com.padyun.scripttoolscore.compatible.data.model.SEImage;
import com.padyun.scripttoolscore.compatible.data.model.actions.SEActionVoid;


/**
 * Created by daiepngfei on 8/19/19
 */
public class CoVoid extends AbsCoImage<SEActionVoid> {

    public CoVoid(@NonNull SEImage image) {
        super(image);
    }

    @NonNull
    @Override
    protected SEActionVoid genActionWithSEImage(@NonNull SEImage image) {
        return null;
    }


    @Override
    protected void onSettingCoTimeout(int co_timeout) {
        // do nothing
    }

    @Override
    public String getCo_type() {
        return "void";
    }

    @Override
    public int getTypeItemLayoutId() {
        return R.layout.item_ui_condition_void;
    }
}
