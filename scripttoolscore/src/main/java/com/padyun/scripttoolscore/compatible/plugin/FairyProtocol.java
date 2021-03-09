package com.padyun.scripttoolscore.compatible.plugin;

public class FairyProtocol {


    public static final short TYPE_START2 = 0x011e ;
    public static final short TYPE_PAUSE2 = 0x011f ;
    public static final short TYPE_RESUME2 = 0x0120 ;
    public static final short TYPE_STOP2 = 0X0121 ;

    /**
     * 启动任务
     * */
    public static final short RESPONCE_TYPE = 0x0001 ;

    // 请求值响应回来的
    //public static final byte RESPONCE_ATTR = 0x06;
    public static final byte RESPONCE_ATTR = 0x10;

    /**
     * 暂停任务
     * */
    public static final short TYPE_PAUSE = 0X0002 ;
    /**
     * 继续任务
     * */
    public static final short TYPE_RESUME = 0X0003 ;
    /**
     * 停止任务
     * */
    public static final short TYPE_STOP = 0X0004 ;

    /**
     * 请求截图
     */
    public static final short TYPE_REQUEST_IMAGE = 0X0005 ;
    /**
     * 更新模板图片
     * */
    public static final short TYPE_UPDATE_IMAGE = 0X0006 ;
    /**
     *  运行时信息
     */
    public static final short TYPE_RUNTIME_INFO = 0X0007 ;

    public static final short TYPE_TEST = 0x0008;



    public static final short REQ_CMD_CHECK_RUNNING_SCRIPT_STATE = 0x000d;
    public static final short RESP_TYPE_CHECK_RUNNING_SCRIPT_STATE = 0x000e;
    public static final short REQ_CMD_SCRIPT_LOG = 0x000f;


    /**
     * 返回屏幕截图
     * */
    public static final short TYPE_SCREENCAP = 0x0009 ;

    public static final short TYPE_IMAGELIST = 0x000a ;
    public static final short TYPE_DIFFLIST = 0x000b ;
    public static final short TYPE_RUNTIME_INTERVAL = 0x000c;

    public static final byte ATTR_STR = 0X01 ;
    public static final byte ATTR_IMAGE = 0X02 ;
    public static final byte ATTR_WIDTH = 0X03 ;
    public static final byte ATTR_HEIGHT = 0X04 ;
    public static final byte ATTR_X = 0X05 ;
    public static final byte ATTR_Y = 0X06 ;
    public static final byte ATTR_SIM = 0X07 ;
    public static final byte ATTR_THRESH = 0x08 ;
    public static final byte ATTR_MAXVAL = 0x09 ;
    public static final byte ATTR_TYPE = 0x0a ;
    public static final byte ATTR_FLAG = 0x0b ;
    public static final byte ATTR_PIC = 0x0c ;
    public static final byte ATTR_NAME = 0x0d ;

}
