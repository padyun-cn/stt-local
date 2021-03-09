package com.padyun.scripttoolscore.compatible.data.model.item;


import android.graphics.Rect;

import com.padyun.scripttoolscore.compatible.data.model.IDGen;
import com.padyun.scripttoolscore.compatible.data.model.ISEImageContainer;
import com.padyun.scripttoolscore.compatible.data.model.SEImage;
import com.padyun.scripttoolscore.compatible.data.model.coord.SECoord;
import com.padyun.scripttoolscore.compatible.data.model.coord.SECoordFixed;
import com.padyun.scripttoolscore.compatible.data.model.range.SERange;
import com.padyun.scripttoolscore.compatible.data.model.range.SERangeSize;
import com.uls.utilites.io.Files;


/**
 * Created by daiepngfei on 1/16/19
 */
@SuppressWarnings("all")
public class SEItemImage extends SEItem implements ISEImageContainer {
    /**
     * type : image
     * state :
     * relation :
     * timeout :
     * image_info :
     * coord : {}
     * range : {}
     */
    /**
     * 图片存在
     */
    public static final int STATE_EXIST = 0x01;
    /**
     * 图片不存在
     */
    public static final int STATE_WITHOUT = 0x02;
    /**
     * 区域变化
     */
    public static final int STATE_CHANGE = 0x04;
    /**
     * 区域未变化
     */
    public static final int STATE_UNCHANGE = 0x08;
    /**
     * 区域颜色数量多于
     */
    public static final int STATE_MORE = 0x0100;
    /**
     * 区域颜色数量少于
     */
    public static final int STATE_LESS = 0x0200;

    private final String type = TYPE_IMAGE;
    private int state = 1; // 存在1 不存在2
    private int timeout;
    private String image_info;
    private SEImage image_detail;
    private SECoord coord;
    private SERange range;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id = IDGen.genTmsStrRandom("item_image");

    private void newId() {
        id = IDGen.genTmsStrRandom("item_image");
    }

    @Override
    protected String getSeType() {
        return "item_image";
    }

    public String getType() {
        return type;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
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

    public void resetCoordAndRange(){
        this.range = null;
        this.coord = null;
    }

    public SEImage getImage_detail() {
        return image_detail;
    }

    public void setImage_detail(SEImage image_detail) {
        this.image_detail = image_detail;
        setImage_info(Files.name(getImageCropPath()));
    }

    public void mergeOrSetNewImage(SEImage image){
        if(image != null){
            if(image_detail != null) image_detail.merge(image);
            else image_detail = image;
            resetCoordAndRange();
        }
        setImage_info(Files.name(getImageCropPath()));
    }

    public void mergeItemInfo(SEItemImage image){
        if(image != null){
            this.setState(image.state);
            this.setCoord(image.getCoord());
            this.setRange(image.getRange());
            this.setRelation(image.getRelation());
            mergeOrSetNewImage(image.getImage_detail());
        }
    }




    @Override
    public SEImage getSEImage() {
        return image_detail;
    }


    public Rect getCropBounds() {
        Rect r = null;
        /*if (SERangeSize.class.isInstance(range) && SECoordFixed.class.isInstance(coord)) {
            SERangeSize size = (SERangeSize) range;
            SECoordFixed fixed = (SECoordFixed) coord;
            r = new Rect(fixed.getX(), fixed.getY(), fixed.getX() + size.getW(), fixed.getY() + size.getH());
        }*/

        if(image_detail != null && image_detail.getImageInfo() != null){
            r = image_detail.getBounds();
        }
        return r;
    }

    public Rect getSearchRangeRect(){
        Rect r = null;
        if (SERangeSize.class.isInstance(range) && SECoordFixed.class.isInstance(coord)) {
            SERangeSize size = (SERangeSize) range;
            SECoordFixed fixed = (SECoordFixed) coord;
            r = new Rect(fixed.getX(), fixed.getY(), fixed.getX() + size.getW(), fixed.getY() + size.getH());
        }
        return r;
    }

    public SEItemImage seClone(boolean deeply){
        SEItemImage image = null;
        try {
            image = (SEItemImage) clone();
            if (range != null) image.setRange(range.duplicate());
            if (coord != null) image.setCoord(coord.duplicate());
            if(deeply){
                image.fromItemWitTid(this);
                image.id = this.id;
            } else {
                image.newTID();
                image.newId();
            }
            image.image_detail = this.image_detail.seClone(deeply);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
       return image;
    }

    @Override
    public SEItem duplicate() throws CloneNotSupportedException {
        return seClone(false);
    }



    public int getImageX() {
        return image_detail == null ? 0 : image_detail.getImageX();
    }

    public int getImageY() {
        return image_detail == null ? 0 : image_detail.getImageY();
    }

    public int getImageW() {
        return image_detail == null ? 0 : image_detail.getImageW();
    }

    public int getImageH() {
        return image_detail == null ? 0 : image_detail.getImageH();
    }

    public int getImageFlag() {
        return image_detail == null ? 0 : image_detail.getImageFlag();
    }

    public int getImageThreshold() {
        return image_detail == null ? 0 : image_detail.getImageThreshold();
    }

    public int getImageMaxval() {
        return image_detail == null ? 0 : image_detail.getImageMaxval();
    }

    public int getImageType() {
        return image_detail == null ? 0 : image_detail.getImageType();
    }

    public int getImageSim() {
        return image_detail == null ? 0 : image_detail.getImageSim();
    }


    public void setImageX(int x) {
        if(image_detail != null) image_detail.setImageX(x);
    }

    public void setImageY(int y) {
        if(image_detail != null) image_detail.setImageY(y);
    }

    public void setImageW(int w) {
        if(image_detail != null) image_detail.setImageW(w);
    }

    public void setImageH(int h) {
        if(image_detail != null) image_detail.setImageH(h);
    }

    public void setImageFlag(int flag) {
        if(image_detail != null) image_detail.setImageFlag(flag);
    }

    public void setImageThreshold(int threshold) {
        if(image_detail != null) image_detail.setImageThreshold(threshold);
    }

    public void setImageMaxval(int maxval) {
        if(image_detail != null) image_detail.setImageMaxval(maxval);
    }

    public void setImageType(int type) {
        if(image_detail != null) image_detail.setImageType(type);
    }

    public void setImageSim(int sim) {
        if(image_detail != null) image_detail.setImageSim(sim);
    }

    public String getImageCropPath() {
        return image_detail == null ? "" : image_detail.getImageCropPath();
    }

    public String getImageName() {
        return image_detail == null ? "" : image_detail.getDescName();
    }
    
    public String getImageOriginal() {
        return image_detail == null ? "" : image_detail.getImageOriginal();
    }

    public void setImageOriginal(String original) {
        if(image_detail != null) image_detail.setImageOriginal(original);
    }
    public void setImageName(String name) {
        if(image_detail != null) image_detail.setDescName(name);
    }
    public void setImageCropPath(String path) {
        if(image_detail != null) image_detail.setImageCropPath(path);
    }

    public void setSearchRect(Rect searchRect) {
        if(searchRect != null){
            setCoord(new SECoordFixed(searchRect.left, searchRect.top));
            setRange(new SERangeSize(searchRect.width(), searchRect.height()));
        }
    }
}
