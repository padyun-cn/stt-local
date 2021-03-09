package com.padyun.scripttoolscore.compatible.data.model.brain;

/**
 * Created by daiepngfei on 3/27/19
 */
public class SEBrainAlarm extends SEBrain {

    private Integer startHour, endHour, starMin, endMin;

    public Integer getStartHour() {
        return startHour;
    }

    public void setStartHour(Integer startHour) {
        this.startHour = startHour;
    }

    public Integer getEndHour() {
        return endHour;
    }

    public void setEndHour(Integer endHour) {
        this.endHour = endHour;
    }

    public Integer getStarMin() {
        return starMin;
    }

    public void setStarMin(Integer starMin) {
        this.starMin = starMin;
    }

    public Integer getEndMin() {
        return endMin;
    }

    public void setEndMin(Integer endMin) {
        this.endMin = endMin;
    }

    @Override
    public String getType() {
        return "alarm";
    }

    @Override
    protected String getSeType() {
        return "brain_alarm";
    }

    private String timeofday;
    private long timeout = 5; // minute

    public String getTimeofday() {
        return timeofday;
    }

    public void setTimeofday(String timeofday) {
        this.timeofday = timeofday;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void mergeFrom(SEBrainAlarm alarm) {
        if(alarm == null) return;
        this.timeofday = alarm.timeofday;
        this.timeout = alarm.timeout;
        this.setName(alarm.getName());
    }
}
