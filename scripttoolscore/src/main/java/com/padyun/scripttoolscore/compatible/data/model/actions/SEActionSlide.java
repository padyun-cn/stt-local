package com.padyun.scripttoolscore.compatible.data.model.actions;


import android.graphics.PointF;
import android.graphics.Rect;

import com.padyun.scripttoolscore.compatible.data.model.coord.SECoord;
import com.padyun.scripttoolscore.compatible.data.model.coord.SECoordFixed;
import com.padyun.scripttoolscore.compatible.data.model.range.SERange;
import com.padyun.scripttoolscore.compatible.data.model.range.SERangeSize;

/**
 * Created by daiepngfei on 1/16/19
 */
@SuppressWarnings("FieldCanBeLocal")
public class SEActionSlide extends SEAction {
    private final String type = TYPE_SLIDE;
    private long delay = 1000;
    private long duration = 1000;
    private SECoord start_coord;
    private SERange start_range;
    private SECoord end_coord;
    private SERange end_range;
    private String orignalPath;
    private float start_pointf_x, start_pointf_y;
    private float end_pointf_x, end_pointf_y;

    public PointF getStart_pointf() {
        return new PointF(start_pointf_x, start_pointf_y);
    }

    public void setStart_pointf(PointF start_pointf) {
        if(start_pointf != null) {
            this.start_pointf_x = start_pointf.x;
            this.start_pointf_y = start_pointf.y;
        }
    }

    public PointF getEnd_pointf() {
        return new PointF(end_pointf_x, end_pointf_y);
    }

    public void setEnd_pointf(PointF end_pointf) {
        if(end_pointf != null) {
            this.end_pointf_x = end_pointf.x;
            this.end_pointf_y = end_pointf.y;
        }
    }

    public Rect getStartBounds() {
        return getBoundsWithRangeAndCoord(start_coord, start_range);
    }

    public static Rect getBoundsWithRangeAndCoord(SECoord coord, SERange range) {
        Rect r = null;
        if (SERangeSize.class.isInstance(range) && SECoordFixed.class.isInstance(coord)) {
            SERangeSize size = (SERangeSize) range;
            SECoordFixed fixed = (SECoordFixed) coord;
            r = new Rect(fixed.getX(), fixed.getY(), fixed.getX() + size.getW(), fixed.getY() + size.getH());
        }
        return r;
    }


    public void setStartBounds(Rect rect) {
        setBounds(rect, true);
    }

    public void setEndBounds(Rect rect) {
        setBounds(rect, false);
    }

    private void setBounds(Rect rect, boolean start) {
        if(rect == null) {
            if(start) {
                start_coord = null;
                start_range = null;
            } else{
                end_coord = null;
                end_range = null;
            }
        } else {
            if(start) {
                start_coord = new SECoordFixed(rect.left, rect.top);
                start_range = new SERangeSize(rect.width(), rect.height());
            } else {
                end_coord = new SECoordFixed(rect.left, rect.top);
                end_range = new SERangeSize(rect.width(), rect.height());
            }
        }
    }

    public Rect getEndBounds() {
        return getBoundsWithRangeAndCoord(end_coord, end_range);
    }

    public String getOriginalPath() {
        return orignalPath;
    }

    public void setOrignalPath(String orignalPath) {
        this.orignalPath = orignalPath;
    }

    @Override
    protected String getSeType() {
        return "action_slide";
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

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public SECoord getStart_coord() {
        return start_coord;
    }

    public void setStart_coord(SECoord start_coord) {
        this.start_coord = start_coord;
    }

    public SECoord getEnd_coord() {
        return end_coord;
    }

    public void setEnd_coord(SECoord end_coord) {
        this.end_coord = end_coord;
    }

    public SERange getStart_range() {
        return start_range;
    }

    public void setStart_range(SERange start_range) {
        this.start_range = start_range;
    }

    public SERange getEnd_range() {
        return end_range;
    }

    public void setEnd_range(SERange end_range) {
        this.end_range = end_range;
    }


    public SEActionSlide seClone(){
        SEActionSlide image = null;
        try {
            image = (SEActionSlide) clone();
            if(getStart_range() != null) image.setStart_range(getStart_range().duplicate());
            if(getStart_coord() != null) image.setStart_coord(getStart_coord().duplicate());
            if(getEnd_range() != null) image.setEnd_range(getEnd_range().duplicate());
            if(getEnd_coord() != null) image.setEnd_coord(getEnd_coord().duplicate());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return image;
    }

    @Override
    public Object duplicate() throws CloneNotSupportedException {
        SEActionSlide image = (SEActionSlide) clone();
        image.newAid();
        if(getStart_range() != null) image.setStart_range(getStart_range().duplicate());
        if(getStart_coord() != null) image.setStart_coord(getStart_coord().duplicate());
        if(getEnd_range() != null) image.setEnd_range(getEnd_range().duplicate());
        if(getEnd_coord() != null) image.setEnd_coord(getEnd_coord().duplicate());
        return image;
    }
}
