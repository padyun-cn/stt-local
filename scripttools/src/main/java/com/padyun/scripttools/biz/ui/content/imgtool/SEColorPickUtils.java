package com.padyun.scripttools.biz.ui.content.imgtool;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;

import androidx.core.util.Consumer;
import com.padyun.scripttools.common.utils.CoBitmapWorker;
import com.padyun.scripttoolscore.compatible.data.model.item.SEItemColor;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * Created by daiepngfei on 6/27/19
 */
public class SEColorPickUtils {

    /**
     *
     * @param colorItem
     * @param resultConsumer
     */
    public static void getColorCountWithItemColor(SEItemColor colorItem, Consumer<Integer> resultConsumer){
        getColorCountWithItemColor(colorItem, null, resultConsumer);
    }

    public static void getColorCountWithItemColor(SEItemColor colorItem, Consumer<Bitmap> bitmapConsumer, Consumer<Integer> resultConsumer){
        if(colorItem != null && resultConsumer != null){
            final Rect rect = colorItem.getBounds();
            final String path = colorItem.getOriginal();
            final int color = colorItem.getColor();
            final float sim = colorItem.getSim();
            getColorCountWithItemColor(color, rect, path, sim, bitmapConsumer, resultConsumer);
        }
    }
    /**
     *
     * @param resultConsumer
     */
    public static void getColorCountWithItemColor(int color, Rect rect, String imgPath, float sim, Consumer<Bitmap> bitmapConsumer, Consumer<Integer> resultConsumer){
        if(resultConsumer != null){
            CoBitmapWorker.consume64(imgPath, t -> {
                if(bitmapConsumer != null) bitmapConsumer.accept(t);
                resultConsumer.accept(getColorCountWithBitmap(rect, color, sim, t));
            });
        }
    }

    /**
     *
     * @param rect
     * @param color
     * @param sim
     * @param t
     * @return
     */
    public static int getColorCountWithBitmap(Rect rect, int color, float sim, Bitmap t) {
        int count = 0;
        if(rect.isEmpty()) return count;
        Bitmap bm = Bitmap.createBitmap(t, rect.left, rect.top, rect.width(), rect.height());
        if (bm != null) {
            Mat mat = new Mat();
            Mat dest = new Mat();
            Utils.bitmapToMat(bm, mat);
            Imgproc.cvtColor(mat, dest, Imgproc.COLOR_RGBA2RGB);
            mat.release();

            count = getColorCount(Color.red(color), Color.green(color), Color.blue(color), sim, dest);
            dest.release();
        }
        return count;
    }

    private static int getColorCount(int r, int g, int b, float sim, Mat mat) {
        double simValue = 255 * (1 - sim);
        double t;
        double min_r = (t = r - simValue) < 0 ? 0 : t;
        double min_g = (t = g - simValue) < 0 ? 0 : t;
        double min_b = (t = b - simValue) < 0 ? 0 : t;
        double max_r = (t = r + simValue) > 255 ? 255 : t;
        double max_g = (t = g + simValue) > 255 ? 255 : t;
        double max_b = (t = b + simValue) > 255 ? 255 : t;

        Scalar minValues = new Scalar(min_r, min_g, min_b);
        Scalar maxValues = new Scalar(max_r, max_g, max_b);
        Core.inRange(mat, minValues, maxValues, mat);
        return Core.countNonZero(mat);
    }
}
