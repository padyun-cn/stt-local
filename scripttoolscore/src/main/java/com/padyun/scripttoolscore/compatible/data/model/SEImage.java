package com.padyun.scripttoolscore.compatible.data.model;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;

import com.padyun.scripttoolscore.compatible.plugin.ImageModule;

import androidx.annotation.Nullable;
import com.uls.utilites.un.Useless;


/**
 * Created by daiepngfei on 1/16/19
 */
public class SEImage extends SEBase implements Parcelable {

    private  String signature;

    public SEImage(){
        newSignature();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id = IDGen.genTmsStrRandom("image");

    public void newSignature(){
        signature = genNewSignature();
    }

    /**
     * 用户设置名称
     */
    private String name;

    /**
     * 图片文件名
     */
    private String fileName;
    /**
     * 图片路径
     */
    private String path;
    /**
     * 图片原图路径
     */
    private String original;

    /**
     *
     */
    private String rawImageName;


    public String getRawImageName() {
        return rawImageName;
    }

    /**
     * 图片信息
     */
    private ImageInfo info;

    public String getDescName() {
        return name;
    }

    public String getImageCropPath() {
        return path;
    }


    public String getImageOriginal() {
        return original;
    }

    public ImageInfo getImageInfo() {
        return info;
    }

    public void setImageOriginal(String original) {
        this.original = original;
    }

    public void setDescName(String name) {
        this.name = name;
    }

    public void setImageCropPath(String path) {
        this.path = path;
    }
    public void setImageInfo(ImageInfo info) {
        this.info = info;
    }

    public void setCropFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getCropFileName() {
        return fileName;
    }

    public void merge(SEImage img) {
        if(img == null) return;
        this.setImageCropPath(img.path);
        this.setCropFileName(img.fileName);
        this.setImageInfo(img.info);
        this.setImageOriginal(img.original);
        this.setDescName(img.name);
        this.signature = img.signature;
    }

    public SEImage seClone(boolean deeply){
        SEImage image = new SEImage();
        image.merge(this);
        if(deeply) {
            ImageInfo info = new ImageInfo();
            info.setFlag(getImageFlag());
            info.setH(getImageH());
            info.setMaxval(getImageMaxval());
            info.setSim(getImageSim());
            info.setThreshold(getImageThreshold());
            info.setType(getImageType());
            info.setW(getImageW());
            info.setX(getImageX());
            info.setY(getImageY());
            image.setImageInfo(info);
            image.id = this.id;
        }
        return image;
    }

    public void newId(){
        this.id = IDGen.genTmsStrRandom("image");
    }

    public SEImage duplicate(){
        return seClone(false);
    }

    public void regenSignature(){
        this.signature = genNewSignature();
    }

    public String getSignature(){
       /* if(signature == null){
            regenSignature();
        }*/
       regenSignature();
        return signature;
    }

    public Rect getBounds(){
        Rect rect = null;
        if(getImageInfo() != null){
            ImageInfo info = getImageInfo();
            rect = new Rect(info.x, info.y, info.x + info.w, info.y + info.h);
        }
        return rect;
    }

    private String genNewSignature() {
        return String.valueOf(System.currentTimeMillis());
    }



    public int getImageX() {
        return info == null ? 0 : info.x;
    }

    public int getImageY() {
        return info == null ? 0 : info.y;
    }

    public int getImageW() {
        return info == null ? 0 : info.w;
    }

    public int getImageH() {
        return info == null ? 0 : info.h;
    }

    public int getImageFlag() {
        return info == null ? 0 : info.flag;
    }

    public int getImageThreshold() {
        return info == null ? 0 : info.threshold;
    }

    public int getImageMaxval() {
        return info == null ? 0 : info.maxval;
    }

    public int getImageType() {
        return info == null ? 0 : info.type;
    }

    public int getImageSim() {
        return info == null ? 0 : info.sim;
    }


    public void setImageX(int x) {
        if(info != null) info.x = x;
    }

    public void setImageY(int y) {
        if(info != null) info.y = y;
    }

    public void setImageW(int w) {
        if(info != null) info.w = w;
    }

    public void setImageH(int h) {
        if(info != null) info.h = h;
    }

    public void setImageFlag(int flag) {
        if(info != null) info.flag = flag;
    }

    public void setImageThreshold(int threshold) {
        if(info != null) info.threshold = threshold;
    }

    public void setImageMaxval(int maxval) {
        if(info != null) info.maxval = maxval;
    }

    public void setImageType(int type) {
        if(info != null) info.type = type;
    }

    public void setImageSim(int sim) {
        if(info != null) info.sim = sim;
    }

    public boolean isEmpty() {
        return Useless.hasEmptyIn(getImageOriginal(), getImageCropPath());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.signature);
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.fileName);
        dest.writeString(this.path);
        dest.writeString(this.original);
        dest.writeParcelable(this.info, flags);
    }

    protected SEImage(Parcel in) {
        this.signature = in.readString();
        this.id = in.readString();
        this.name = in.readString();
        this.fileName = in.readString();
        this.path = in.readString();
        this.original = in.readString();
        this.info = in.readParcelable(ImageInfo.class.getClassLoader());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        //noinspection ConditionCoveredByFurtherCondition
        return obj != null && obj instanceof SEImage && Useless.nonNullStr(this.getImageCropPath()).equals(((SEImage)obj).getImageCropPath());
    }

    public static final Creator<SEImage> CREATOR = new Creator<SEImage>() {
        @Override
        public SEImage createFromParcel(Parcel source) {
            return new SEImage(source);
        }

        @Override
        public SEImage[] newArray(int size) {
            return new SEImage[size];
        }
    };
}
