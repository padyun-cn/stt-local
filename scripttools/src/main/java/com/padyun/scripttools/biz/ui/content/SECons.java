package com.padyun.scripttools.biz.ui.content;

/**
 * Created by daiepngfei on 7/12/19
 */
public class SECons {

    public static class Permission {
        private String permission;
        private boolean ignored;

        Permission(String p) {
            this(p, false);
        }

        Permission(String p, boolean ig) {
            permission = p;
            ignored = ig;
        }

        public String getPermission() {
            return permission;
        }

        public boolean isIgnored() {
            return ignored;
        }
    }


    public static class Permissions {
        public static final Permission[] ALT = new Permission[]
                {
                        new Permission(android.Manifest.permission.READ_PHONE_STATE, true),
                        new Permission(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        new Permission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                };
    }

    public static class Ints {
        /**
         * 新增条件
         */
        public static final int REQC_CO_CONS_ADD = 123;
        public static final int REQC_CO_CON_EDIT_CROP = 124;
        public static final int REQC_CO_CON_EDIT_REGION = 125;
        public static final int REQC_CO_CON_EDIT_COLOR = 126;
        public static final int REQC_CO_CON_EDIT_CROP_SLIDE = 127;
        public static final int REQC_CO_CON_EDIT_NONEXIST = 128;

        public static final String KEY_CROP_IMG = "image";
        public static final String KEY_CROP_REQ_MODE = "mode";
        public static final String KEY_CROP_CROP_RECT = "crop";
        public static final String KEY_CROP_REGION_RECT = "cropRegion";
        public static final String KEY_CROP_SLIDE_START_RECT = "crop_slide_start_rect";
        public static final String KEY_CROP_SLIDE_END_RECT = "crop_slide_end_rect";
        public static final String KEY_CROP_REGION_MIN_RECT = "region_min";
        public static final String KEY_ORIGIN_PATH = "color_origin_path";
        public static final String KEY_COLOR_ENTITY = "key_color_entity";
        public static final String KEY_COLOR_PIXCEL = "pixcel";
        public static final String KEY_EDIT_NEW_OFFSET = "new_offset_point";
        public static final int VALUE_CROP_REQ_MODE_NORMAL = 0;
        public static final int VALUE_CROP_REQ_MODE_SIMPLE_CROP = -1;
        public static final int VALUE_CROP_REQ_MODE_SIMPLE_REGION = -2;
        public static final int VALUE_CROP_REQ_MODE_SIMPLE_COLOR = -3;
        public static final int VALUE_CROP_REQ_MODE_SIMPLE_CROP_OFFSET = -4;
        public static final int VALUE_CROP_REQ_MODE_SIMPLE_CROP_SLIDE = -5;
        public static final int VALUE_CROP_REQ_MODE_SIMPLE_CROP_CLICK = -6;
        public static final int VALUE_CROP_REQ_MODE_SIMPLE_NONEXIST = -7;
        public static final int VALUE_CROP_REQ_MODE_CROP_FROM_FILE = -8;

        public static final int SCRIPT_NEED_SAVE = 909;


        public static final String KEY_SCRIPT_FILE_PATH = "KEY_SCRIPT_FILE_PATH";
        public static final String KEY_STREAM_IP = "KEY_STREAM_IP";
        public static final String KEY_STREAM_AS_IP = "KEY_STREAM_AS_IP";
        public static final String REQUEST_ORIENTATION = "REQUEST_ORIENTATION";
        public static final String REQUEST_ORIENTATION_DRAWINGCACHE = "REQUEST_ORIENTATION_DRAWINGCACHE";
    }
}
