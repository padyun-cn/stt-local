package com.padyun.scripttools.biz.ui.content.imgtool;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import com.uls.utilites.un.Useless;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * Created by daiepngfei on 7/1/19
 */
@SuppressWarnings("WeakerAccess")
public class SEImgConvertor {
    private static final String TAG = "SEImgConvertor#";

    public interface OnConversion {
        void onConversion(Mat c, Bitmap bitmap);
    }

    private String imgPath;
    private Rect cropRect;
    private Mat primitiveBGRSourceMat, primitiveBGRCropMat, convertMat;
    private boolean isConvertingBitmap;
    private OnConversion onConversion;
    private Bitmap.Config bitmapConfig = Bitmap.Config.ARGB_8888;


    public SEImgConvertor() {
    }

    public SEImgConvertor(String imgPath) {
        this(imgPath, false);
    }

    public SEImgConvertor(String imgPath, boolean isConvertingBitmap) {
        this(imgPath, null, isConvertingBitmap);
    }

    public SEImgConvertor(String imgPath, Rect cropRect, boolean isConvertingBitmap) {
        config(imgPath, cropRect, isConvertingBitmap);
    }

    public void config(String imgPath, Rect cropRect, boolean isConvertingBitmap) {
        setImgPath(imgPath);
        setCropRect(cropRect);
        setConvertingBitmap(isConvertingBitmap);
    }


    public void setBitmapConfig(Bitmap.Config config) {
        if (config != null) bitmapConfig = config;
    }

    public void setImgPath(String imgPath) {
        if (!Useless.equals(this.imgPath, imgPath)) {
            resetAll();
        }
        this.imgPath = imgPath;
    }

    public void setCropRect(Rect cropRect) {
        if (cropRect != null && this.cropRect != null &&
                cropRect.left == this.cropRect.left && cropRect.right == this.cropRect.right &&
                cropRect.top == this.cropRect.top && cropRect.bottom == this.cropRect.bottom) {
            return;
        }
        resetCropMat();
        this.cropRect = cropRect;
    }

    public void setConvertingBitmap(boolean convertingBitmap) {
        isConvertingBitmap = convertingBitmap;
    }

    public void setOnConversion(OnConversion onConversion) {
        this.onConversion = onConversion;
    }

    public Mat getConvertedMat() {
        return convertMat;
    }

    private Bitmap getConvertBitmap() {
        final Mat mat = convertMat;
        Bitmap bitmap = null;
        if (mat != null && !mat.empty()) {
            bitmap = Bitmap.createBitmap(mat.width(), mat.height(), bitmapConfig);
            if (mat.channels() == 3 || mat.channels() == 4) {
                Mat matD = new Mat();
                Imgproc.cvtColor(mat, matD, Imgproc.COLOR_BGR2RGB);
                Utils.matToBitmap(matD, bitmap);
                matD.release();
            } else {
                Utils.matToBitmap(mat, bitmap);
            }
        }
        return bitmap;
    }

    public void cColor() {
        setConvertMat(getPrimitiveBGRCropMat());
    }

    public void cHLS() {
        final Mat primitive = getPrimitiveBGRCropMat();
        if (primitive == null) return;
        // gray scale
        final Mat hlsMat = new Mat();
        Imgproc.cvtColor(primitive, hlsMat, Imgproc.COLOR_BGR2HLS);
        setConvertMat(hlsMat);
    }

    public void cThreshold(int threshold, int maxval, int type) {
        final Mat primitive = getPrimitiveBGRCropMat();
        if (primitive == null) return;
        // gray scale
        final Mat grayScaleMat = new Mat();
        Imgproc.cvtColor(primitive, grayScaleMat, Imgproc.COLOR_BGR2GRAY);
        //
        final Mat thresholdMat = new Mat();
        Imgproc.threshold(grayScaleMat, thresholdMat, threshold, maxval, type);
        grayScaleMat.release();
        setConvertMat(thresholdMat);
    }

    private void setConvertMat(Mat thresholdMat) {
        if (convertMat != null && primitiveBGRSourceMat != null && primitiveBGRCropMat != null &&
                convertMat.nativeObj != primitiveBGRSourceMat.nativeObj &&
                convertMat.nativeObj != primitiveBGRCropMat.nativeObj) {
            convertMat.release();
        }
        convertMat = thresholdMat;
        if (onConversion != null && convertMat != null && !convertMat.empty()) {
            onConversion.onConversion(convertMat, isConvertingBitmap ? getConvertBitmap() : null);
        }
    }

    private Mat getPrimitiveBGRCropMat() {
        if (primitiveBGRSourceMat == null) {
            final Mat mat = Imgcodecs.imread(imgPath, Imgcodecs.CV_LOAD_IMAGE_COLOR);
            if (mat == null || mat.empty()) {
                Log.e(TAG, "empty or null mat when read from '" + imgPath + "'");
                throw new IllegalArgumentException("empty or null mat when read from '" + imgPath + "'");
            }
            primitiveBGRSourceMat = mat;
        }
        if (primitiveBGRCropMat == null) {
            final Mat mat = primitiveBGRSourceMat;
            if (cropRect != null) {
                final Rect r = cropRect;
                final boolean valid = r.left >= 0 && r.top >= 0 && r.right <= mat.width() && r.bottom <= mat.height();
                if (valid) {
                    primitiveBGRCropMat = new Mat(primitiveBGRSourceMat, new org.opencv.core.Rect(r.left, r.top, r.width(), r.height()));
                    //Mat submat = mat.submat(new Range(r.top, r.bottom), new Range(r.left, r.right));
                    //primitiveBGRCropMat = new Mat(mat, new Range(r.left, r.right), new Range(r.top, r.bottom));
                }
            } else primitiveBGRCropMat = new Mat(mat.nativeObj);
        }
        return primitiveBGRCropMat;
    }

    private void resetAll() {
        resetSourceMat();
        resetCropMat();
        resetConvertMat();
    }

    private void resetSourceMat() {
        if (primitiveBGRSourceMat != null) {
            primitiveBGRSourceMat.release();
            primitiveBGRSourceMat = null;
        }
    }


    private void resetConvertMat() {
        if (convertMat != null) {
            convertMat.release();
            convertMat = null;
        }
    }

    private void resetCropMat() {
        if (primitiveBGRCropMat != null) {
            primitiveBGRCropMat.release();
            primitiveBGRCropMat = null;
        }
    }

    public void release() {
        resetAll();
    }
}
