package com.spring.network.socket.compat2;

/**
 * Created by daiepngfei on 11/11/19
 */
@SuppressWarnings("WeakerAccess")
abstract class AbsBaseRDRequest extends StatefulWRSockRequest {
    private String tag;
    private ISockResponse response;

    public AbsBaseRDRequest() {
    }

    public AbsBaseRDRequest(ISockResponse response) {
        this("", response);
    }

    public AbsBaseRDRequest(String tag, ISockResponse response) {
        this.tag = tag;
        this.response = response;
    }

    public void setResponse(ISockResponse response) {
        this.response = response;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    void error(int code, String msg, Exception e) {
        if (response != null) {
            response.onSendFail(code, msg, e);
        }
    }

    protected ISockResponse getResponse() {
        return response;
    }

    @Override
    public final void onReading(CoReader reader) throws Exception {
        try {
            onResponse(reader);
        } catch (Exception e) {
            e.printStackTrace();
            if (response != null) {
                response.onSendFail(ISockResponse.ERR_TYPE_ERROR, "error", e);
            }
            throw e;
        }
    }

    @Override
    public boolean onResponse(CoReader reader) throws Exception {
        if (response != null) {
            return response.onResponse(reader);
        }
        return false;
    }

    @Override
    public void onSendFail(int errno, String msg, Exception e) {
        if (response != null) {
            response.onSendFail(errno, msg, e);
        }
    }
}
