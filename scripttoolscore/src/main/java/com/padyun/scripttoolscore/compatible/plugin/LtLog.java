package com.padyun.scripttoolscore.compatible.plugin;


import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by litao on 14-12-30.
 */
public class LtLog {

    public static final String TAG = "yp_fairy" ;
    private static String logFile;
    private static OutputStream out ;
    private static int maxSize ;
    private static int currentSize ;
    private static String logPrefix = "ypfairy-->";


    public static void setLogprefix(String prefix){
        logPrefix = prefix ;
    }
    public static String packFairyLog(String info){
        String time = new SimpleDateFormat("MM/dd HH:mm:ss").format(new Date()) ;
        String log = time +" "+logPrefix  + ":"+info ;
        return  log ;
    }
    public static void i(String info){
        String log = packFairyLog(info) ;
        Log.i(TAG,log);
    }

    public static void d(String info){
        String log = packFairyLog(info) ;
        Log.d(TAG,log);
    }
    public static void e(String info){
        String log = packFairyLog(info) ;
        Log.w(TAG,log);
    }
    public static void w(String info){
        String log = packFairyLog(info) ;
        Log.w(TAG,log);
    }
    public static void i(String tag, String info){
        i(tag+":"+info) ;
    }
    public static void e(String tag, String info){
        e(tag+":"+info) ;
    }
    public static void d(String tag, String info){
        d(tag+":"+info) ;
    }
    public static void w(String tag, String info){
        w(tag+":"+info) ;
    }

    static class Log{
        public static void w(String tag, String log){
            System.out.println(log) ;
        }
        public static void i(String tag, String log){
            System.out.println(log) ;
        }

        public static void d(String tag, String log){
            System.out.println(log) ;
        }
    }

}
