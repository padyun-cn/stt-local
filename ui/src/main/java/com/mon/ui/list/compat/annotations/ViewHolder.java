package com.mon.ui.list.compat.annotations;


import com.mon.ui.list.compat.adapter.BaseRecyclerHolder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by daiepngfei on 2020-11-16
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewHolder {
    int layoutId();
    Class<? extends BaseRecyclerHolder> holder();
}
