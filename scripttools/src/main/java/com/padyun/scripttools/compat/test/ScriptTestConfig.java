package com.padyun.scripttools.compat.test;


import android.content.Context;
import android.content.SharedPreferences;

import com.padyun.scripttools.module.runtime.StContext;
import com.spring.network.socket.compat2.BSConfiguration;

/**
 * Created by daiepngfei on 1/24/19
 */
public class ScriptTestConfig {
    private static final String DEFAULT_SERVER_IP = "";
    private static final String DEFAULT_AS_IP = "";
    private static final String DEFAULT_VERIFY = "";
    private static String SERVER_IP = DEFAULT_SERVER_IP;
    private static String AS_IP = DEFAULT_AS_IP;
    public static final short TEST_SERVER_PORT = 12410;
    public static final short TEST_SCRIPT_PORT = 12100;
    private static String VERIFY = DEFAULT_VERIFY;

    public static class Image {

        public static final int SPECIFED_WIDTH_LAND = 1280;
        public static final int SPECIFED_HEIGHT_LAND = 720;

        public static final int SPECIFED_WIDTH_PORT = 720;
        public static final int SPECIFED_HEIGHT_PORT = 1280;
    }

    public static class Global {
        public static final int AUTO_SAVE_TIME_INTEVAL = 10 * 1000;
    }

    public static void setServerIp(String serverIp) {
        SERVER_IP = serverIp;
    }

    public static void setAsIp(String asIp) {
        AS_IP = asIp;
    }

    public static void setVerify(String verify) {
        ScriptTestConfig.VERIFY = verify;
    }

    public static String getServerIp() {
        //
        // return SERVER_IP;
        return StContext.getInstance().getManifest().getDeviceIp();
    }

    public static String getAsIp() {
        // return AS_IP;
        return StContext.getInstance().getManifest().getDeviceAsIp();
    }

    public static int getAsPort() {
        //return PortRule.ipToPort(AS_IP);
        return StContext.getInstance().getManifest().getDevicePort();
    }

    public static String getDefaultVerify() {
        // return VERIFY;
        return StContext.getInstance().getManifest().getDeviceVerify();
    }

  /*  public static ConfigModule genDefaultConfigModule() {
        return new ConfigModule(25, 2000000, 1280, 720);
    }

    public static int getAudionConfigType() {
        return EStreamAudioConfig.LC480_2.getNumber();
    }*/


    public static BSConfiguration genDefaultBSConnectConfiguration(){
        return new BSConfiguration(getServerIp(), getAsPort(), true);
    }

    public static void init(Context activity) {
        if (activity != null) {
            SharedPreferences sp = activity.getSharedPreferences("Settings", Context.MODE_PRIVATE);
            SERVER_IP = sp.getString("CONIFIG_SERVER_IP", ScriptTestConfig.DEFAULT_SERVER_IP);
            AS_IP = sp.getString("CONIFIG_AS_IP", ScriptTestConfig.DEFAULT_AS_IP);
        }
    }


}
