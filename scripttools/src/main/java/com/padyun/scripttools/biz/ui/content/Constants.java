package com.padyun.scripttools.biz.ui.content;

/**
 * Created by daiepngfei on 2020-05-14
 */
public class Constants {

    public static class Ints {
        public static final String KEY_IP = "IP";
        public static final String KEY_ASIP = "ASIP";
        public static final String KEY_VERIFY = "VERIFY";
        public static final String KEY_GAME_ID = "GAMEID";
        public static final String KEY_USER_ID = "USERID";
    }

    public static class Tips {
        public static final String NET_ERROR = "网络错误，请稍候重试";
    }

    public static class ApiPath {
        public static class Segs {
            public static final String Ypaih = "Ypaih";
            public static final String QINIU_TOKEN = "GetUpInfo";
            public static final String CREATE_ADD_TASK = "AddTask";
            public static final String GET_RESOUCES_WITH_TASK_ID = "GetDetail";
        }
        public static class Phrase {
            public static final String QINIU_TOKEN = "/Ypaih/GetUpInfo";
            public static final String CREATE_ADD_TASK = "/Ypaih/AddTask";
            public static final String GET_RESOUCES_WITH_TASK_ID = "/Ypaih/GetDetail";
            public static final String GET_USERS_TASK_LIST = "/Ypaih/List";
            public static final String POST_TASK_STATUS = "/Ypaih/TaskStatus";
            public static final String GET_CHECK_TASK_STATUS = "/Ypaih/TaskCheckStatus";
            public static final String POST_CLEAR_TASK = "/Ypaih/ClearTask";
            public static final String POST_REMOVE_TASK = "/Ypaih/Remove";
            public static final String POST_SUBMIT_TASK = "/Ypaih/Submit";
            public static final String POST_SHARE_CODE = "/Ypaih/ShareCode";
            public static final String POST_SUBSCRIPTION = "/Ypaih/Subscription";
        }
    }

    public static class Folder {

        public static final String SCRIPT = V1.SCRIPT;
        public static final String CROP = V1.CROP;
        public static final String SCREEN = V1.SCREEN;
        public static final String IMAGE_CACHE = V1.IMAGE_CACHE;

        public static class V1 {
            public static final String SCRIPT = "script_data";
            public static final String CROP = "crop";
            public static final String SCREEN = "screen";
            public static final String IMAGE_CACHE = "image_cache";
        }
    }
}
