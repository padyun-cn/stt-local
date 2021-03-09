package com.padyun.scripttoolscore.compatible.data.model.coord;


import com.padyun.scripttoolscore.compatible.data.model.range.SERange;

/**
 * Created by daiepngfei on 1/16/19
 */
public class SECoordImage extends SECoord {


    /**
     * type :
     * image_info :
     * coord : {}
     * range : {}
     * shift_x :
     * shift_y :
     */

    private final String type = TYPE_IMAGE;
    private String image_info;
    private SECoord coord;
    private SERange range;
    private int shift_x;
    private int shift_y;

    @Override
    protected String getSeType() {
        return "coord_image";
    }

    public String getType() {
        return type;
    }

    public String getImage_info() {
        return image_info;
    }

    public void setImage_info(String image_info) {
        this.image_info = image_info;
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

    public int getShift_x() {
        return shift_x;
    }

    public void setShift_x(int shift_x) {
        this.shift_x = shift_x;
    }

    public int getShift_y() {
        return shift_y;
    }

    public void setShift_y(int shift_y) {
        this.shift_y = shift_y;
    }

    @Override
    public SECoord duplicate() throws CloneNotSupportedException {
        return (SECoord) clone();
    }
}
