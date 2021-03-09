package com.padyun.scripttoolscore.compatible.data.model.coord;


import com.padyun.scripttoolscore.compatible.data.model.Duplicable;
import com.padyun.scripttoolscore.compatible.data.model.SEBaseType;

import java.io.Serializable;

/**
 * Created by daiepngfei on 1/16/19
 */
public abstract class SECoord extends SEBaseType implements Serializable, Cloneable, Duplicable<SECoord> {

    public static final String TYPE_FIXED = "fixed" ;
    public static final String TYPE_IMAGE = "image" ;
    public abstract String getType();

}
