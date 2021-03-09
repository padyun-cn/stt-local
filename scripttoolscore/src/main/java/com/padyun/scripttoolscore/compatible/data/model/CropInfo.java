package com.padyun.scripttoolscore.compatible.data.model;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by daiepngfei on 6/27/19
 */
public class CropInfo implements Parcelable {
    public int x, y, w, h;
    public String imgFile;
    public boolean isImgFileNewScreen;
    private Bundle extras;

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

    public String getImgFile() {
        return imgFile;
    }

    public boolean isImgFileNewScreen() {
        return isImgFileNewScreen;
    }

    public Rect getBounds(){
        return new Rect(x, y, x + w, y + w);
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
        dest.writeString(this.imgFile);
        dest.writeByte(this.isImgFileNewScreen ? (byte) 1 : (byte) 0);
        dest.writeBundle(this.extras);
    }

    public CropInfo(Rect rect){
        this.x = rect.left;
        this.y = rect.top;
        this.w = rect.width();
        this.h = rect.height();
    }

    public CropInfo() {
    }

    protected CropInfo(Parcel in) {
        this.x = in.readInt();
        this.y = in.readInt();
        this.w = in.readInt();
        this.h = in.readInt();
        this.imgFile = in.readString();
        this.isImgFileNewScreen = in.readByte() != 0;
        this.extras = in.readBundle();
    }

    public static final Creator<CropInfo> CREATOR = new Creator<CropInfo>() {
        @Override
        public CropInfo createFromParcel(Parcel source) {
            return new CropInfo(source);
        }

        @Override
        public CropInfo[] newArray(int size) {
            return new CropInfo[size];
        }
    };
}
