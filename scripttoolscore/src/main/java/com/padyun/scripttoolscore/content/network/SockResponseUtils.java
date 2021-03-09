package com.padyun.scripttoolscore.content.network;

import android.os.Handler;
import android.os.Looper;

import com.padyun.scripttoolscore.compatible.plugin.FairyProtocol;
import com.padyun.scripttoolscore.compatible.plugin.Utils;
import com.spring.network.socket.compat2.CoReader;
import com.spring.network.socket.compat2.ISockResponse;
import com.spring.network.socket.compat2.ISockSendResponse;
import com.uls.utilites.utils.BitUtils;
import com.uls.utilites.utils.LogUtil;

/**
 * Created by daiepngfei on 2020-06-15
 */
public class SockResponseUtils {
    private static final String TAG = "SockResponseUtils#";
    /**
     *
     * @param respData
     * @return
     */
    public static boolean isSimpleRespOk(final byte[] respData){
        if(respData == null || respData.length == 0){
            LogUtil.e(TAG, "respData is NUL");
            return false;
        }

        int index = 0;
        final short type = Utils.bytesToShort(respData, index);
        if(type != FairyProtocol.RESPONCE_TYPE){
            LogUtil.e(TAG, "type != FairyProtocol.RESPONCE_TYPE");
            return false;
        }
        index += 2;
        final byte attr = respData[index];
        final boolean isAttr = attr == FairyProtocol.RESPONCE_ATTR;
        if(!isAttr){
            LogUtil.e(TAG, "attr != FairyProtocol.RESPONCE_ATTR");
            return false;
        }
        return true;
//        index ++;
//        final int responseResult = Utils.bytesToInt(respData, index);
//        return responseResult == FairyProtocol.ATTR_RESPONSE_VALUE_OK;
    }



    /**
     *
     * @param reader
     * @return
     * @throws Exception
     */
    public static byte[] readNextData(CoReader reader) throws Exception {
        final byte[] dataLenBuffer = new byte[4];
        final int result = reader.read(dataLenBuffer, 0, dataLenBuffer.length);
        if (result <= 0) {
            return null;
        }
        final int datalen = BitUtils.bytesToInt(dataLenBuffer, 0);
        if (datalen < 0) {
            return null;
        }
        final byte[] data = new byte[datalen];
        final int dataResult = reader.read(data, 0, data.length);

        return dataResult >= 0 ? data : null;
    }

    public static class SimpleResponse implements ISockSendResponse {

        @Override
        public void onSendingCompelete() {

        }

        @Override
        public boolean onResponse(CoReader reader) throws Exception {
            return false;
        }

        @Override
        public void onSendFail(int errno, String msg, Exception e) {

        }
    }

    public abstract static class SimpleOkResponse implements ISockSendResponse {
        private Handler handler = new Handler(Looper.getMainLooper());
        @Override
        public boolean onResponse(CoReader reader) throws Exception {
            if(!isSimpleRespOk(readNextData(reader))){
                onSendFail(ISockResponse.ERR_EXCEPTION, "Null Script text", new IllegalArgumentException());
            } else {
                handler.post(this::onResponseOk);
            }
            return true;
        }

        @Override
        public final void onSendingCompelete() {
            handler.post(this::onSendOver);
        }

        @Override
        public final void onSendFail(int errno, String msg, Exception e) {
            handler.post(() -> onFail(errno, msg, e));
        }

        public void onFail(int errno, String msg, Exception e) {
        }

        public void onSendOver(){}

        public void onResponseOk(){}

    }


}
