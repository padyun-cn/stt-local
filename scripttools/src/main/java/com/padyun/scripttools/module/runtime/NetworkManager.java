package com.padyun.scripttools.module.runtime;

import com.padyun.manifest.Api;
import com.padyun.scripttools.module.runtime.test.TestProxy;
import com.padyun.scripttoolscore.content.network.HttpStack;
import com.padyun.scripttoolscore.content.network.SockResponseUtils;
import com.padyun.scripttoolscore.models.HttpParam;

import java.io.IOException;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;

/**
 * Created by daiepngfei on 2020-06-05
 */
public class NetworkManager {

    private StContext context;
    private SocketProxy mSocketProxy = new SocketProxy();


    NetworkManager(StContext context) {
        this.context = context;
    }

    private HttpProxy mHttpProxy = new HttpProxy();

    public HttpProxy http() {
        return mHttpProxy;
    }

    public NetworkManager setDns(HttpStack.IDnsCreator dns){
        HttpStack.setDnsCreator(dns);
        return this;
    }

    public SocketProxy socket() {
        return mSocketProxy;
    }


    /**
     *
     * @param usingHttp
     */
    public NetworkManager setUsingHttp(boolean usingHttp) {
        http().isUsingHttp = usingHttp;
        return this;
    }

    /**
     *
     * @param usingDebugHost
     * @return
     */
    public NetworkManager setUsingDebugHost(boolean usingDebugHost) {
        http().isUsingDebugHost = usingDebugHost;
        return this;
    }

    public class HttpProxy {

        private boolean isUsingHttp = false;
        private boolean isUsingDebugHost = false;


        private HttpProxy() {

        }

        /**
         * @param apiPath
         * @param callback
         */
        public void get(String apiPath, Callback callback, HttpParam... params) {
            get(apiPath, HttpParam.toMap(params), callback);
        }

        /**
         * @param apiPath
         * @param parameters
         * @param callback
         */
        public void get(String apiPath, Map<String, String> parameters, Callback callback) {
            HttpStack.Get.get(getDefaultBuilder(apiPath, parameters), getDefaultTokenHeaders(), callback);
        }


        /**
         * @param apiPath
         * @param params
         *
         * @return
         *
         * @throws IOException
         */
        public Response getSync(String apiPath, HttpParam... params) throws IOException {
            return HttpStack.Get.getSync(getDefaultBuilder(apiPath, HttpParam.toMap(params)), getDefaultTokenHeaders());
        }


        private HttpUrl.Builder getDefaultBuilder(String apiPath, Map<String, String> stringStringMap) {
            return HttpStack.defaultUrlBuilder(
                        isUsingHttp ? Api.Scheme.HTTP : Api.Scheme.HTTPS,
                        isUsingDebugHost ?  Api.Host.TEST : Api.Host.MAIN,
                        isUsingDebugHost ? 9090 : null,
                        apiPath,
                        stringStringMap
                    );
        }

        private Map<String, String> getDefaultTokenHeaders() {
            Map<String, String> headers = context.getManifest().getCommonHeaders();
            headers.put("token", context.getManifest().getUserToken());
            return headers;
        }


        public Response postSync(String apiPath, HttpParam... params) throws IOException {
            return HttpStack.Post.postSync(getDefaultBuilder(apiPath, null), getDefaultTokenHeaders(), params);
        }


        public void post(String apiPath, Callback callback, HttpParam... params) {
            HttpStack.Post.post(getDefaultBuilder(apiPath, null), getDefaultTokenHeaders(), callback, params);
        }

    }

    public TestProxy applyTest() {
        return new TestProxy(context.getManifest().getDeviceIp(), context.getManifest().getDeviceAsIp());
    }

    public class SocketProxy {
        private SocketProxy() {
        }

        public void stopCurrent(SockResponseUtils.SimpleOkResponse response) {
            applyTest().sendCmdStop(new SockResponseUtils.SimpleOkResponse() {
                @Override
                public void onFail(int errno, String msg, Exception e) {
                    super.onFail(errno, msg, e);
                    if (response != null) {
                        response.onSendFail(errno, msg, e);
                    }
                }

                @Override
                public void onResponseOk() {
                    super.onResponseOk();
                    if (response != null) {
                        response.onResponseOk();
                    }
                }
            });
        }
    }

}
