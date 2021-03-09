package com.padyun.scripttoolscore.compatible.data.model;

/**
 * Created by daiepngfei on 1/16/19
 */
public abstract class SEBaseType extends SEBase {
    private  final String se_type = getSeType();
    protected abstract String getSeType();
}