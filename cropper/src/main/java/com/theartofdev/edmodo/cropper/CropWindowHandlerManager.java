package com.theartofdev.edmodo.cropper;

import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

import androidx.core.util.Consumer;

/**
 * Created by daiepngfei on 2020-02-15
 */
public class CropWindowHandlerManager {

    private static final int FOLK_MAX = 2;

    /** Minimum width in pixels that the crop window can get. */
    private float mMinCropWindowWidth;

    /** Minimum height in pixels that the crop window can get. */
    private float mMinCropWindowHeight;

    /** Maximum width in pixels that the crop window can CURRENTLY get. */
    private float mMaxCropWindowWidth;

    /** Maximum height in pixels that the crop window can CURRENTLY get. */
    private float mMaxCropWindowHeight;

    /**
     * Minimum width in pixels that the result of cropping an image can get, affects crop window
     * width adjusted by width scale factor.
     */
    private float mMinCropResultWidth;

    /**
     * Minimum height in pixels that the result of cropping an image can get, affects crop window
     * height adjusted by height scale factor.
     */
    private float mMinCropResultHeight;

    /**
     * Maximum width in pixels that the result of cropping an image can get, affects crop window
     * width adjusted by width scale factor.
     */
    private float mMaxCropResultWidth;

    /**
     * Maximum height in pixels that the result of cropping an image can get, affects crop window
     * height adjusted by height scale factor.
     */
    private float mMaxCropResultHeight;

    /** The width scale factor of shown image and actual image */
    private float mScaleFactorWidth = 1;

    /** The height scale factor of shown image and actual image */
    private float mScaleFactorHeight = 1;
    // endregion
    private List<CropWindowHandler> mCropWindowHandlers = new ArrayList<>();
    private CropWindowHandler mFocusedCropWindowHandler;

    CropWindowHandlerManager() {
        mCropWindowHandlers.add(new CropWindowHandler(this));
        mFocusedCropWindowHandler = mCropWindowHandlers.get(0);
    }

    public void reset(){
        mCropWindowHandlers.clear();
        mCropWindowHandlers.add(mFocusedCropWindowHandler);
    }

    public boolean defolk() {
        if(mCropWindowHandlers.size() > 1){
            mCropWindowHandlers.remove(mCropWindowHandlers.size() - 1);
            mFocusedCropWindowHandler = mCropWindowHandlers.get(mCropWindowHandlers.size() - 1);
            return true;
        }
        return false;
    }

    public Rect folk(int offset) {
        if(mCropWindowHandlers.size() >= FOLK_MAX){
            return null;
        }
        RectF f = new RectF(mFocusedCropWindowHandler.getRect());
        f.offset(offset, offset);
        f.right = Math.min(f.right, getMaxCropWidth());
        f.bottom = Math.min(f.bottom, getMaxCropHeight());
        CropWindowHandler handler = new CropWindowHandler(this);
        handler.setRect(f);
        mCropWindowHandlers.add(handler);
        mFocusedCropWindowHandler = handler;
        return new Rect((int)f.left, (int)f.top, (int)f.right, (int)f.bottom);
    }

    private CropWindowHandler getFocusedCropWindowHandler() {
        return mFocusedCropWindowHandler;
    }

    public List<RectF> getAllRects() {
        final List<RectF> rectFS = new ArrayList<>();
        for (CropWindowHandler handler : mCropWindowHandlers) {
            if (handler != null && handler.getRect() != null) {
                rectFS.add(handler.getRect());
            }
        }
        return rectFS;
    }

    public RectF getRect() {
        return getRect(-1);
    }

    /**
     * @param consumer
     */
    public void foreachUnfocusedRects(Consumer<RectF> consumer) {
        for (CropWindowHandler handler : mCropWindowHandlers) {
            if (handler != null /*&& handler.getRect() != null*/ && handler != mFocusedCropWindowHandler) {
                consumer.accept(handler.getRect());
            }
        }
    }

    public List<RectF> getUnfocusedRects() {
        final List<RectF> rects = new ArrayList<>();
        for (CropWindowHandler handler : mCropWindowHandlers) {
            if (handler != null /*&& handler.getRect() != null*/ && handler != mFocusedCropWindowHandler) {
                rects.add(handler.getRect());
            }
        }
        return rects;
    }

