package com.padyun.scripttools.compat.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daiepngfei on 10/15/19
 */
public class CropImgGroupParceble implements Parcelable {

    private ArrayList<CropImgParceble> imgs = new ArrayList<>();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.imgs);
    }

    public CropImgGroupParceble() {
    }

    public void add(CropImgParceble parceble) {
        if (parceble != null) {
            this.imgs.add(parceble);
        }
    }

    protected CropImgGroupParceble(Parcel in) {
        this.imgs = in.createTypedArrayList(CropImgParceble.CREATOR);
    }

    public static final Creator<CropImgGroupParceble> CREATOR = new Creator<CropImgGroupParceble>() {
        @Override
        public CropImgGroupParceble createFromParcel(Parcel source) {
            return new CropImgGroupParceble(source);
        }

        @Override
        public CropImgGroupParceble[] newArray(int size) {
            return new CropImgGroupParceble[size];
        }
    };

    public boolean isEmpty() {
        return imgs.size() == 0;
    }

    @NotNull
    public List<CropImgParceble> group() {
        return imgs;
    }

    public static AbsCoImage<?> conAbsCondition(CropImgGroupParceble cropImgGroupParceble) {
        if (cropImgGroupParceble == null) return null;

        AbsCoImage condition;
        // temp

        final List<CropImgParceble> cropImgParcebles = cropImgGroupParceble.group();
        final CropImgParceble tar = cropImgParcebles.size() == 2 ? cropImgParcebles.get(1) : cropImgParcebles.get(0);
        switch (tar.getMode()) {
            case CropImgParceble.CLICK:
                condition = new CoClick(tar.getImage());
                break;
            case CropImgParceble.OFFSET:
                condition = new CoOffset(tar.getImage());
                ((CoOffset)condition).setOffset(tar.getOffsetX(), tar.getOffsetY());
                break;
            case CropImgParceble.SLIDE:
                condition = new CoSlide(tar.getImage());
                ((CoSlide)condition).setSlide(tar.getSlideStart(), tar.getSlideEnd());
                break;
            case CropImgParceble.FINISH:
                condition = new CoFinish(tar.getImage());
                break;
            default:
                condition = null;
        }

        if(cropImgParcebles.size() == 2 && condition != null){
            ((AbsCoImage<?>) condition).setNonExsitExtra(cropImgParcebles.get(0).getImage());
        }

        return condition;

        // temp
    }
}
