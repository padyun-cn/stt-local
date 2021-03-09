package com.padyun.scripttools.compat.data;

import androidx.annotation.NonNull;

import com.padyun.scripttools.R;
import com.padyun.scripttoolscore.compatible.data.model.SEImage;
import com.padyun.scripttoolscore.compatible.data.model.actions.SEActionFinish;

/**
 * Created by daiepngfei on 2019-12-03
 */
public class CoFinish extends AbsCoImage<SEActionFinish> {

    public CoFinish(@NonNull SEImage image) {
        super(image);
    }

    @NonNull
    @Override
    protected SEActionFinish genActionWithSEImage(@NonNull SEImage image) {
        return new SEActionFinish();
    }

    @Override
    protected void onSettingCoTimeout(int co_timeout) {
        // getSEAction().setDelay(co_timeout);
    }

    @Override
    public String getCo_type() {
        return "finish";
    }

    @Override
    public int getTypeItemLayoutId() {
        return R.layout.item_ui_condition_finish;
    }
}
