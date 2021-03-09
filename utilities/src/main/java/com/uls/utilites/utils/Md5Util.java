package com.uls.utilites.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by daiepngfei on 2/25/19
 */
public class Md5Util {

    public static String string(String s) {
        MessageDigest MD5 = null;
        try {
            MD5 = MessageDigest.getInstance("MD5");
            byte[] bytes = s.getBytes();
            MD5.update(bytes, 0 , bytes.length);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return MD5 == null ? null : bytesToHexString(MD5.digest());
    }

    public static String file(File f){
        try {
            return file(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String file(InputStream inputStream){
        try {
            MessageDigest MD5 = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024 * 8] ;
            //inputStream.skip(offset) ;
            int readLen  ;
            while( (readLen = inputStream.read(buffer)) > 0){
                MD5.update(buffer, 0 , readLen);
            }
            return bytesToHexString(MD5.digest());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
