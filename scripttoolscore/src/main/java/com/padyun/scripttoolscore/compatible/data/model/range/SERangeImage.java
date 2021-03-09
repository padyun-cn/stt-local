package com.padyun.scripttoolscore.compatible.data.model.range;


/**
 * Created by daiepngfei on 1/16/19
 */
public class SERangeImage extends SERange {

    /**
     * type :
     * image_info :
     */

    private final String type = TYPE_IMAGE ;
    private String image_info;

    @Override
    protected String getSeType() {
        return "range_image";
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

    @Override
    public SERange duplicate() throws CloneNotSupportedException {
        return (SERange) clone();
    }
}
