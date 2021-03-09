package com.padyun.scripttoolscore.compatible.data.model.item;

import com.padyun.scripttoolscore.compatible.data.model.brain.SEBrain;

/**
 * Created by daiepngfei on 1/16/19
 */
@SuppressWarnings("FieldCanBeLocal")
public class SEItemBrain extends SEItem {
    /**
     * type : user
     * relation :
     * key :
     * value :
     */

    private final String type = TYPE_BRAIN;
    private int id;

    @Override
    protected String getSeType() {
        return "item_brain";
    }

    public String getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SEItemBrain seClone(){
        SEItemBrain brain = null;
        try {
             brain = (SEItemBrain) this.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return brain;
    }

    @Override
    public SEItem duplicate() throws CloneNotSupportedException {
        SEItem item = this.clone();
        item.newTID();
        return this;
    }
}
