package com.uls.utilites.common;

/**
 * Created by daiepngfei on 2020-12-15
 */
public interface BreakedConsumer<T> {
    boolean breakingOn(T t);
}
