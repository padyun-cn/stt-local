package com.padyun.scripttoolscore.tools;

import android.util.Base64;

import java.nio.charset.Charset;
import java.util.Random;

/**
 * Created by daiepngfei on 2020-06-06
 */
public class SceCryptor {
    private static final int oh = 3;
    private static final int ot = 7;
    private static final int ol = 3;
    public static byte[] encrypt(String str){
        if(str == null || str.isEmpty()){
            return null;
        }

        // gen random inds
        final byte[] inds = new byte[ol];
        new Random().nextBytes(inds);
        // make sure random inds are non-zeros
        for(int i = 0; i < inds.length; i ++){
            inds[i] = inds[i] == 0 ? 1 : inds[i];
        }

        // get edc from inds
        final byte[] edc = getEdcOfInds(inds);

        // using inds and random bytes fill-up the-header
        final int headerLen = oh + ot + edc[0];
        /*System.out.println("headerLen : " + headerLen);
        System.out.println("inds: " + inds[0] + "," +  inds[1] + "," + inds[2]);
        System.out.println("edc: " + edc[0] + "," +  edc[1] + "," + edc[2]);*/
        final byte[] header = new byte[headerLen];
        final int ohPos = getOhPos();
        new Random().nextBytes(header);
        header[ohPos] = inds[0];
        header[ohPos + edc[0]] = inds[1];
        header[header.length - 1] = inds[ohPos];

        // copy header into result
        final byte[] result = new byte[headerLen + str.length()];
        System.arraycopy(header, 0, result, 0, header.length);

        // encrypion of string
        for(int i = header.length, j = 0; i < result.length && j < str.length(); i++,j++){
            final byte c = (byte) str.charAt(j);
            result[i] = (byte) (c ^ edc[j % edc.length]);
        }


        // base64 finanlly
        //noinspection UnnecessaryLocalVariable
        byte[] base64 =  Base64.encode(result, Base64.DEFAULT);
        return base64;
    }


    public static String decrypt(byte[] data){
        if(data == null || data.length < 12){
            throw new IllegalArgumentException();
        }

        // ffmpeg_decoder_decoding base64
        final byte[] base64 = android.util.Base64.decode(data, Base64.DEFAULT);
        /*final byte[] base64 = data;*/
        // get inds
        //final byte[] inds = new byte[ol];
        final byte[] edc = new byte[ol];
        final int ohPos = getOhPos();
        edc[0] = getEdcElement(base64[ohPos]);
        edc[1] = getEdcElement(base64[ohPos + edc[0]]);
        edc[2] = getEdcElement(base64[ohPos + edc[0] + ot]);
        /*inds[0] = base64[ohPos];
        inds[1] = base64[ohPos + edc[0]];
        inds[2] = base64[ohPos + edc[0] + ot];*/
        final int headerLen = oh + ot + edc[0];
        /*System.out.println("headerLen : " + headerLen);
        System.out.println("inds: " + inds[0] + "," +  inds[1] + "," + inds[2]);
        System.out.println("edc: " + edc[0] + "," +  edc[1] + "," + edc[2]);*/

        final byte[] str = new byte[base64.length - headerLen];
        for(int i = 0; i < str.length; i++){
            final byte tar = base64[headerLen + i];
            str[i] = (byte) (tar ^ (edc[i % edc.length]));
        }

        return new String(str, Charset.forName("utf-8"));
    }

    private static int getOhPos() {
        return oh - 1;
    }

    private static byte[] getEdcOfInds(byte[] inds) {
        final byte[] edc = new byte[inds.length];
        for(int i = 0; i < edc.length; i ++){
            edc[i] = getEdcElement(inds[i]);
        }
        return edc;
    }

    private static byte getEdcElement(byte ind) {
        byte temp = ind;
        if(temp < 0){
            temp &= 127;
            temp = temp == 0 ? 1 : temp;
        }
        return temp;
    }

    public static void main(String[] args) {
        testCase();
        /*if(args != null){
            System.out.println("参数个数：" + args.length);
            for (int i = 0; i < args.length; i++) {
                System.out.println("参数(" + i + "): " + args[i]);
            }

            File src = args.length > 0 ? new File(args[0]) : null;
            File tar = args.length > 1 ? new File(args[1]) : null;

            if(src != null && (src.exists() || src.length() >= Integer.MAX_VALUE - 1)){
                byte[] srcBytes = new byte[(int) src.length()];
                try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(src)) ) {
                    is.read(srcBytes);
                    final String de = decrypt(srcBytes);
                    if(de.length() > 0){
                        tar = tar == null || !tar.createNewFile() ? new File(src.getParent(), "decrypt of " + src.getName()) : tar;
                        try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tar))){
                            bos.write(de.getBytes());
                            System.out.println("解密完成！");
                        } catch (Exception e){
                            throw e;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }*/

    }

    private static void testCase() {
        byte a = -1;
        final String hi = "hi123567ojn";
        System.out.println(hi);
        byte[] data = encrypt(hi);
        System.out.println(hi);
        System.out.println("data.length : " + data.length + ", hi : " + hi);
        final String resultHi = decrypt(data);
        System.out.println("data.length : " + data.length + ", resultHi : " + resultHi);
    }
}