    public RectF getRect(int index) {
        if (index < 0 || index >= mCropWindowHandlers.size()) {
            return getFocusedCropWindowHandler().getRect();
        }
        return mCropWindowHandlers.get(index).getRect();
    }

    public void setRect(RectF rect) {
        setRect(-1, rect);
    }

    public void setRect(int index, RectF rect) {
        if (index < 0 || index >= mCropWindowHandlers.size()) {
            getFocusedCropWindowHandler().setRect(rect);
        } else {
            mCropWindowHandlers.get(index).setRect(rect);
        }
    }

    /**
     * TODO:
     *
     * @param event
     */
    public void onFocusTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // event.
        }
    }

    /**
     * the min size the resulting cropping image is allowed to be, affects the cropping window
     * limits (in pixels).<br>
     */
    public void setMinCropResultSize(int minCropResultWidth, int minCropResultHeight) {
        mMinCropResultWidth = minCropResultWidth;
        mMinCropResultHeight = minCropResultHeight;
    }

    /**
     * the max size the resulting cropping image is allowed to be, affects the cropping window
     * limits (in pixels).<br>
     */
    public void setMaxCropResultSize(int maxCropResultWidth, int maxCropResultHeight) {
        mMaxCropResultWidth = maxCropResultWidth;
        mMaxCropResultHeight = maxCropResultHeight;
    }

    /**
     * set the max width/height and scale factor of the showen image to original image to scale the
     * limits appropriately.
     */
    public void setCropWindowLimits(
            float maxWidth, float maxHeight, float scaleFactorWidth, float scaleFactorHeight) {
        mMaxCropWindowWidth = maxWidth;
        mMaxCropWindowHeight = maxHeight;
        mScaleFactorWidth = scaleFactorWidth;
        mScaleFactorHeight = scaleFactorHeight;
    }

    /** Set the variables to be used during crop window handling. */
    public void setInitialAttributeValues(CropImageOptions options) {
        mMinCropWindowWidth = options.minCropWindowWidth;
        mMinCropWindowHeight = options.minCropWindowHeight;
        mMinCropResultWidth = options.minCropResultWidth;
        mMinCropResultHeight = options.minCropResultHeight;
        mMaxCropResultWidth = options.maxCropResultWidth;
        mMaxCropResultHeight = options.maxCropResultHeight;
    }


    /** Minimum width in pixels that the crop window can get. */
    public float getMinCropWidth() {
        return Math.max(mMinCropWindowWidth, mMinCropResultWidth / mScaleFactorWidth);
//    return Math.min(mMinCropWindowWidth, mMinCropResultWidth / mScaleFactorWidth);
    }

    /** Minimum height in pixels that the crop window can get. */
    public float getMinCropHeight() {
        return Math.max(mMinCropWindowHeight, mMinCropResultHeight / mScaleFactorHeight);
//    return Math.min(mMinCropWindowHeight, mMinCropResultHeight / mScaleFactorHeight);
    }

    /** Maximum width in pixels that the crop window can get. */
    public float getMaxCropWidth() {
        return Math.min(mMaxCropWindowWidth, mMaxCropResultWidth / mScaleFactorWidth);
    }

    /** Maximum height in pixels that the crop window can get. */
    public float getMaxCropHeight() {
        return Math.min(mMaxCropWindowHeight, mMaxCropResultHeight / mScaleFactorHeight);
    }

    /** get the scale factor (on width) of the showen image to original image. */
    public float getScaleFactorWidth() {
        return mScaleFactorWidth;
    }

    /** get the scale factor (on height) of the showen image to original image. */
    public float getScaleFactorHeight() {
        return mScaleFactorHeight;
    }

    public CropWindowMoveHandler getMoveHandler(
            float x, float y, float targetRadius, CropImageView.CropShape cropShape) {
        return getFocusedCropWindowHandler().getMoveHandler(x, y, targetRadius, cropShape);
    }

    public boolean showGuidelines() {
        return getFocusedCropWindowHandler().showGuidelines();
    }

}
