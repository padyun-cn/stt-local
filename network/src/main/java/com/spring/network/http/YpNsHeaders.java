package com.spring.network.http;

import okhttp3.Request;

/**
 * Created by daiepngfei on 11/26/18
 */
class YpNsHeaders {
    static void addCommonHeader(Request.Builder bui){
//        bui.addHeader("company", Cnv.cbEmpty(YpFlavor.getYpChannelName()));
//        bui.addHeader("language", Cnv.cbEmpty(YpEnvironment.language()));
//        bui.addHeader("User-Agent", getUserAgent());
    }

//    private static String getUserAgent() {
//        String userAgent = "";
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            try {
//                userAgent = WebSettings.getDefaultUserAgent(AppContext.context());
//            } catch (Exception e) {
//                userAgent = System.getProperty("http.agent");
//            }
//        } else {
//            userAgent = System.getProperty("http.agent");
//        }
//        StringBuffer sb = new StringBuffer();
//        for (int i = 0, length = userAgent.length(); i < length; i++) {
//            char c = userAgent.charAt(i);
//            if (c <= '\u001f' || c >= '\u007f') {
//                sb.append(String.format("\\u%04x", (int) c));
//            } else {
//                sb.append(c);
//            }
//        }
//        return sb.toString();
//    }
}
