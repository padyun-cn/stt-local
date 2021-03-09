package com.padyun.scripttoolscore.compatible.data.model.actions;

import com.uls.utilites.un.Useless;

/**
 * Created by daiepngfei on 1/16/19
 */
@SuppressWarnings("FieldCanBeLocal")
public class SEActionBrain extends SEAction {

    public static final String ACTION_TRUE = "true";
    public static final String ACTION_FALSE = "false";
    public static final String ACTION_RESET = "reset";
    public static final String ACTION_SUB = "sub";
    public static final String ACTION_PLUS = "plus";


    /**
     * type : brain
     * id :
     * action :
     */

    private final String type = TYPE_BRAIN;
    private int id;
    private String action;

    @Override
    protected String getSeType() {
        return "action_brain";
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String getName() {
        final String name = super.getName();
        return Useless.isEmpty(name) ? "未知(" + id + ")" : name;
    }

    @Override
    public Object duplicate() throws CloneNotSupportedException {
        SEActionBrain brain = (SEActionBrain) clone();
        brain.newAid();
        return brain;
    }

    public SEActionBrain seClone() {
        SEActionBrain brain = null;
        try {
            brain = (SEActionBrain) clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return brain;
    }
}
