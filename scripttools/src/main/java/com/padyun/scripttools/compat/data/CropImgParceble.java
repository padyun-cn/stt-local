package com.padyun.scripttools.compat.data;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;

import com.padyun.scripttoolscore.compatible.data.model.SEImage;

/**
 * Created by daiepngfei on 10/15/19
 */
public class CropImgParceble implements Parcelable {
    public static final int CLICK = 0;
    public static final int OFFSET = 1;
    public static final int SLIDE = 2;
    public static final int FINISH = 3;
    public static final int FLAG = 4;
    private SEImage image;
    private int offsetX, offsetY;
    private Rect slideStart;
    private Rect slideEnd;
    private int mode = CLICK;

    public SEImage getImage() {
        return image;
    }

    public void setImage(SEImage image) {
        this.image = image;
    }

    public Rect getSlideStart() {
        return slideStart;
    }

    public void setSlideStart(Rect slideStart) {
        this.slideStart = slideStart;
    }

    public Rect getSlideEnd() {
        return slideEnd;
    }

    public void setSlideEnd(Rect slideEnd) {
        this.slideEnd = slideEnd;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public CropImgParceble(int mode) {
        this.mode = mode;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.image, flags);
        dest.writeInt(this.offsetX);
        dest.writeInt(this.offsetY);
        dest.writeParcelable(this.slideStart, flags);
        dest.writeParcelable(this.slideEnd, flags);
        dest.writeInt(this.mode);
    }

    protected CropImgParceble(Parcel in) {
        this.image = in.readParcelable(SEImage.class.getClassLoader());
        this.offsetX = in.readInt();
        this.offsetY = in.readInt();
        this.slideStart = in.readParcelable(Rect.class.getClassLoader());
        this.slideEnd = in.readParcelable(Rect.class.getClassLoader());
        this.mode = in.readInt();
    }

    public static final Parcelable.Creator<CropImgParceble> CREATOR = new Parcelable.Creator<CropImgParceble>() {
        @Override
        public CropImgParceble createFromParcel(Parcel source) {
            return new CropImgParceble(source);
        }

        @Override
        public CropImgParceble[] newArray(int size) {
            return new CropImgParceble[size];
        }
    };
}
