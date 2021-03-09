package com.padyun.scripttoolscore.compatible.data.model.actions;



/**
 * Created by daiepngfei on 1/16/19
 */
@SuppressWarnings("FieldCanBeLocal")
public class SEActionFinish extends SEAction {

    /**
     * type : brain
     * id :
     * action :
     */

    private final String type = TYPE_FINISH;
    private int state = 99;

    @Override
    protected String getSeType() {
        return "action_finish";
    }

    public String getType() {
        return type;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }


    @Override
    public Object duplicate() throws CloneNotSupportedException {
        SEActionFinish finish = (SEActionFinish) clone();
        finish.newAid();
        return finish;
    }
}
