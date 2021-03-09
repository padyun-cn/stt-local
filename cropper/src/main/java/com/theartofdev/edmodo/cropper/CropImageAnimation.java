// "Therefore those skilled at the unorthodox
// are infinite as heaven and earth,
// inexhaustible as the great rivers.
// When they come to an end,
// they begin again,
// like the days and months;
// they die and are reborn,
// like the four seasons."
//
// - Sun Tsu,
// "The Art of War"

package com.theartofdev.edmodo.cropper;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Animation to handle smooth cropping image matrix transformation change, specifically for
 * zoom-in/out.
 */
final class CropImageAnimation extends Animation implements Animation.AnimationListener {

    // region: Fields and Consts

    private final ImageView mImageView;

    private final CropOverlayView mCropOverlayView;

    private final float[] mStartBoundPoints = new float[8];

    private final float[] mEndBoundPoints = new float[8];

    private final List<RectF> mStartCropWindowRects = new ArrayList<>();
    private final List<RectF> mEndCropWindowRects = new ArrayList<>();

    private final float[] mStartImageMatrix = new float[9];

    private final float[] mEndImageMatrix = new float[9];

//    private final RectF mAnimRect = new RectF();

    private final float[] mAnimPoints = new float[8];

    private final float[] mAnimMatrix = new float[9];
    // endregion
    private Runnable mOnCropImageAnimationEnd;

    /* ADDED BY DAIPENGFEI on 2019-12-12. */
    private boolean mAnimationStarted = false;

    public boolean isAnimationStarted() {
        return mAnimationStarted;
    }
    /* ADDED END. */

    public CropImageAnimation(ImageView cropImageView, CropOverlayView cropOverlayView) {
        mImageView = cropImageView;
        mCropOverlayView = cropOverlayView;

        setDuration(300);
        setFillAfter(true);
        setInterpolator(new AccelerateDecelerateInterpolator());
        setAnimationListener(this);
    }

    public void setStartState(float[] boundPoints, Matrix imageMatrix) {
        reset();
        System.arraycopy(boundPoints, 0, mStartBoundPoints, 0, 8);
        /* MODIFIED BY DAIPENGFEI on 2020-02-15. */
        // mStartCropWindowRects.set(mCropOverlayView.getCropWindowRect());
        mStartCropWindowRects.clear();
        for (RectF f : mCropOverlayView.getAllCropWindowRects()) {
            if (f != null && !f.isEmpty()) {
                mStartCropWindowRects.add(new RectF(f));
            }
        }
        /* MODIFIED END. */
        imageMatrix.getValues(mStartImageMatrix);
    }

    public void setEndState(float[] boundPoints, Matrix imageMatrix) {
        System.arraycopy(boundPoints, 0, mEndBoundPoints, 0, 8);
        /* MODIFIED BY DAIPENGFEI on 2020-02-15. */
        // mEndCropWindowRects.set(mCropOverlayView.getCropWindowRect());
        mEndCropWindowRects.clear();
        for (RectF f : mCropOverlayView.getAllCropWindowRects()) {
            if (f != null && !f.isEmpty()) {
                mEndCropWindowRects.add(new RectF(f));
            }
        }
        /* MODIFIED END. */
        //mEndCropWindowRect0.set(mCropOverlayView.getR());
        imageMatrix.getValues(mEndImageMatrix);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        for(int i = 0; i < mStartCropWindowRects.size() && i < mEndCropWindowRects.size(); i++ ){
            RectF start  = mStartCropWindowRects.get(i);
            RectF end  = mEndCropWindowRects.get(i);
            if(start == null || end == null) continue;
            RectF rect = new RectF();
            rect.left =
                    start.left
                            + (end.left - start.left) * interpolatedTime;
            rect.top =
                    start.top
                            + (end.top - start.top) * interpolatedTime;
            rect.right =
                    start.right
                            + (end.right - start.right) * interpolatedTime;
            rect.bottom =
                    start.bottom
                            + (end.bottom - start.bottom) * interpolatedTime;
            mCropOverlayView.setCropWindowRect(i, rect);
        }

//        mAnimRect.left =
//                mStartCropWindowRects.left
//                        + (mEndCropWindowRects.left - mStartCropWindowRects.left) * interpolatedTime;
//        mAnimRect.top =
//                mStartCropWindowRects.top
//                        + (mEndCropWindowRects.top - mStartCropWindowRects.top) * interpolatedTime;
//        mAnimRect.right =
//                mStartCropWindowRects.right
//                        + (mEndCropWindowRects.right - mStartCropWindowRects.right) * interpolatedTime;
//        mAnimRect.bottom =
//                mStartCropWindowRects.bottom
//                        + (mEndCropWindowRects.bottom - mStartCropWindowRects.bottom) * interpolatedTime;
//        LogUtil.d("nimada#handler", "/animation::1297/");
//        mCropOverlayView.setCropWindowRect(mAnimRect);

        for (int i = 0; i < mAnimPoints.length; i++) {
            mAnimPoints[i] =
                    mStartBoundPoints[i] + (mEndBoundPoints[i] - mStartBoundPoints[i]) * interpolatedTime;
        }
        mCropOverlayView.setBounds(mAnimPoints, mImageView.getWidth(), mImageView.getHeight());

        for (int i = 0; i < mAnimMatrix.length; i++) {
            mAnimMatrix[i] =
                    mStartImageMatrix[i] + (mEndImageMatrix[i] - mStartImageMatrix[i]) * interpolatedTime;
        }
        Matrix m = mImageView.getImageMatrix();
        m.setValues(mAnimMatrix);
        mImageView.setImageMatrix(m);

        mImageView.invalidate();
        mCropOverlayView.invalidate();
    }

    public void setOnCropImageAnimationEnd(Runnable onCropImageAnimationEnd) {
        mOnCropImageAnimationEnd = onCropImageAnimationEnd;
    }

    @Override
    public void onAnimationStart(Animation animation) {
        mAnimationStarted = true;
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        mImageView.clearAnimation();
        mAnimationStarted = false;
        if (mOnCropImageAnimationEnd != null) {
            mOnCropImageAnimationEnd.run();
            mOnCropImageAnimationEnd = null;
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }
}
