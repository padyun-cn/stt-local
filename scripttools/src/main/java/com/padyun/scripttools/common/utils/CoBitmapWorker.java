package com.padyun.scripttools.common.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import com.uls.utilites.content.CoreWorkers;
import com.uls.utilites.common.IAccept;
import com.uls.utilites.common.ICCallback;
import com.uls.utilites.common.IReject;
import com.uls.utilites.content.CAsync;
import com.uls.utilites.content.YpBitmapCacher;
import com.uls.utilites.io.Files;
import com.uls.utilites.un.Useless;

import java.io.FileOutputStream;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;

/**
 * Created by daiepngfei on 2/19/19
 */
public class CoBitmapWorker {

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // First handleFrame with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap consume565(String path, Consumer<Bitmap> consumer) {
        return consume(path, Bitmap.Config.RGB_565, consumer);
    }

    public static void consume8888Croped(String path, Rect rect, Consumer<Bitmap> consumer) {
        consume8888Croped(path, rect, consumer, null);
    }

    public static void consume8888Croped(String path, Rect rect, Consumer<Bitmap> consumer, Runnable reject) {
        consume64(path, img -> {
            if (rect != null && !rect.isEmpty()) {
                try {
                    consumer.accept(Bitmap.createBitmap(img, rect.left, rect.top, rect.width(), rect.height()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (reject != null) {
                reject.run();
            }
        });
    }

    public static Bitmap load(String path) {
        return consume(path, Bitmap.Config.ARGB_8888, null);
    }

    public static Bitmap consume64(String path, Consumer<Bitmap> consumer) {
        return consume(path, Bitmap.Config.ARGB_8888, consumer);
    }

    public static Bitmap consume(String path, Bitmap.Config config, Consumer<Bitmap> consumer) {
        if (!Files.exists(path)) return null;
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inPreferredConfig = config;
        Bitmap bm = BitmapFactory.decodeFile(path);
        if (bm != null && consumer != null) consumer.accept(bm);
        return bm;
    }

    public static boolean saveSync(Bitmap bm, String path) {
        boolean result = false;
        if (bm != null && !Useless.isEmpty(path)) {
            try {
                result = Files.mkFile(path);
                if (result)
                    result = bm.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     *
     * @param uri
     * @param cropRect
     */
    public static void lruInvalidate(String uri, Rect cropRect){
       YpBitmapCacher.ins().invalidate(getUrlCacheKey(uri, cropRect));
    }

    /**
     * @param uri
     * @param croppedRect
     * @param consumer
     */
    @Deprecated
    public static void lruLoad(String uri, Rect croppedRect, ICCallback<Bitmap> consumer) {
        if (Useless.isEmpty(uri) || consumer == null) {
            return;
        }

        final boolean notCrop = croppedRect == null || croppedRect.isEmpty();
        final String urlCache = uri + (notCrop ? "" : ("_" + croppedRect.left + "_" + croppedRect.top + "_" + croppedRect.right + "_" + croppedRect.bottom));

        final Bitmap bm = YpBitmapCacher.ins().get(urlCache);
        if (bm != null) {
            consumer.onSuccess(bm);
            return;
        }

        new CoreWorkers<>(() -> {

            // httpGet primitive bitmap
            Bitmap primitiveBitmap = YpBitmapCacher.ins().get(uri);
            if (primitiveBitmap == null) {
                primitiveBitmap = CoBitmapWorker.consume64(uri, null);
                YpBitmapCacher.ins().put(uri, primitiveBitmap);
            }


            if (primitiveBitmap == null) {
                consumer.onSendFail(-1, "", null);
                return null;
            }

            // just return primitvieBitmap if not cropping
            if (notCrop) {
                return primitiveBitmap;
            }

            // do cropping
            Bitmap croppedBitmap = null;
            if (new Rect(0, 0, primitiveBitmap.getWidth(), primitiveBitmap.getHeight()).contains(croppedRect)) {
                try {
                    Bitmap t = Bitmap.createBitmap(primitiveBitmap, croppedRect.left, croppedRect.top, croppedRect.width(), croppedRect.height());
                    if (t != null) {
                        YpBitmapCacher.ins().put(urlCache, t);
                        croppedBitmap = t;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    consumer.onSendFail(-1, "", e);
                    return null;
                }
            } else {
                consumer.onSendFail(-1, "", null);
            }

            return croppedBitmap;

        }
                , consumer::onSuccess).start();
    }

    /**
     * @param uri
     * @param consumer
     */
    public static void lruLoad(String uri, IAccept<Bitmap> consumer) {
        lruLoad(uri, consumer, null);
    }

    /**
     * @param uri
     * @param consumer
     * @param reject
     */
    public static void lruLoad(String uri, IAccept<Bitmap> consumer, IReject reject) {
        if (Useless.isEmpty(uri) || consumer == null) {
            if (reject != null) {
                reject.reject(null);
            }
            return;
        }

        final Bitmap bm = YpBitmapCacher.ins().get(uri);
        if (bm != null) {
            consumer.accept(bm);
            return;
        }

        CAsync.excute(() -> {
            // httpGet primitive bitmap
            Bitmap primitiveBitmap = YpBitmapCacher.ins().get(uri);
            if (primitiveBitmap == null) {
                primitiveBitmap = CoBitmapWorker.consume64(uri, null);
                YpBitmapCacher.ins().put(uri, primitiveBitmap);
            }
            return primitiveBitmap;
        },  consumer, reject);
    }

    /**
     *
     * @param uri
     * @param croppedRect
     * @param consumer
     */
    public static void lruLoad(String uri, Rect croppedRect, IAccept<Bitmap> consumer) {
        lruLoad(uri, croppedRect, consumer, null);
    }

    /**
     * @param uri
     * @param croppedRect
     * @param consumer
     * @param reject
     */
    public static void lruLoad(String uri, Rect croppedRect, IAccept<Bitmap> consumer, IReject reject) {
        if (Useless.isEmpty(uri) || consumer == null || croppedRect == null || croppedRect.isEmpty()) {
            if (reject != null) {
                reject.reject(null);
            }
            return;
        }

        // httpGet cached url
        final String urlCache = getUrlCacheKey(uri, croppedRect);
        final Bitmap bm = YpBitmapCacher.ins().get(urlCache);
        if (bm != null) {
            consumer.accept(bm);
            return;
        }

        // load bitmap cropped
        CAsync.excute(() -> {
            // httpGet primitive bitmap
            Bitmap primitiveBitmap = YpBitmapCacher.ins().get(uri);
            if (primitiveBitmap == null) {
                primitiveBitmap = CoBitmapWorker.consume64(uri, null);
                YpBitmapCacher.ins().put(uri, primitiveBitmap);
            }

            Bitmap croppedBitmap = null;
            if (primitiveBitmap != null && new Rect(0, 0, primitiveBitmap.getWidth(), primitiveBitmap.getHeight()).contains(croppedRect)) {
                Bitmap t = Bitmap.createBitmap(primitiveBitmap, croppedRect.left, croppedRect.top, croppedRect.width(), croppedRect.height());
                if (t != null) {
                    YpBitmapCacher.ins().put(urlCache, t);
                    croppedBitmap = t;
                }
            }

            if(croppedBitmap == null){
                throw new BitmapLoadFailedException();
            }

            return croppedBitmap;
        }, consumer, reject);
    }

    @NonNull
    private static String getUrlCacheKey(String uri, Rect croppedRect) {
        return croppedRect == null || croppedRect.isEmpty() ? uri : uri + ("_" + croppedRect.left + "_" + croppedRect.top + "_" + croppedRect.right + "_" + croppedRect.bottom);
    }

    @SuppressWarnings("WeakerAccess")
    public static class BitmapLoadFailedException extends Exception {}
}
