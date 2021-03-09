package com.padyun.scripttoolscore.compatible.data.model;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by daiepngfei on 8/14/19
 */
public class SEColor implements Serializable, Parcelable {
    private int color;
    private Rect rect;
    private String path;

    public SEColor(int color, String path, Rect rect) {
        this.path = path;
        this.color = color;
        this.rect = rect;
    }

    public int getColor() {
        return color;
    }

    public Rect getRect() {
        return rect;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.color);
        dest.writeParcelable(this.rect, flags);
        dest.writeString(this.path);
    }

    protected SEColor(Parcel in) {
        this.color = in.readInt();
        this.rect = in.readParcelable(Rect.class.getClassLoader());
        this.path = in.readString();
    }

    public static final Creator<SEColor> CREATOR = new Creator<SEColor>() {
        @Override
        public SEColor createFromParcel(Parcel source) {
            return new SEColor(source);
        }

        @Override
        public SEColor[] newArray(int size) {
            return new SEColor[size];
        }
    };

    public String getPath() {
        return path;
    }
}
