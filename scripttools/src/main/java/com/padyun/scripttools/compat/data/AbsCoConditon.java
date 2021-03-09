package com.padyun.scripttools.compat.data;


import com.mon.ui.list.compat.adapter.IBaseRecyclerModel;
import com.padyun.scripttoolscore.compatible.data.model.SECondition;
import com.padyun.scripttoolscore.compatible.data.model.brain.SEBrainAlarm;

/**
 * Created by daiepngfei on 8/19/19
 */
public abstract class AbsCoConditon extends SECondition implements IBaseRecyclerModel {
    private boolean isValid = true;
    private boolean co_isDefaultTimeout = true;
    private boolean disabled = false;
    private transient UIFlags uiFlags = new UIFlags();

    /*-----brains-------*/
    /*
     * brain 业务包括计数count和定时alarm两种
     * 都为"单例"，即一个condition中count和alarm只能set
     * 不能add。最终在CoScript的时候"翻译"成兼容类SEScript。
     */
    private int co_brain_count = 0; // brain count
    private SEBrainAlarm co_brainAlarm; // brain alarm
    public int getCo_brain_count() {
        return co_brain_count;
    }

    public void setCo_brain_count(int co_brain_counter) {
        this.co_brain_count = co_brain_counter;
    }
    public SEBrainAlarm getCo_brainAlarm() {
        return co_brainAlarm;
    }

    public void setCo_brainAlarm(SEBrainAlarm alarm) {
        this.co_brainAlarm = alarm;
        if(this.co_brainAlarm != null){
            this.co_brainAlarm.setTimeout((alarm.getEndHour() - alarm.getStartHour()) * 60 + (alarm.getEndMin() - alarm.getStarMin()));
            this.co_brainAlarm.setTimeofday(alarm.getStartHour() + ":" + alarm.getStarMin() + ":00");
        }
    }

    /*
     * 用作导入时候的UI状态和标记
     */
    public static class UIFlags {
        public enum InsertingState {
            OFF, STANDBY, INSERTING
        }

        private InsertingState insertingState = InsertingState.OFF;

        public InsertingState getInsertingState() {
            return insertingState;
        }

        public void setInsertStateOFF(){
            insertingState = UIFlags.InsertingState.OFF;
        }

        public void setInsertStateInserting(){
            insertingState = UIFlags.InsertingState.INSERTING;
        }

        public void setInsertStateStandby(){
            insertingState = UIFlags.InsertingState.STANDBY;
        }
    }


    public UIFlags getUiFlags() {
        return uiFlags;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public int getCo_timeout() {
        return co_timeout;
    }

    public void setCo_timeout(int co_timeout) {
        this.co_timeout = co_timeout;
        onSettingCoTimeout(co_timeout);
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    protected abstract void onSettingCoTimeout(int co_timeout);

    private int co_timeout = 1000;
    private final String co_type = getCo_type();
    public abstract String getCo_type();

    public boolean isCo_isDefaultTimeout() {
        return co_isDefaultTimeout;
    }

    public void setCo_isDefaultTimeout(boolean co_isDefaultTimeout) {
        this.co_isDefaultTimeout = co_isDefaultTimeout;
    }
}
