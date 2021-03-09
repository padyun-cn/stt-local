package com.padyun.scripttoolscore.compatible.plugin;


/**
 * 接收，发送一个字符串
 * */
public class StringModule extends YpModule2 {

    public String str ;

    public StringModule(short type) {
        super(type);
    }
    public StringModule(byte[] data, int offset, int len) throws YpModuleException {
        super(data, offset, len);
    }

    @Override
    public void initField() {
        try {
            fields.put(FairyProtocol.ATTR_STR, getClass().getField("str")) ;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
