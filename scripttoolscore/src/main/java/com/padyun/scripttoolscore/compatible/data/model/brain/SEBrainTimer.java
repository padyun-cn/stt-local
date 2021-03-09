package com.padyun.scripttoolscore.compatible.data.model.brain;

/**
 * Created by daiepngfei on 1/16/19
 */
@SuppressWarnings("FieldCanBeLocal")
public class SEBrainTimer extends SEBrain {
    private final String type = TYPE_TIMER;
    /**
     * name :
     * id :
     * delay :
     * period :
     */

    private long delay;
    private int period;

    @Override
    protected String getSeType() {
        return "brain_timer";
    }

    public String getType() {
        return type;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public void mergeFrom(SEBrainTimer judge) {
        if(judge == null) return;
        this.delay = judge.delay;
        this.period = judge.period;
        this.setName(judge.getName());
    }
}
