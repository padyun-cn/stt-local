package com.padyun.scripttools.compat;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.padyun.scripttoolscore.compatible.plugin.FairyProtocol;
import com.padyun.scripttoolscore.compatible.plugin.ImageModule;
import com.padyun.scripttoolscore.compatible.plugin.ScreencapModule;
import com.padyun.scripttoolscore.compatible.plugin.TypeModule;
import com.padyun.scripttoolscore.compatible.plugin.Utils;
import com.padyun.scripttoolscore.compatible.plugin.YpModule2;
import com.padyun.scripttoolscore.compatible.plugin.network.ClientNio;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeoutException;


/**
 * Created by litao on 2018/7/5.
 */
public class CompatScriptService {

    public static final short SERVER_PORT = 12410;
    public static final short SCRIPT_PORT = 12100;
    public static final int TIMEOUT = 5000;

    public static String TEST_IP = "127.0.0.0" ;



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

    public static void sendIp(ClientNio clientNio, String asip) throws TimeoutException {
        if (asip != null) {
            byte[] ip = ipToBytesByReg(asip);
            ByteBuffer byteBuffer = ByteBuffer.allocate(4);
            byteBuffer.put(ip);
            clientNio.send(byteBuffer);
        }
    }

    public static void sendPort(ClientNio clientNio, short port) throws TimeoutException {

        ByteBuffer byteBuffer = ByteBuffer.allocate(2);
        byteBuffer.putShort(port);

        clientNio.send(byteBuffer);

    }

    public static ClientNio connectToas(String server, String asip) {
        return connectToas(server, asip, TIMEOUT);
    }

    public static ClientNio connectToas(String server, String asip, int timeout) {
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


    //测试请求截图
    public static boolean requestImage(String server, String asip, String file) {

        ClientNio clientNio = new ClientNio(5000);

        if (clientNio.connect(server, SERVER_PORT)) {

            try {
                sendIp(clientNio, asip);
                sendPort(clientNio, SCRIPT_PORT);
                long start = System.currentTimeMillis();
                clientNio.send(new TypeModule(FairyProtocol.TYPE_REQUEST_IMAGE).toDataWithLen());

                ByteBuffer byteBuffer = clientNio.readPackage();
                byte[] data = byteBuffer.array();
                short type = Utils.bytesToShort(data, 0);
                ScreencapModule screencapModule = new ScreencapModule(data, 2, data.length - 2);

                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(screencapModule.pic.array());
                fileOutputStream.close();
                return true;
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (YpModule2.YpModuleException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                clientNio.disconnect();
            }

        }
        return false;
    }

    //测试请求截图
    public static Bitmap requestImageBitmap(String server/*, String asip, String file*/) {

        ClientNio clientNio = new ClientNio(50000);

        if (clientNio.connect(server, /*SERVER_PORT*/ 12100)) {

            try {
//                sendIp(clientNio, asip);
//                sendPort(clientNio, SCRIPT_PORT);
                long start = System.currentTimeMillis();
                clientNio.send(new TypeModule(FairyProtocol.TYPE_REQUEST_IMAGE).toDataWithLen());

                ByteBuffer byteBuffer = clientNio.readPackage();
                byte[] data = byteBuffer.array();
                short type = Utils.bytesToShort(data, 0);
                ScreencapModule screencapModule = new ScreencapModule(data, 2, data.length - 2);
                byte[] arr = screencapModule.pic.array();
                return BitmapFactory.decodeByteArray(arr, 0, arr.length);

            } catch (TimeoutException | YpModule2.YpModuleException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                clientNio.disconnect();
            }

        }
        return null;
    }

    //更新单张图片
    public static boolean updateImage(String server, String asip, ImageModule imageModule) {
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
                imageModule.name = file;
                    File f = new File(file);
                    int filelen = (int) f.length();
                    LtLog.i("file len:" + filelen + "file:" + f.getAbsolutePath());
                    FileInputStream fileInputStream = null;
                    fileInputStream = new FileInputStream(f);
                    byte b[] = new byte[filelen];
                    fileInputStream.read(b);
                    fileInputStream.close();
                    imageModule.pic = ByteBuffer.wrap(b);
*/
                    clientNio.send(imageModule.toDataWithLen());
                    clientNio.disconnect();
                    result = true;
                } catch ( TimeoutException e) {
                    Log.e("ScriptService", "updateImage => error ", e);
                    e.printStackTrace();
                }
            }
        } else {
            Log.w("ScriptService", "updateImage => error imageModule(null)" );
        }
        return result;
    }

