package com.uls.utilites.content;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by daiepngfei on 12/5/17
 */
public class YpBitmapCacher {

    private LruCache<String, Bitmap> mBitmapLruCache;

    public static YpBitmapCacher ins(){
        return Factory.sInstance;
    }

    private static class Factory {
        private static final YpBitmapCacher sInstance = new YpBitmapCacher();
    }

    private YpBitmapCacher(){
        int maxMemory = (int)(Runtime.getRuntime().maxMemory() / 1024);// kB
        int cacheSize = maxMemory / 8;
        mBitmapLruCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight() / 1024;
            }
        };
    }

    public void invalidate(String key){
        mBitmapLruCache.remove(key);
    }

    public void put(String key, Bitmap bitmap){
        mBitmapLruCache.put(key, bitmap);
    }

    public Bitmap get(String key){
        return mBitmapLruCache.get(key);
    }

}
