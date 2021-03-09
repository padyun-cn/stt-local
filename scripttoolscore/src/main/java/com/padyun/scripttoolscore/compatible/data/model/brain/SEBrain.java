package com.padyun.scripttoolscore.compatible.data.model.brain;


import com.padyun.scripttoolscore.compatible.data.model.SEBaseType;

import com.uls.utilites.un.Useless;

/**
 * Created by daiepngfei on 1/16/19
 */
public abstract class SEBrain extends SEBaseType {
    public static final String TYPE_JUDGE = "judge" ;
    public static final String TYPE_COUNTER = "counter" ;
    public static final String TYPE_TIMER = "timer" ;
    public static final String TYPE_ALARM = "alarm" ;
    public abstract String getType() ;

    private int id;
    private String name;

    public String getName() {
        return Useless.isEmpty(name) ? "未知(" + id + ")" : name;
    }


    public void setName(String name) {
        this.name = name;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