//    public static void updatePlugin(String server, String asip){
//        PluginInfo pluginInfo = ScriptEditor.getInstance().getPluginInfo() ;
//        PluginModule pluginModule = new PluginModule() ;
//        pluginModule.packageName = pluginInfo.packageName ;
//        FileInputStream fileInputStream = null;
//        try {
//            fileInputStream = new FileInputStream(pluginInfo.apkPath);
//            byte b[] = new byte[fileInputStream.available()];
//            fileInputStream.read(b);
//            fileInputStream.close();
//            pluginModule.data = ByteBuffer.wrap(b);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        ClientNio clientNio = new ClientNio(5000);
//        if (clientNio.connect(server, SERVER_PORT)) {
//            try {
//                    sendIp(clientNio, asip);
//                    sendPort(clientNio, SCRIPT_PORT);
//                    clientNio.send(pluginModule.toDataWithLen());
//                    clientNio.disconnect();
//                Log.e("ScriptService", "updatePlugin => success");
//            } catch ( TimeoutException e) {
//                    Log.e("ScriptService", "updatePlugin => error ", e);
//                    e.printStackTrace();
//            }
//        }
//    }

//    public static boolean syncPlugin(String server, String asip){
//        if(ScriptEditor.getInstance().getPluginInfo() == null){
//            return false;
//        }
//        StringModule stringModule = new StringModule(FairyProtocol.TYPE_PLUGIN_INFO) ;
//        stringModule.str = PluginInfo.toJson(ScriptEditor.getInstance().getPluginInfo()).toString() ;
//        ClientNio clientNio = new ClientNio(5000);
//        if (clientNio.connect(server, SERVER_PORT)) {
//            try {
//                sendIp(clientNio, asip);
//                sendPort(clientNio, SCRIPT_PORT);
//                clientNio.send(stringModule.toDataWithLen());
//
//                ByteBuffer byteBuffer = clientNio.readPackage();
//
//                byte[] data = byteBuffer.array();
//                short type = Utils.bytesToShort(data, 0);
//                StringModule stringModule1 = new StringModule(data, 2, data.length - 2);
//                PluginInfoResponse response = new PluginInfoResponse();
//                response.fromJson(new JSONObject(stringModule1.str));
//                if(response.needUpdate){
//                    updatePlugin(server, asip);
//                    return true ;
//                }else{
//                    return false ;
//                }
//
//            } catch (TimeoutException e) {
//                e.printStackTrace();
//            } catch (YpModule2.YpModuleException e) {
//                e.printStackTrace();
//            }catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            } finally {
//                clientNio.disconnect();
//            }
//
//        }
//        return false ;
//    }


    //同步图片信息
//    public static void syncImage(String server, String asip) {
//        StringModule stringModule = new StringModule(FairyProtocol.TYPE_IMAGELIST);
//        stringModule.str = ScriptEditor.getInstance().allImageMd5();
//        ClientNio clientNio = new ClientNio(5000);
//        if (clientNio.connect(server, SERVER_PORT)) {
//            try {
//                sendIp(clientNio, asip);
//                sendPort(clientNio, SCRIPT_PORT);
//                clientNio.send(stringModule.toDataWithLen());
//
//                ByteBuffer byteBuffer = clientNio.readPackage();
//
//                byte[] data = byteBuffer.array();
//                short type = Utils.bytesToShort(data, 0);
//                StringModule stringModule1 = new StringModule(data, 2, data.length - 2);
//
//                BufferedReader reader = new BufferedReader(new StringReader(stringModule1.str));
//                while (true) {
//                    String line = reader.readLine();
//                    if (line == null) {
//                        break;
//                    }
//                    System.out.println(line);
//                    ImageInfo imageInfo = ScriptEditor.getInstance().getImageInfo2(line) ;
//                    ImageModule imageModule = new ImageModule(imageInfo) ;
//
//                    updateImage(server, asip, imageModule);
//                }
//                reader.close();
//
//            } catch (TimeoutException e) {
//                e.printStackTrace();
//            } catch (YpModule2.YpModuleException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } finally {
//                clientNio.disconnect();
//            }
//
//        }
//    }

    private static ClientNio clientNio, longConnection;
    //    private static Thread t = null;
    private static Thread t;

