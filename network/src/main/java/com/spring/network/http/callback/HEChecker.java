package com.spring.network.http.callback;


/**
 * Created by daiepngfei on 2/9/18
 */

public class HEChecker {

    public static class Info {
        int code = -1;
        String msg = "";
    }

    public static Info getInfo(Exception e, OnNeedRelogin onNeedRelogin) {
        final Info info = new Info();
        if (HEResultCode.class.isInstance(e)) {
            final int code = ((HEResultCode) e).getCode();
            info.msg = ((HEResultCode) e).getMsg();
            /*if (isNeedRelogin(code)*//*|| code == Cons.NO_TOKEN *//*) {
                V2AccountDelegate.invalidToken();
                if(onNeedRelogin != null) onNeedRelogin.onRelogin();
            }*/
        }
        if (HEResultCode.class.isInstance(e)) {
            HEResultCode ce = (HEResultCode) e;
            info.code = ce.getCode();
            info.msg = ce.getMsg();
        }
        return info;
    }

    public static boolean isNeedRelogin(int code) {
        // return /*code == Cons.API.OVER_FALIURE ||*/ code == Cons.API.OVER_FALIURE_V2;
        return false;
    }

    public interface OnNeedRelogin {
        void onRelogin();
    }
}
