package com.padyun.scripttoolscore.compatible.data.model.actions;


import com.padyun.scripttoolscore.compatible.data.model.IVoid;

/**
 * Created by daiepngfei on 6/19/19
 */
public class SEActionVoid extends SEAction implements IVoid {
    @Override
    public String getType() {
        return TYPE_VOID;
    }

    @Override
    public Object duplicate() throws CloneNotSupportedException {
        return null;
    }

    @Override
    protected String getSeType() {
        return "action_void";
    }
}
