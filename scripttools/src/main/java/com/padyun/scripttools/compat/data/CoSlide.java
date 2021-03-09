package com.padyun.scripttools.compat.data;

import android.graphics.Rect;
import androidx.annotation.NonNull;

import com.padyun.scripttools.R;
import com.padyun.scripttoolscore.compatible.data.model.SEImage;
import com.padyun.scripttoolscore.compatible.data.model.actions.SEActionSlide;
import com.padyun.scripttoolscore.compatible.data.model.coord.SECoordFixed;
import com.padyun.scripttoolscore.compatible.data.model.range.SERangeSize;


/**
 * Created by daiepngfei on 8/19/19
 */
public class CoSlide extends AbsCoImage<SEActionSlide> {

    private Rect co_start;
    private Rect co_end;

    public CoSlide(@NonNull SEImage image) {
        super(image);
    }

    public CoSlide(@NonNull SEImage image, Rect start, Rect end) {
        super(image);
        setSlide(start, end);
    }

    @NonNull
    @Override
    protected SEActionSlide genActionWithSEImage(@NonNull SEImage image) {
        SEActionSlide seActionSlide = new SEActionSlide();
        /*
        Rect r = image.getBounds();
        co_end = co_start = r;
        SECoordFixed fixed = new SECoordFixed(r.left, r.top);
        seActionSlide.setStart_coord(fixed);
        seActionSlide.setEnd_coord(fixed);
        SERangeSize rangeSize = new SERangeSize(r.width(), r.height());
        seActionSlide.setStart_range(rangeSize);
        seActionSlide.setEnd_range(rangeSize);
        */
        seActionSlide.setOrignalPath(image.getImageOriginal());
        return seActionSlide;
    }

    public void setStartImage(Rect r) {
        this.co_start = r;
        if (r != null) {
            getMainSEAction().setStart_range(new SERangeSize(r.width(), r.height()));
            getMainSEAction().setStart_coord(new SECoordFixed(r.left, r.top));
        }
    }

    public void setEndImage(Rect r) {
        this.co_end = r;
        if (r != null) {
            getMainSEAction().setEnd_range(new SERangeSize(r.width(), r.height()));
            getMainSEAction().setEnd_coord(new SECoordFixed(r.left, r.top));
        }
    }

    public void setSlide(@NonNull Rect start, @NonNull Rect end) {
        setStartImage(start);
        setEndImage(end);
    }

    @Override
    public void setMainSEAction(@NonNull SEActionSlide action) {
        super.setMainSEAction(action);
        this.co_start = action.getStartBounds();
        this.co_end = action.getEndBounds();
    }

    public Rect getCo_start() {
        return co_start;
    }

    public Rect getCo_end() {
        return co_end;
    }

    @Override
    protected void onSettingCoTimeout(int co_timeout) {
        getMainSEAction().setDelay(co_timeout);
    }

    @Override
    public String getCo_type() {
        return "slide";
    }

    @Override
    public int getTypeItemLayoutId() {
        return hasNonExist() ? R.layout.item_ui_condition_slide_m : R.layout.item_ui_condition_slide;
    }
}
