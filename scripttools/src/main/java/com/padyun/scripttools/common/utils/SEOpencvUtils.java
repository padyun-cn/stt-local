package com.padyun.scripttools.common.utils;

import android.graphics.Bitmap;

import com.uls.utilites.un.Useless;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;


/**
 * Created by daiepngfei on 2/19/19
 */
public class SEOpencvUtils {

    public static Mat getMatBGROfFile565(String path, Rect crop) {
        return getMat8UC3OfFile565(path, crop, true);
    }

    public static Mat getMat8UC3OfFile8888(String path, Rect crop, boolean convertTOBGR) {
        return getMat8UC3OfFile(path, Bitmap.Config.ARGB_8888, crop, convertTOBGR);
    }

    public static Mat getMat8UC3OfFile565(String path, Rect crop, boolean convertTOBGR) {
        return getMat8UC3OfFile(path, Bitmap.Config.RGB_565, crop, convertTOBGR);
    }

    public static Mat getMatBGROfFile8888(String path, Rect crop) {
        return getMat8UC3OfFile8888(path, crop, true);
    }

    /**
     * Default Config : @link{Bitmap.Config.RGB_565}, crop : null, RGB default
     * @param path
     * @return
     */
    public static Mat getMat8UC3OfFile(String path) {
        return getMat8UC3OfFile(path, Bitmap.Config.RGB_565, null, false);
    }

    public static Mat getMat8UC3OfFile(String path, Bitmap.Config config, Rect crop, boolean convertToBGR) {
        if (Useless.nulls(path)) return null;
        final MatWrapper mat = new MatWrapper();
        CoBitmapWorker.consume(path, config, bm -> {
            Mat cvtMat = new Mat();
            Utils.bitmapToMat(bm, cvtMat);
            Mat cropMat;
            if (crop == null) cropMat = cvtMat;
            else {
                cropMat = new Mat(cvtMat, crop);
                cvtMat.release();
            }
            mat.mat = new Mat();
            Imgproc.cvtColor(cropMat, mat.mat, convertToBGR ? Imgproc.COLOR_RGBA2BGR : Imgproc.COLOR_RGBA2RGB);
            cropMat.release();
//            if(convertToBGR) {
//                mat.mat = new Mat();
//                Imgproc.cvtColor(cropMat, mat.mat, Imgproc.COLOR_RGBA2BGR);
//                cropMat.opengl_render_release();
//            } else mat.mat = cropMat;
            //cvtMat.opengl_render_release();
        });
        return mat.mat == null || mat.mat.width() == 0 || mat.mat.height() == 0 ? null : mat.mat;
    }

    public static class MatWrapper {
        Mat mat;
    }

}
