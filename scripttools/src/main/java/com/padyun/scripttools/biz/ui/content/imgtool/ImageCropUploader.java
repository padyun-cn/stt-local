package com.padyun.scripttools.biz.ui.content.imgtool;

/**
 * Created by daiepngfei on 4/3/19
 */
public class ImageCropUploader {

    /*public static void saveAndUpload(String ip, String asip, SEImage image, CropInfo cropInfo, final String savedPath, CoreWorkers.IResult<Boolean> result) {
        saveAndUpload(ip, asip, image, new Rect(cropInfo.x, cropInfo.y, cropInfo.w, cropInfo.h), savedPath, result);
    }

    public static void saveAndUpload(String ip, String asip, SEImage image, android.graphics.Rect cropInfo, final String savedPath, CoreWorkers.IResult<Boolean> result) {
        saveAndUpload(ip, asip, image, new Rect(cropInfo.left, cropInfo.top, cropInfo.width(), cropInfo.height()), savedPath, result);
    }
*/
/*
    private static void saveAndUpload(String ip, String asip, SEImage image, Rect cropInfo, final String savedPath, CoreWorkers.IResult<Boolean> result) {
        new AsyncBool(() -> compressAndUploadSync(ip, asip, image, cropInfo, savedPath), r -> {
            if (result != null) {
                result.result(r);
            }
        }).go();
    }

    public static void saveAndUpload(String ip, String asip, Collection<SEImage> images, CoreWorkers.IResult<Boolean> result) {
        new AsyncBool(() -> {
            for (SEImage image : images) {
                android.graphics.Rect r = image.getBounds();
                if (!compressAndUploadSync(ip, asip, image, new Rect(r.left, r.top, r.width(), r.height()), image.getImageCropPath())) {
                    return false;
                }
            }
            return true;
        }, r -> {
            if (result != null) {
                result.result(r);
            }
        }).go();
    }
*/

 /*   private static boolean compressAndUploadSync(String ip, String asip, SEImage image, Rect cropInfo, String savedPath) {
        boolean result = false;
        try {
            Mat matSrc = Imgcodecs.imread(image.getImageOriginal(), Imgcodecs.CV_LOAD_IMAGE_COLOR);
            Mat crop = new Mat(matSrc, cropInfo);
            Imgcodecs.imwrite(savedPath, crop);
            StContext.network().applyTest().updateImageSync(image);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }*/

    /*@SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean compressAndUploadWithConfigedConvertor(Mat matToBeingSave, SEImage image) {
        try {
            if (image != null && !Useless.isEmpty(image.getImageCropPath()) && matToBeingSave != null && !matToBeingSave.empty()) {

                final File f = new File(image.getImageCropPath() + ".conv.png");
                if (f.exists()) {
                    f.delete();
                }
                Imgcodecs.imwrite(f.getAbsolutePath(), matToBeingSave);
                File fOri = new File(image.getImageCropPath());

                if (fOri.exists()) {
                    fOri.delete();
                }

                f.renameTo(fOri);
                StContext.network().applyTest().updateImageSync(image);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }*/

}
