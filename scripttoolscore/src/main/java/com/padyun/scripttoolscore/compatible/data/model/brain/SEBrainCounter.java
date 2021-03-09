package com.padyun.scripttoolscore.compatible.data.model.brain;

/**
 * Created by daiepngfei on 1/16/19
 */
@SuppressWarnings("FieldCanBeLocal")
public class SEBrainCounter extends SEBrain {
    private final String type = TYPE_COUNTER ;
    /**
     * name :
     * id :
     * threshold : 6
     */


    private int threshold;

    @Override
    protected String getSeType() {
        return "brain_counter";
    }

    public String getType() {
        return type;
    }


    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public void mergeFrom(SEBrainCounter judge) {
        if(judge == null)return;
        this.threshold = judge.threshold;
        this.setName(judge.getName());
    }
}
