package com.padyun.scripttools.module.runtime;

import android.graphics.Bitmap;

import com.padyun.scripttools.biz.ui.content.Constants;
import com.uls.utilites.content.CoreWorkers;
import com.padyun.scripttoolscore.compatible.data.model.SEImage;
import com.uls.utilites.io.Files;
import com.uls.utilites.un.Useless;
import com.uls.utilites.utils.Md5Util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import androidx.annotation.WorkerThread;
import androidx.core.util.Consumer;

/**
 * Created by daiepngfei on 2020-05-20
 */
public class AssetsManager {
    private StContext context;
    private static final String RAW = Constants.Folder.SCREEN;
    private static final String PROCESSED = Constants.Folder.CROP;
    private static final String PARENT = Constants.Folder.IMAGE_CACHE;

    AssetsManager(StContext context) {
        this.context = context;
    }

    public File getImageProcessedByName(String name) {
        return getImageWithTypeAndName(PROCESSED, name);
    }

    public File getImageRawByName(String name) {
        return getImageWithTypeAndName(RAW, name);
    }

    public File currentDirRaw() {
        return getDirWithType(RAW);
    }

    public File currentDirProcessed() {
        return getDirWithType(PROCESSED);
    }

    public File newProccessed() {
        return new File(getDirWithType(PROCESSED), UUID.randomUUID().toString() + ".png");
    }

    private File getImageWithTypeAndName(String imageType, String name) {
        return Useless.noEmptyStr(name) ? new File(getDirWithType(imageType), name) : null;
    }

    @NotNull
    private File getDirWithType(String imageType) {
        return new File(context.getCurrentWorkingDir() + File.separator + PARENT + File.separator + imageType);
    }

    @WorkerThread
    public void storeRawOnWorkers(Bitmap bitmap, CoreWorkers.Then<File> then) {
        CoreWorkers.on(() -> compressBitmapToFile(getDirWithType(RAW).getAbsolutePath(), bitmap))
                .then(then);
    }

    /**
     *
     * @param raw
     * @param cropX
     * @param cropY
     * @param cropWidth
     * @param cropHeight
     * @param then
     */
    public void processOnWorkers(File raw, int cropX, int cropY, int cropWidth, int cropHeight, CoreWorkers.Then<File> then) {
        CoreWorkers
                .on(() -> process(raw, cropX, cropY, cropWidth, cropHeight))
                .then(then);
    }

    /**
     *
     * @param raw
     * @param cropX
     * @param cropY
     * @param cropWidth
     * @param cropHeight
     * @return
     */
    public File process(File raw, int cropX, int cropY, int cropWidth, int cropHeight) {
        try {
            Mat matSrc = Imgcodecs.imread(raw.getAbsolutePath(), Imgcodecs.CV_LOAD_IMAGE_COLOR);
            Mat crop = new Mat(matSrc, new Rect(cropX, cropY, cropWidth, cropHeight));
            File f = newTempProcessingFile();
            Imgcodecs.imwrite(f.getAbsolutePath(), crop);
            return renameWithCalculatedMd5(f, currentDirProcessed(), Files.Ext.PNG);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File renameWithCalculatedMd5(File f, File dir, String ext) {
        if(f != null && f.exists()){
            String md5Name = Md5Util.file(f);
            dir.mkdirs();
            if(!Useless.isEmpty(md5Name)){
                if(!Useless.isEmpty(ext)){
                    md5Name = md5Name + ext;
                }
                final File destFile = new File(dir, md5Name);
                if(f.renameTo(destFile)){
                    return destFile;
                }
            }
        }
        return null;
    }


    /**
     *
     * @param convertedMat
     * @return
     */
    public File process(Mat convertedMat) {
        try {
            if (convertedMat == null || convertedMat.empty()) {
                return null;
            }
            final File f = newTempProcessingFile();
            if (f.exists()) {
                //noinspection ResultOfMethodCallIgnored
                f.delete();
            }
            Imgcodecs.imwrite(f.getAbsolutePath(), convertedMat);
            return renameWithCalculatedMd5(f, currentDirProcessed(), Files.Ext.PNG);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void loadRaw(SEImage image, Consumer<Bitmap> loader) {

        // load
        if (image == null || loader == null || image.getImageOriginal() != null) {
            loader.accept(null);
            return;
        }


    }

    @Nullable
    private File compressBitmapToFile(String dir, Bitmap bitmap) {
        File destFile = null;
        if (bitmap != null && bitmap.getWidth() > 0 && bitmap.getHeight() > 0) {
            // httpGet a temp file for storing the bitmap
            final String tempName = UUID.randomUUID().toString();
            final File tempFile = new File(dir, tempName);
            try(FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                // compress the bitmap
                if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
                    // httpGet the MD5 value of the temp image file
                    destFile = renameWithCalculatedMd5(tempFile, new File(dir), Files.Ext.PNG);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return destFile;
    }


    @NotNull
    private File newTempProcessingFile() {
        return new File(getDirWithType(PROCESSED), UUID.randomUUID().toString() + ".processing.png");
    }
}
