package com.padyun.scripttoolscore.compatible.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class ImageInfo extends SEBase implements Parcelable {
    public static final int FLAG_COLOR = 1 ;
    public static final int FLAG_THRESHOLD =-1 ;
    public static final int FLAG_HLS = 52 ;



    /**
     * 图片所在位置
     * */
    public int x,y,w,h ;
    /**
     * 图片颜色类型
     * */
    public int flag = FLAG_COLOR;
    /**
     * 二值化信息
     */
    public int threshold = 150;
    public int maxval  = 255;
    public int type = 0;

    /**
     * 相似度0-100
     * */
    public int sim = 80;

    public ImageInfo() {
        // do nothing
    }
    public ImageInfo(CropInfo cropInfo) {
        Objects.requireNonNull(cropInfo);
        this.x = cropInfo.x;
        this.y = cropInfo.y;
        this.w = cropInfo.w;
        this.h = cropInfo.h;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public int getFlag() {
        return flag;
    }

    public int getThreshold() {
        return threshold;
    }

    public int getMaxval() {
        return maxval;
    }

    public int getType() {
        return type;
    }

    public int getSim() {
        return sim;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setW(int w) {
        this.w = w;
    }

    public void setH(int h) {
        this.h = h;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public void setMaxval(int maxval) {
        this.maxval = maxval;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setSim(int sim) {
        this.sim = sim;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.x);
        dest.writeInt(this.y);
        dest.writeInt(this.w);
        dest.writeInt(this.h);
        dest.writeInt(this.flag);
        dest.writeInt(this.threshold);
        dest.writeInt(this.maxval);
        dest.writeInt(this.type);
        dest.writeInt(this.sim);
    }

    protected ImageInfo(Parcel in) {
        this.x = in.readInt();
        this.y = in.readInt();
        this.w = in.readInt();
        this.h = in.readInt();
        this.flag = in.readInt();
        this.threshold = in.readInt();
        this.maxval = in.readInt();
        this.type = in.readInt();
        this.sim = in.readInt();
    }

    public static final Creator<ImageInfo> CREATOR = new Creator<ImageInfo>() {
        @Override
        public ImageInfo createFromParcel(Parcel source) {
            return new ImageInfo(source);
        }

        @Override
        public ImageInfo[] newArray(int size) {
            return new ImageInfo[size];
        }
    };
}
