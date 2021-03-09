package com.padyun.scripttoolscore.compatible.plugin;


import java.nio.ByteBuffer;



/**
 * 截图
 * */
public class ScreencapModule extends YpModule2 {

    public ByteBuffer pic ;
    public int width ;
    public int height ;

    public ScreencapModule() {
        super(FairyProtocol.TYPE_SCREENCAP);
    }
    public ScreencapModule(byte[] data, int offset, int len) throws YpModuleException {
        super(data, offset, len);
    }

    @Override
    public void initField() {
        try {
            fields.put(FairyProtocol.ATTR_IMAGE, getClass().getField("pic")) ;
            fields.put(FairyProtocol.ATTR_WIDTH, getClass().getField("width")) ;
            fields.put(FairyProtocol.ATTR_HEIGHT, getClass().getField("height")) ;

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
