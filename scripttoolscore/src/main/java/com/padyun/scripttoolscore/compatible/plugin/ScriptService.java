package com.padyun.scripttoolscore.compatible.plugin;


import android.util.Log;

import com.padyun.scripttoolscore.compatible.plugin.network.ClientNio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeoutException;

/**
 * Created by litao on 2018/7/5.
 */
public class ScriptService {

    public static final short SERVER_PORT = 12410;
    public static final short SCRIPT_PORT = 12100;
    public static final int TIMEOUT = 5000;

    public static byte[] ipToBytesByReg(String ipAddr) {
        byte[] ret = new byte[4];
        try {
            String[] ipArr = ipAddr.split("\\.");
            ret[0] = (byte) (Integer.parseInt(ipArr[0]) & 0xFF);
            ret[1] = (byte) (Integer.parseInt(ipArr[1]) & 0xFF);
            ret[2] = (byte) (Integer.parseInt(ipArr[2]) & 0xFF);
            ret[3] = (byte) (Integer.parseInt(ipArr[3]) & 0xFF);
            return ret;
        } catch (Exception e) {
            throw new IllegalArgumentException(ipAddr + " is invalid IP");
        }
    }

    private static void sendIp(ClientNio clientNio, String asip) throws TimeoutException {
        if (asip != null) {
            byte[] ip = ipToBytesByReg(asip);
            ByteBuffer byteBuffer = ByteBuffer.allocate(4);
            byteBuffer.put(ip);
            clientNio.send(byteBuffer);
        }
    }

    private static void sendPort(ClientNio clientNio, short port) throws TimeoutException {

        ByteBuffer byteBuffer = ByteBuffer.allocate(2);
        byteBuffer.putShort(port);

        clientNio.send(byteBuffer);

    }




    //更新单张图片
    public static boolean updateImage(String server, String asip, String file, ImageModule imageModule) {
        boolean result = false;
        if (imageModule != null) {
            ClientNio clientNio = new ClientNio(5000);
            if (clientNio.connect(server, SERVER_PORT)) {
                try {
                    sendIp(clientNio, asip);
                    sendPort(clientNio, SCRIPT_PORT);
            /*ImageModule imageModule = new ImageModule();
            Properties properties = new Properties();*/

                /*properties.load(new FileInputStream(basePath + File.separator + file + ".info"));
                imageModule.x = Integer.parseInt(properties.getProperty("x")) ;
                imageModule.y = Integer.parseInt(properties.getProperty("y")) ;
                imageModule.width = Integer.parseInt(properties.getProperty("width")) ;
                imageModule.height = Integer.parseInt(properties.getProperty("height")) ;
                imageModule.flag = Integer.parseInt(properties.getProperty("flag")) ;
                imageModule.thresh = Integer.parseInt(properties.getProperty("thresh")) ;
                imageModule.maxval = Integer.parseInt(properties.getProperty("maxval")) ;
                imageModule.type = Integer.parseInt(properties.getProperty("type")) ;
                String sim = properties.getProperty("sim");
                if(sim != null) {
                    imageModule.sim = (int) (Float.parseFloat(properties.getProperty("sim")) * 100);
                }
                imageModule.name = file;*/
                    File f = new File(file);
                    int filelen = (int) f.length();
                    LtLog.i("file len:" + filelen + "file:" + f.getAbsolutePath());
                    FileInputStream fileInputStream = null;
                    fileInputStream = new FileInputStream(f);
                    byte b[] = new byte[filelen];
                    fileInputStream.read(b);
                    fileInputStream.close();
                    imageModule.pic = ByteBuffer.wrap(b);

                    final int resultCode = clientNio.send(imageModule.toDataWithLen());
                    clientNio.disconnect();
                    System.out.println("呜呜 " + resultCode);
                    if(resultCode != -1) {
                        result = true;
                    }
                } catch (IOException | TimeoutException e) {
                    Log.e("ScriptService", "updateImage => error ", e);
                    e.printStackTrace();
                }
            }
        } else {
            Log.w("ScriptService", "updateImage => error imageModule(null)" );
        }
        return result;
    }




}

