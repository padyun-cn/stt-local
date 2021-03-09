package com.padyun.scripttoolscore.content.network;

import com.padyun.manifest.Api;
import com.padyun.scripttoolscore.models.HttpParam;
import com.uls.utilites.un.Useless;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.Dns;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by daiepngfei on 2020-05-25
 */
public class HttpStack {

    private static IDnsCreator sDnsCreator = null;

    public interface IDnsCreator {
        Dns getOrCreateDns();
    }

    public static void setDnsCreator(IDnsCreator sDnsCreator) {
        HttpStack.sDnsCreator = sDnsCreator;
    }

    public static OkHttpClient client() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS);
        if(sDnsCreator != null){
            builder.dns(sDnsCreator.getOrCreateDns());
        }
        return builder.build();
    }


    public static class Post {

        public static Response postSync(HttpUrl.Builder builder, Map<String, String> headers, HttpParam... param) throws IOException {
            final Request.Builder requestBuilder = getRequestBuilder(builder, headers, param);
            return HttpStack.client().newCall(requestBuilder.build()).execute();
        }

        public static void post(HttpUrl.Builder builder, Map<String, String> headers, Callback callback, HttpParam... param)  {
            HttpStack.client().newCall(getRequestBuilder(builder, headers, param).build())
                    .enqueue(callback);
        }

        @NotNull
        private static Request.Builder getRequestBuilder(HttpUrl.Builder builder, Map<String, String> headers, HttpParam[] param) {
            final FormBody.Builder bodyBuilder = new FormBody.Builder();
            Useless.foreach(param, p -> {
                if(Useless.noEmptyStr(p.getKey(), p.getValue())) {
                    bodyBuilder.add(p.getKey(), p.getValue());
                }
            });
            final Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.url(builder.build());
            requestBuilder.post(bodyBuilder.build());
            Useless.foreach(headers, h -> requestBuilder.addHeader(h.first, h.second));
            return requestBuilder;
        }
    }

    public static class Get {

        /**
         * @param builder
         * @param headers
         *
         * @return
         *
         * @throws IOException
         */
        public static Response getSync(HttpUrl.Builder builder, Map<String, String> headers) throws IOException {
            return HttpStack.client().newCall(request(builder, headers)).execute();
        }

        /**
         * @param builder
         * @param callback
         */
        public static void get(HttpUrl.Builder builder, Callback callback, HttpParam... params) {
            HttpStack.client().newCall(request(builder, HttpParam.toMap(params))).enqueue(callback);
        }


        /**
         * @param builder
         * @param headers
         * @param callback
         */
        public static void get(HttpUrl.Builder builder, Map<String, String> headers, Callback callback) {
            HttpStack.client().newCall(request(builder, headers)).enqueue(callback);
        }

        /**
         * @param builder
         * @param headers
         *
         * @return
         */
        private static Request request(HttpUrl.Builder builder, Map<String, String> headers) {
            Request.Builder requestBuilder = new Request.Builder();
            if (headers != null) {
                for (String key : headers.keySet()) {
                    if (key == null) continue;
                    final String value = headers.get(key);
                    if (value == null) continue;
                    requestBuilder.addHeader(key, value);
                }
            }
            requestBuilder.url(builder.build());
            return requestBuilder.build();
        }
    }

    /**
     * @param apiPath
     * @param parameters
     *
     * @return
     */
    public static HttpUrl.Builder urlBuilder(String apiPath, HttpParam... parameters) {
        return defaultUrlBuilder(Api.Scheme.HTTPS, Api.Host.MAIN, null, apiPath, HttpParam.toMap(parameters));
    }

    /**
     * @param apiPath
     * @param parameters
     *
     * @return
     */
    public static HttpUrl.Builder urlBuilderForTest(String apiPath, HttpParam... parameters) {
        return defaultUrlBuilder(Api.Scheme.HTTP, Api.Host.TEST, null, apiPath, HttpParam.toMap(parameters));
    }

    /**
     * @param apiPath
     * @param parameters
     *
     * @return
     */
    public static HttpUrl.Builder urlBuilder(String apiPath, Map<String, String> parameters) {
        return defaultUrlBuilder(Api.Scheme.HTTPS, Api.Host.MAIN, null, apiPath, parameters);
    }

    /**
     * @param apiPath
     * @param parameters
     *
     * @return
     */
    public static HttpUrl.Builder urlBuilderForTest(String apiPath, Map<String, String> parameters) {
        return defaultUrlBuilder(Api.Scheme.HTTP, Api.Host.TEST, 9090, apiPath, parameters);
    }

    /**
     * @param scheme
     * @param host
     * @param port
     * @param apiPath
     * @param parameters
     *
     * @return
     */
    public static HttpUrl.Builder defaultUrlBuilder(String scheme, String host, Integer port, String apiPath, Map<String, String> parameters) {
        if (Useless.isEmpty(apiPath)) {
            return null;
        }
        final HttpUrl.Builder httpUrlBuilder = new HttpUrl.Builder();
        httpUrlBuilder.scheme(scheme);
        httpUrlBuilder.host(host);
        if (port != null) {
            httpUrlBuilder.port(port);
        }
        Useless.foreach(Useless.sSplits(apiPath, "/"), httpUrlBuilder::addPathSegment);
        Useless.foreach(parameters, p -> {
            if (Useless.noEmptyStr(p.first, p.second)) {
                //noinspection ConstantConditions
                httpUrlBuilder.addQueryParameter(p.first, p.second);
            }
        });
        return httpUrlBuilder;
    }


}
