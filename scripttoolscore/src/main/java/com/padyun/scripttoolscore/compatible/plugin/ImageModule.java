package com.padyun.scripttoolscore.compatible.plugin;


import java.nio.ByteBuffer;

/**
 * 模板图片数据
 * */
public class ImageModule extends YpModule2 {

    public String name ;
    /**
     * X坐标
     */
    public int x ;
    /**
     * Y坐标
     */
    public int y ;
    /**
     * 宽
     */
    public int width ;
    /**
     * 高
     */
    public int height ;
    /**
     * 图片颜色类型 IMREAD_GRAYSCALE=0 IMREAD_COLOR=1 ，其它二值化
     */
    public int flag ;
    /**
     * 二值化阈值
     */
    public int thresh ;
    /**
     * 二值化最大值
     */
    public int maxval ;
    /**
     * 图片二值化类型
     */
    public int type ;

    public int sim ;
    /**
     * 图片
     */
    public ByteBuffer pic ;



    public ImageModule(){
        super(FairyProtocol.TYPE_UPDATE_IMAGE);
    }

    public ImageModule(byte[] data, int offset, int len) throws YpModuleException {
        super(data, offset, len);
    }

    @Override
    public void initField() {
        try {
            fields.put(FairyProtocol.ATTR_NAME, getClass().getField("name"));
            fields.put(FairyProtocol.ATTR_X, getClass().getField("x"));
            fields.put(FairyProtocol.ATTR_Y, getClass().getField("y"));
            fields.put(FairyProtocol.ATTR_WIDTH, getClass().getField("width"));
            fields.put(FairyProtocol.ATTR_HEIGHT, getClass().getField("height"));
            fields.put(FairyProtocol.ATTR_FLAG, getClass().getField("flag"));
            fields.put(FairyProtocol.ATTR_THRESH, getClass().getField("thresh"));
            fields.put(FairyProtocol.ATTR_MAXVAL, getClass().getField("maxval"));
            fields.put(FairyProtocol.ATTR_TYPE, getClass().getField("type"));
            fields.put(FairyProtocol.ATTR_SIM, getClass().getField("sim"));
            fields.put(FairyProtocol.ATTR_PIC, getClass().getField("pic"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
