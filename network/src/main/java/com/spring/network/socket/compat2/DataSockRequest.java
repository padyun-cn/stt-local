package com.spring.network.socket.compat2;

import com.uls.utilites.un.Useless;
import com.uls.utilites.utils.LogUtil;

/**
 * Created by daiepngfei on 11/11/19
 */
@SuppressWarnings("WeakerAccess")
public class DataSockRequest extends AbsBaseRDRequest {
    private DataSocketParams params;

    public DataSockRequest() {
    }

    public DataSockRequest(byte[]... data) {
        this(data, null);
    }

    public DataSockRequest(DataSocketParams params, ISockResponse response) {
        this(params, "", response);
    }

    public DataSockRequest(DataSocketParams params, String tag, ISockResponse response) {
        super(tag, response);
        this.params = params;
    }

    public DataSockRequest(byte[] data, ISockResponse response) {
        this(data, "", response);
    }

    public DataSockRequest(byte[] data, String tag, ISockResponse response) {
        this(new byte[][]{data}, tag, response);
    }

    public DataSockRequest(byte[][] data, ISockResponse response) {
        this(data, "", response);
    }

    public DataSockRequest(byte[][] data, String tag, ISockResponse response) {
        this(new DataSocketParams(data), tag, response);
    }

    DataSocketParams getParams() {
        return params;
    }

    public void setParams(DataSocketParams params) {
        this.params = params;
    }

    public void setParams(byte[]... data){
        this.params = new DataSocketParams(data);
    }

    @Override
    public final void onWriting(CoWriter writer) throws Exception {
        LogUtil.d("DataSockRequest", "requestTag{" + Useless.nonNullStr(getTag()) + "} onWriting START ");
        if (params != null && !params.isEmpty()) {
            try {
                for (byte[] bytes : params.getParams()) {
                    if (bytes != null && bytes.length > 0) {
                        writer.write(bytes);
                    }
                }
            } catch (Exception e) {
                error(ISockResponse.ERR_EXCEPTION, "Writting Error!", e);
                LogUtil.e("DataSockRequest", "requestTag{" + Useless.nonNullStr(getTag()) + "} onWriting Error ", e);
                throw e;
            }
        }

        onWrittingFinished();
    }

    protected void onWrittingFinished() {
        /*if(ISockSendResponse.class.isInstance(getResponse())){
            ((ISockSendResponse)getResponse()).onSendingCompelete();
        }*/
        LogUtil.d("DataSockRequest", "requestTag{" + Useless.nonNullStr(getTag()) + "} onWriting END ");
    }

}
