package com.padyun.scripttoolscore.compatible.data.model;

/**
 * Created by daiepngfei on 4/2/19
 */
public interface Duplicable<T> {
    T duplicate() throws CloneNotSupportedException;
}
