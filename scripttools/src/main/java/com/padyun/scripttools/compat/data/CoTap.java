package com.padyun.scripttools.compat.data;

import android.graphics.Rect;
import androidx.annotation.NonNull;

import com.padyun.scripttools.R;
import com.padyun.scripttoolscore.compatible.data.model.SEImage;
import com.padyun.scripttoolscore.compatible.data.model.actions.SEActionTap;
import com.padyun.scripttoolscore.compatible.data.model.coord.SECoordFixed;
import com.padyun.scripttoolscore.compatible.data.model.range.SERangeSize;


/**
 * Created by daiepngfei on 8/16/19
 */
public class CoTap extends AbsCoImage<SEActionTap> {

    private Rect co_tap;

    public CoTap(@NonNull SEImage image, @NonNull Rect r) {
        super(image);
        setTapRect(r);
    }

    public CoTap(@NonNull SEImage image) {
        super(image);
    }

    @NonNull
    @Override
    protected SEActionTap genActionWithSEImage(@NonNull SEImage image) {
        SEActionTap tap = new SEActionTap();
        tap.setOriginalPath(image.getImageOriginal());
        setTapRect(tap, image.getBounds());
        return tap;
    }

    public void setTapRect(@NonNull Rect rect){
        setTapRect(getMainSEAction(), rect);
    }

    private void setTapRect(@NonNull SEActionTap tap, @NonNull Rect rect){
        this.co_tap = rect;
        tap.setRange(new SERangeSize(rect.width(), rect.height()));
        tap.setCoord(new SECoordFixed(rect.left, rect.top));
    }

    public Rect getTapRect(){
        return co_tap;
    }


    @Override
    protected void onSettingCoTimeout(int co_timeout) {
        getMainSEAction().setDelay(co_timeout);
    }

    @Override
    public String getCo_type() {
        return "tap";
    }

    @Override
    public int getTypeItemLayoutId() {
        return  R.layout.item_ui_condition_position_click;
    }
}
