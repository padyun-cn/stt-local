package com.uls.utilites.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

/**
 * Created by litao on 2015/6/16.
 */
public class BitUtils {


    public static int bytesToInt(byte[] b, int offset) {
        int value= 0;
        for (int i = 0; i < 4; i++) {
            int shift= (4 - 1 - i) * 8;
            value +=(b[i + offset] & 0x000000FF) << shift;//往高位游
        }
        return value;
    }
    public static short bytesToShort(byte[] bytes,int start){
        if( start+2 > bytes.length){
            return -1 ;
        }
        short s = 0 ;
        s |= bytes[start] << 8 ;
        s |= bytes[start+1] ;
        return s ;
    }
    public static String bytesToString(byte[] bytes,int start,int count){
        try {
            return new String(bytes,start,count,"UTF-8") ;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null ;
    }
    public static void sendTo(String ip, int port, byte[] msg){
        try {
            Socket socket = new Socket(ip, port) ;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