//    private static void longConnThreadStart(String server, String as, String scriptInfo, ScriptEvent event) {
//        stopLongConnThread();
//        t = new Thread(() -> {
//            if (longConnection != null) {
//                longConnection.disconnect();
//            }
//            longConnection = connectToas(server, as, -1);
//            if (longConnection != null) {
//                StringModule stringModule = new StringModule(FairyProtocol.TYPE_START);
//                stringModule.str = scriptInfo;
//                try {
//                    longConnection.send(stringModule.toDataWithLen());
//                    event.onConnectted();
//                    while (t != null && !t.isInterrupted()) {
//                        System.out.println("sssss - tid -> ");
//                        ByteBuffer byteBuffer = longConnection.readPackage();
//                        if (byteBuffer == null) {
//                            Thread.sleep(100);
//                            continue;
//                        }
//                        byte[] data = byteBuffer.array();
//                        short type = Utils.bytesToShort(data, 0);
//                        StringModule runtimeModule = new StringModule(data, 2, data.length - 2);
//                        RuntimeInfo runtimeInfo = new RuntimeInfo(runtimeModule.str);
//                        event.onRuntimInfo(runtimeInfo);
//                    }
//                } catch (TimeoutException | YpModule2.YpModuleException | JSONException | InterruptedException e) {
//                    e.printStackTrace();
//                } finally {
//                    longConnection.disconnect();
//                    event.onDisconnect();
//                    System.out.println("sssss - tid -> END ");
//                }
//            } else {
//                event.onDisconnect();
//            }
//        });
//        t.start();
//    }

    private static void stopLongConnThread() {
        if (t != null && t.isAlive()) {
            t.interrupt();
        }
    }
    public static void scriptDisconnect(){
        stopLongConnThread();
    }

//    public static StateInfo scriptState(String server, String as){
//        TypeModule typeModule = new TypeModule(FairyProtocol.TYPE_REQUEST_STATE) ;
//        ClientNio clientNio = new ClientNio(5000);
//        StateInfo stateInfo = null ;
//        if (clientNio.connect(server, SERVER_PORT)) {
//            try {
//                sendIp(clientNio, as);
//                sendPort(clientNio, SCRIPT_PORT);
//                clientNio.send(typeModule.toDataWithLen());
//                ByteBuffer byteBuffer = clientNio.readPackage() ;
//                StringModule stringModule = new StringModule(byteBuffer.array(), 2, byteBuffer.position()-2) ;
//                stateInfo = StateInfo.fromJson(stringModule.str) ;
//            }catch (Exception e){
//                e.printStackTrace();
//            }finally {
//                clientNio.disconnect();
//            }
//        }
//        return stateInfo ;
//    }

//    public static void scriptStart(String server, String as, String scriptInfo) {
//        StringModule stringModule = new StringModule(FairyProtocol.TYPE_START);
//        stringModule.str = scriptInfo;
//        ClientNio clientNio = new ClientNio(5000);
//        if (clientNio.connect(server, SERVER_PORT)) {
//            try {
//                sendIp(clientNio, as);
//                sendPort(clientNio, SCRIPT_PORT);
//                clientNio.send(stringModule.toDataWithLen());
//            }catch (Exception e){
//                e.printStackTrace();
//            }finally {
//                clientNio.disconnect();
//            }
//        }
//    }
    public static void scriptStop(String server, String as) {
        //longConnThreadStart(server, as, scriptInfo, event);
        TypeModule typeModule = new TypeModule(FairyProtocol.TYPE_STOP);
        ClientNio clientNio = new ClientNio(5000);
        if (clientNio.connect(server, SERVER_PORT)) {
            try {
                sendIp(clientNio, as);
                sendPort(clientNio, SCRIPT_PORT);
                clientNio.send(typeModule.toDataWithLen());

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                clientNio.disconnect();
            }
        }
    }

    private static Thread resumeT;



//    public static interface ScriptEvent {
//        void onConnectted();
//
//        public void onRuntimInfo(RuntimeInfo info);
//
//        public void onDisconnect();
//
//        public void onScriptState(StateInfo stateInfo) ;
//    }


}

