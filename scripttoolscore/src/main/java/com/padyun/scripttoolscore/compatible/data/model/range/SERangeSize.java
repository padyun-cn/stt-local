package com.padyun.scripttoolscore.compatible.data.model.range;

/**
 * Created by daiepngfei on 1/16/19
 */
public class SERangeSize extends SERange {

    /**
     * type :
     * w :
     * h :
     */

    private final String type = TYPE_SIZE;
    private int w;
    private int h;

    public SERangeSize(int w, int h) {
        this.w = w;
        this.h = h;
    }

    public SERangeSize() {
    }

    @Override
    protected String getSeType() {
        return "range_size";
    }

    public String getType() {
        return type;
    }


    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    @Override
    public SERange duplicate() throws CloneNotSupportedException {
        return (SERange) clone();
    }
}
