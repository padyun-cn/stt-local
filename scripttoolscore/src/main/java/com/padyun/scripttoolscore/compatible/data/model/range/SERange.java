package com.padyun.scripttoolscore.compatible.data.model.range;


import com.padyun.scripttoolscore.compatible.data.model.Duplicable;
import com.padyun.scripttoolscore.compatible.data.model.SEBaseType;

/**
 * Created by daiepngfei on 1/16/19
 */
public abstract class SERange extends SEBaseType implements Cloneable, Duplicable<SERange> {

    public static final String TYPE_SIZE = "size";
    public static final String TYPE_IMAGE = "image" ;

    public abstract String getType() ;

}
