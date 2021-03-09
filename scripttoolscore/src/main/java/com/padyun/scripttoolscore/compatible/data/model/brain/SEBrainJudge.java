package com.padyun.scripttoolscore.compatible.data.model.brain;

/**
 * Created by daiepngfei on 1/16/19
 */
@SuppressWarnings("FieldCanBeLocal")
public class SEBrainJudge extends SEBrain {
    private final String type = TYPE_JUDGE;
    /**
     * name :
     * id :
     * default : false
     */

    private boolean defaultValue = true;

    @Override
    protected String getSeType() {
        return "brain_judge";
    }

    public String getType() {
        return type;
    }


    public boolean getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void mergeFrom(SEBrainJudge judge) {
        if(judge == null) return;
        this.defaultValue = judge.defaultValue;
        this.setName(judge.getName());
    }
}
