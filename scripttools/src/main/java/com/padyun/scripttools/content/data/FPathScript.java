package com.padyun.scripttools.content.data;

import android.os.Environment;

import com.mon.ui.app.AppDelegate;
import com.padyun.scripttools.module.runtime.ScriptManager;
import com.uls.utilites.un.Useless;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * Created by daiepngfei on 11/20/18
 */
public class FPathScript {

    private static /*final*/ String ROOT_DIR_SD;
    private static /*final*/ String ROOT_DIR_APP;
    private static /*final*/ String ROOT_DIR;
    private static /*final*/ String IMG_CACHE;
    private static /*final*/ String IMG_CACHE_SCREEN;
    private static /*final*/ String IMG_CACHE_TEMP;
    private static /*final*/ String IMG_CACHE_CROP;
    private static /*final*/ String SCRIPT_DATA;
    private static /*final*/ String DOWNLOAD;
    private static /*final*/ String UPGRADE;
    private static /*final*/ String WEB_CACHE;

    private static String USER_SPACE = "NO_USER";
    private static String GAME_SPACE = "NO_GAME";
    public static String DEXT_OLD = ".sce";
    public static String DEXT = ScriptManager.stFileExt();

    static void initWithUserAndGameIds(String userId, String gameId) {
        if (Useless.hasEmptyIn(userId, gameId) || Useless.equals(userId, USER_SPACE) && Useless.equals(GAME_SPACE, gameId)) {
            return;
        }
        USER_SPACE = userId;
        GAME_SPACE = gameId;
        File rootFile = AppDelegate.app().getExternalCacheDir();
        ROOT_DIR_SD = getScriptAppCompatSDCardRootDirectory();
        if (rootFile != null) ROOT_DIR_APP = rootFile.getAbsolutePath();
        else ROOT_DIR_APP = ROOT_DIR_SD;
        System.out.println("dest is FPATH " + ROOT_DIR_APP + "=====rootFile : " + (rootFile == null ? "NULL" : rootFile.getAbsolutePath()));
        switchToAppPath();
        switchToSDPath();
    }

    @NotNull
    public static String getScriptAppCompatSDCardRootDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/YPEditor/cache";
    }

    public static File newScriptFile(String path, String name){
        return new File(path, name + DEXT);
    }

    public static void switchToSDPath() {
        initPathes(ROOT_DIR_SD);
    }

    public static void switchToAppPath() {
        initPathes(ROOT_DIR_APP);
    }

    public static File getAppFile() {
        return new File(ROOT_DIR_APP);
    }

    public static File getSDFile() {
        return new File(ROOT_DIR_SD);
    }

    public static String getAppDir() {
        return ROOT_DIR_APP;
    }

    public static String getSDDir() {
        return ROOT_DIR_SD;
    }

    private static void initPathes(String root) {
        ROOT_DIR = root + "/plugins/" + "script" + "/" + USER_SPACE + "/" + GAME_SPACE;
        IMG_CACHE = ROOT_DIR + "/image_cache";
        IMG_CACHE_SCREEN = IMG_CACHE + "/screen";
        IMG_CACHE_CROP = IMG_CACHE + "/crop";
        IMG_CACHE_TEMP = IMG_CACHE + "/temp";
        DOWNLOAD = ROOT_DIR + "/download";
        UPGRADE = ROOT_DIR + "/upgrade";
        WEB_CACHE = ROOT_DIR + "/web_cache";
        SCRIPT_DATA = ROOT_DIR + "/script_data";
        mkdirIfNeedy(IMG_CACHE);
        mkdirIfNeedy(IMG_CACHE_SCREEN);
        mkdirIfNeedy(IMG_CACHE_CROP);
        mkdirIfNeedy(IMG_CACHE_TEMP);
        mkdirIfNeedy(SCRIPT_DATA);
        mkdirIfNeedy(WEB_CACHE);
        mkdirIfNeedy(DOWNLOAD);
        mkdirIfNeedy(UPGRADE);
    }

    public static String getRootDir() {
        return ROOT_DIR;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void mkdirIfNeedy(String dir) {
        File cache = new File(dir);
        if (!cache.exists()) {
            cache.mkdirs();
        } else if (!cache.isDirectory()) {
            cache.delete();
            cache.mkdirs();
        }

        File nomedia = new File(dir, ".nomedia");
        if(!nomedia.exists()){
            try {
                nomedia.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String downloadDir() {
        return DOWNLOAD;
    }

    public static String upgradeDir() {
        return UPGRADE;
    }

    public static String imageCacheDir() {
        return IMG_CACHE;
    }

    public static String imageCachePath(String name) {
        return IMG_CACHE + File.separator + name;
    }

    public static File imageCacheFile(String name) {
        return new File(IMG_CACHE, name);
    }

    public static String screenDir() {
        return IMG_CACHE_SCREEN;
    }

    public static String screenPath(String name) {
        return IMG_CACHE_SCREEN + File.separator + name;
    }

    public static File screenFile(String name) {
        return new File(IMG_CACHE_SCREEN, name);
    }

    public static File tmpFile(String name) {
        return new File(IMG_CACHE_TEMP, name);
    }

    public static String tmpFilePath(String name) {
        return new File(IMG_CACHE_TEMP, name).getAbsolutePath();
    }

    public static String cropDir() {
        return IMG_CACHE_CROP;
    }

    public static String sd_cropDir() {
        return IMG_CACHE_CROP;
    }

    public static String cropPath(String name) {
        return IMG_CACHE_CROP + File.separator + name;
    }

    public static File cropFile(String name) {
        return new File(IMG_CACHE_CROP, name);
    }

    public static String scriptDataDir() {
        return SCRIPT_DATA;
    }

    public static String scriptDataPath(String name) {
        return SCRIPT_DATA + File.separator + name;
    }

    public static File scriptDataFile(String name) {
        return new File(SCRIPT_DATA, name + DEXT);
    }

    public static String webCacheDir() {
        return WEB_CACHE;
    }

    public static File webCacheFile(String name) {
        return new File(WEB_CACHE, name);
    }

    public static boolean webCacheExist(String name) {
        return webCacheFile(name).exists();
    }

    public static File upgradeFile(String name) {
        return new File(UPGRADE, name);
    }

}
