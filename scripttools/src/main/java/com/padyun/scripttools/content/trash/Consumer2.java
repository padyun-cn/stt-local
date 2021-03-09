package com.padyun.scripttools.content.trash;

/**
 * Created by daiepngfei on 1/22/19
 */
public interface Consumer2<T, V> {
    void accept(T t, V v);
}
