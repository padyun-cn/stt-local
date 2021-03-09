package com.padyun.scripttoolscore.compatible.data.model.actions;

import android.text.SpannableStringBuilder;

import com.uls.utilites.un.Useless;


import com.padyun.scripttoolscore.compatible.data.model.Duplicable;
import com.padyun.scripttoolscore.compatible.data.model.IDGen;
import com.padyun.scripttoolscore.compatible.data.model.SEBaseType;

import java.util.HashMap;

/**
 * Created by daiepngfei on 1/16/19
 */
public abstract class SEAction extends SEBaseType implements Cloneable, Duplicable {


    public static final String TYPE_TAP = "tap" ;
    public static final String TYPE_SLIDE = "slide" ;
    public static final String TYPE_BRAIN = "brain" ;
    public static final String TYPE_FINISH = "finish" ;
    public static final String TYPE_IMAGE = "image" ;
    public static final String TYPE_INPUT = "input" ;
    public static final String TYPE_VOID = "void" ;


    public static final String OP_ACTION = "op_action";
    public static final String CLICKER_COUNTER = "clicker_counter";
    public static final String CLICKER_JUDGE = "clicker_judge";
    public static final String CLICKER_ACTION = "clicker_action";
    public static final String CLICKER_IMAGE = "clicker_image";
    public static final String CLICKER_SLIDE = "clicker_slide";
    public static final String CLICKER_TAP = "clicker_tap";
    public static final String CLICKER_FINISH = "clicker_finish";
    public static final String CLICKER_INPUT = "clicker_input";

    public abstract String getType() ;

    private transient String aid = IDGen.genTmsStrRandom("action_");
    protected void newAid(){
        aid = IDGen.genTmsStrRandom("action_");
    }
    public String getAid() {
        return aid;
    }
    public SEAction aidCopy(SEAction aid){
        if(aid != null) this.aid = aid.aid;
        return this;
    }

    private String name;

    public static final String KEY_AID = "KEY_AID";
    public static final String KEY_NAME = "KEY_NAME";


    public String getName() {
        return Useless.nonNullStr(name);
    }

    public void setName(String name) {
        this.name = name;
    }
}
