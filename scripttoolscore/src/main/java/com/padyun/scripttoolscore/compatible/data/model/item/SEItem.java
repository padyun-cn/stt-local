package com.padyun.scripttoolscore.compatible.data.model.item;

import com.padyun.scripttoolscore.compatible.ScriptCons;
import com.padyun.scripttoolscore.compatible.data.model.Duplicable;
import com.padyun.scripttoolscore.compatible.data.model.IDGen;
import com.padyun.scripttoolscore.compatible.data.model.SEBaseType;

import java.io.Serializable;

/**
 * Created by daiepngfei on 1/16/19
 */
public abstract class SEItem extends SEBaseType implements Duplicable<SEItem>, Cloneable, Serializable {
    /**
     * 图片存在
     */
    public static final int STATE_EXIST = 0x01;
    /**
     * 图片不存在
     */
    public static final int STATE_WITHOUT = 0x02;
    /**
     * 区域变化
     */
    public static final int STATE_CHANGE = 0x04;
    /**
     * 区域未变化
     */
    public static final int STATE_UNCHANGE = 0x08;
    /**
     * 区域颜色数量多于
     */
    public static final int STATE_MORE = 0x0100;
    /**
     * 区域颜色数量少于
     */
    public static final int STATE_LESS = 0x0200;


    public static final String TYPE_BRAIN = "brain";
    public static final String TYPE_COLOR = "color";
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_USER = "user";
    public static final String TYPE_GROUP = "group";


    int relation = ScriptCons.JRelation.FLAG_AND;

    public abstract String getType();
    private transient long TID = IDGen.getTmsRandomLong();
    public void newTID() {
        TID = IDGen.getTmsRandomLong();
    }
    public long getTID(){
        return TID;
    }
    public SEItem fromItemWitTid(SEItem item){
        if(item != null) this.TID = item.TID;
        return this;
    }
    public int getRelation() {
        return relation;
    }

    public void setRelation(int relation) {
        this.relation = relation;
    }

    @Override
    public SEItem clone() throws CloneNotSupportedException {
        return (SEItem) super.clone();
    }
}
