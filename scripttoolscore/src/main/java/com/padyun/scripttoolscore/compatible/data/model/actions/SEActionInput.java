package com.padyun.scripttoolscore.compatible.data.model.actions;



/**
 * Created by daiepngfei on 1/16/19
 */
@SuppressWarnings("FieldCanBeLocal")
public class SEActionInput extends SEAction {

    /**
     * type : brain
     * id :
     * action :
     */

    private final String type = TYPE_INPUT;
    private String input = "";

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    @Override
    protected String getSeType() {
        return "action_input";
    }

    public String getType() {
        return type;
    }

    @Override
    public Object duplicate() throws CloneNotSupportedException {
        SEActionInput finish = (SEActionInput) clone();
        finish.newAid();
        return finish;
    }
}
