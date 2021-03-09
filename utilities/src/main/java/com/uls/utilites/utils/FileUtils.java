package com.uls.utilites.utils;

import com.uls.utilites.un.Useless;

import java.io.File;

/**
 * Created by daiepngfei on 12/20/17
 */

@SuppressWarnings("WeakerAccess")
public class FileUtils {
    private static final String NO_FILE = "no_file_";

    public static String getFileNameWithUrl(String url) {
        if (!Useless.hasEmptyIn(url))
            return url.length() > url.lastIndexOf("/") + 1 ? url.substring(url.lastIndexOf("/") + 1) : null;
        else return null;
    }

    public static boolean isExsit(String path) {
        return new File(path).exists();
    }

    public static boolean deleteFile(File dirFile) {
        // 如果dir对应的文件不存在，则退出
        if (!dirFile.exists()) {
            return false;
        }

        if (dirFile.isFile()) {
            return dirFile.delete();
        } else {

            for (File file : dirFile.listFiles()) {
                deleteFile(file);
            }
        }

        return dirFile.delete();
    }

}
