package com.padyun.scripttoolscore.compatible.data.model.actions;

import android.graphics.Rect;



import com.padyun.scripttoolscore.compatible.data.model.coord.SECoord;
import com.padyun.scripttoolscore.compatible.data.model.coord.SECoordFixed;
import com.padyun.scripttoolscore.compatible.data.model.range.SERange;
import com.padyun.scripttoolscore.compatible.data.model.range.SERangeSize;

/**
 * Created by daiepngfei on 1/16/19
 */
@SuppressWarnings("FieldCanBeLocal")
public class SEActionTap extends SEAction {
    private final String type = TYPE_TAP;
    private long delay = 1000;
    private SECoord coord;
    private SERange range;
    private String originalPath;

    @Override
    protected String getSeType() {
        return "action_tap";
    }
    public String getType(){
        return type;
    }
    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public SECoord getCoord() {
        return coord;
    }

    public void setCoord(SECoord coord) {
        this.coord = coord;
    }

    public SERange getRange() {
        return range;
    }

    public void setRange(SERange range) {
        this.range = range;
    }

    public SEActionTap seClone(){
        SEActionTap image = null;
        try {
            image = (SEActionTap) clone();
            if(getRange() != null) image.setRange(getRange().duplicate());
            if(getCoord() != null) image.setCoord(getCoord().duplicate());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    public Object duplicate() throws CloneNotSupportedException {
        SEActionTap image = (SEActionTap) clone();
        image.newAid();
        if(getRange() != null) image.setRange(getRange().duplicate());
        if(getCoord() != null) image.setCoord(getCoord().duplicate());
        return image;
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    public Rect getBounds() {
        Rect r = null;
        if (SERangeSize.class.isInstance(range) && SECoordFixed.class.isInstance(coord)) {
            SERangeSize size = (SERangeSize) range;
            SECoordFixed fixed = (SECoordFixed) coord;
            r = new Rect(fixed.getX(), fixed.getY(), fixed.getX() + size.getW(), fixed.getY() + size.getH());
        }
        return r;
    }
}
