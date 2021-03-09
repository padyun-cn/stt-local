package com.padyun.scripttools.compat.data;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Created by daiepngfei on 8/19/19
 */
public class CoScene {
    private String name;
    private int scene_id;
    private List<AbsCoConditon> condition_list = new ArrayList<>();

    public void setId(int id) {
        this.scene_id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCondition_list(List<AbsCoConditon> condition_list){
        this.condition_list = condition_list;
    }

    public List<AbsCoConditon> getCondition_list() {
        return condition_list;
    }

    public void addCondtion(@NonNull AbsCoConditon absCoConditon) {
        condition_list.add(absCoConditon);
    }
}
