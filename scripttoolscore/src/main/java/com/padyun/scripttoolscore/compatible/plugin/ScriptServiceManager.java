package com.padyun.scripttoolscore.compatible.plugin;


import android.os.AsyncTask;

import com.padyun.scripttoolscore.compatible.plugin.network.ClientNio;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeoutException;

/**
 * Created by litao on 2018/7/5.
 */
public class ScriptServiceManager {

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

    private static ClientNio connectToas(String server, String asip) {
        return connectToas(server, asip, TIMEOUT);
    }

    private static ClientNio connectToas(String server, String asip, int timeout) {
        ClientNio clientNio = new ClientNio(timeout);
        if (clientNio.connect(server, SERVER_PORT)) {
            try {
                System.out.println("kkkk - Con");
                sendIp(clientNio, asip);
                sendPort(clientNio, SCRIPT_PORT);
                return clientNio;
            } catch (TimeoutException e) {
                System.out.println("kkkk - Con faile timeout");
                e.printStackTrace();
                clientNio.disconnect();
                clientNio = null;
            }
        } else {
            System.out.println("kkkk - Con failed");
            clientNio.disconnect();
            clientNio = null;
        }
        return clientNio;
    }


    private static ClientNio clientNio;

    public static void setRuntimeInterval(String serverIp, String asip, int time, Runnable runnable) {
        new AsyncTask<Object, Object, Boolean>(){

            @Override
            protected Boolean doInBackground(Object... objects) {
                if (clientNio != null) {
                    clientNio.disconnect();
                }
                clientNio = connectToas(serverIp, asip);
                if (clientNio != null) {
                    StringModule module = new StringModule(FairyProtocol.TYPE_RUNTIME_INTERVAL);
                    module.str = String.valueOf(time);
                    try {
                        clientNio.send(module.toDataWithLen());
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                        return false;
                    } finally {
                        clientNio.disconnect();
                    }
                    return true;
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean o) {
                if (runnable != null) runnable.run();
            }
        }.execute(new Object());

    }

}

