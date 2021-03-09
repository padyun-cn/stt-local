package com.padyun.scripttools.biz.ui.data;

import com.mon.ui.list.compat.adapter.IBaseRecyclerModel;
import com.padyun.scripttools.compat.data.AbsCoConditon;

/**
 * Created by daiepngfei on 2019-12-31
 */
public abstract class AbsCoImportCondition<T extends AbsCoConditon> implements IBaseRecyclerModel {

    private T condition;
    private boolean isChecked;

    public AbsCoImportCondition(T condition, boolean isChecked) {
        this.condition = condition;
        this.isChecked = isChecked;
    }

    public T getCondition() {
        return condition;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}

