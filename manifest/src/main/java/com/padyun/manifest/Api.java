package com.padyun.manifest;

/**
 * Created by daiepngfei on 2020-05-25
 */
public class Api {

    public static final class Scheme {
        public static final String HTTP = "http";
        public static final String HTTPS = "https";
        public static final String SUF = "://";
    }

    public static final class Host {
        public static final String MAIN = "api.padyun.com";
        public static final String TEST = "api.test.padyun.com";
    }

    public static final class Port {
        public static final int TEST = 9090;
    }

    /**
     *
     * @param path
     * @return
     */
    public static String test(String path) {
        return Scheme.HTTP  + Scheme.SUF + Host.TEST + ":" + Port.TEST + path;
    }

    /**
     *
     * @param path
     * @return
     */
    public static String main(String path) {
        return Scheme.HTTPS + Scheme.SUF + Host.MAIN + path;
    }


}
