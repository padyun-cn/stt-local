package com.padyun.scripttoolscore.compatible.data.model.actions;


import android.graphics.Rect;

import com.padyun.scripttoolscore.compatible.data.model.ISEImageContainer;
import com.padyun.scripttoolscore.compatible.data.model.SEImage;
import com.padyun.scripttoolscore.compatible.data.model.coord.SECoord;
import com.padyun.scripttoolscore.compatible.data.model.item.SEItemImage;
import com.padyun.scripttoolscore.compatible.data.model.range.SERange;
import com.uls.utilites.io.Files;


/**
 * Created by daiepngfei on 1/16/19
 */
@SuppressWarnings("all")
public class SEActionImage extends SEAction implements ISEImageContainer {
    /**
     * type : image
     * coord : {}
     * range : {}
     * shift_x :
     * shift_y :
     * “delay” :
     */

    private final String type = TYPE_IMAGE;
    private String image_info;
    private SECoord coord;
    private SERange range;
    private int shift_x;
    private int shift_y;
    private SEImage image_detail;

    private int delay = 1000; // FIXME check this code UNIT-MS

    public Rect getCropBounds(){
        return image_detail == null ? null : image_detail.getBounds();
    }



    public Rect getOffsetBounds(){
        Rect rect = null;
        final Rect cropRect = getCropBounds();
        if(cropRect != null){
            rect = new Rect(cropRect.left + getShift_x(), cropRect.top + getShift_y(), cropRect.right + getShift_x(), cropRect.bottom + getShift_y());
        }
        return rect;
    }

    public SEImage getImage_detail() {
        return image_detail;
    }

    public void mergeOrSetNewImage(SEImage image){
        if(image != null){
            if(image_detail != null) image_detail.merge(image);
            else image_detail = image;
        }
        setImage_info(Files.name(getImageCropPath()));
    }

    public void setImage_detail(SEImage image_detail) {
        this.image_detail = image_detail;
        setImage_info(Files.name(getImageCropPath()));
    }

    @Override
    protected String getSeType() {
        return "action_image";
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

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public boolean isOffset(){
        return getShift_x() != 0 || getShift_y() != 0;
    }


    public String getSignature(){
        return getSEImage() == null ? System.currentTimeMillis() + "" : getSEImage().getSignature();
    }

    public SEItemImage genItemImage() {
        SEItemImage image = new SEItemImage();
        image.setImage_detail(getImage_detail());
        image.setCoord(getCoord());
        image.setRange(getRange());
        if (getImage_detail() != null) {
            image.setImage_info(getImage_detail().getCropFileName());
        }
        return image;
    }

    @Override
    public SEImage getSEImage() {
        return image_detail;
    }

    public SEActionImage seClone(){
        SEActionImage image = null;
        try {
            image = (SEActionImage) clone();
            if(getRange() != null) image.setRange(getRange().duplicate());
            if(getCoord() != null) image.setCoord(getCoord().duplicate());
            if(getImage_detail() != null) image.setImage_detail(getImage_detail().seClone(true));
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    public Object duplicate() throws CloneNotSupportedException {
        SEActionImage image = (SEActionImage) clone();
        image.newAid();
        if(getRange() != null) image.setRange(getRange().duplicate());
        if(getCoord() != null) image.setCoord(getCoord().duplicate());
        return image;
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
}
