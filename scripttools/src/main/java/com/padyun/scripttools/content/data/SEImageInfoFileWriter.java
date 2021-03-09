package com.padyun.scripttools.content.data;

import com.padyun.scripttoolscore.compatible.data.model.ImageInfo;
import com.padyun.scripttoolscore.compatible.data.model.SEImage;
import com.uls.utilites.io.Files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Created by daiepngfei on 2021-02-20
 */
public class SEImageInfoFileWriter {

    private static final String KEY_FLAG = "flag" ;
    private static final String KEY_THRESH = "thresh" ;
    private static final String KEY_MAXVAL = "maxval" ;
    private static final String KEY_TYPE = "type" ;
    private static final String KEY_X = "x" ;
    private static final String KEY_Y = "y" ;
    private static final String KEY_WIDTH = "width" ;
    private static final String KEY_HEIGHT = "height" ;
    private static final String KEY_SIM = "sim" ;

    private static boolean writeTemplateInfo(ImageInfo info, OutputStream out){
        Properties properties = new Properties() ;
        properties.setProperty(KEY_FLAG, String.valueOf(info.flag)) ;
        properties.setProperty(KEY_HEIGHT, String.valueOf(info.h)) ;
        properties.setProperty(KEY_WIDTH, String.valueOf(info.w)) ;
        properties.setProperty(KEY_MAXVAL, String.valueOf(info.maxval)) ;
        properties.setProperty(KEY_THRESH, String.valueOf(info.threshold)) ;
        properties.setProperty(KEY_X, String.valueOf(info.x)) ;
        properties.setProperty(KEY_Y, String.valueOf(info.y)) ;
        properties.setProperty(KEY_TYPE, String.valueOf(info.type)) ;
        properties.setProperty(KEY_SIM, String.valueOf(info.sim / 100.F)) ;
        try {
            properties.store(out, null);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public static boolean writeToFile(SEImage seImage){
        if(seImage != null && seImage.getImageInfo() != null){
            final File infoFile = new File(Files.dir(seImage.getImageCropPath()).getAbsolutePath(), seImage.getCropFileName() + ".info");
            try {
                return writeTemplateInfo(seImage.getImageInfo(), new FileOutputStream(infoFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
