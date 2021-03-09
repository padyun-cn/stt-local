package com.padyun.scripttoolscore.compatible.data.model.coord;

/**
 * Created by daiepngfei on 1/16/19
 */
public class SECoordFixed extends SECoord {

    /**
     * type :
     * x :
     * y :
     */

    private final String type = TYPE_FIXED;

    private int x;
    private int y;

    public SECoordFixed() {

    }
    public SECoordFixed(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    protected String getSeType() {
        return "coord_fixed";
    }

    public String getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public SECoord duplicate() throws CloneNotSupportedException {
        return (SECoord) super.clone();
    }
}
