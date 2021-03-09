package com.padyun.scripttoolscore.compatible.data.model;


import com.uls.utilites.un.Useless;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daiepngfei on 1/16/19
 */
public class SEScene extends SEBase {
    private String name;
    private int scene_id;
    private SECondition scene_condition;
    private List<SECondition> condition_list;

    public SEScene() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScene_id() {
        return scene_id;
    }

    public void setScene_id(int scene_id) {
        this.scene_id = scene_id;
    }

    public SECondition getScene_condition() {
        return scene_condition;
    }

    public void setScene_condition(SECondition scene_condition) {
        this.scene_condition = scene_condition;
    }

    public List<SECondition> getCondition_list() {
        return condition_list;
    }

    public void setCondition_list(List<SECondition> condition_list) {
        this.condition_list = condition_list;
    }

    public void addCondition(SECondition condition) {
        if (this.condition_list == null) this.condition_list = new ArrayList<>();
        if (condition != null) this.condition_list.add(condition);
    }

    public void removeCondition(SECondition condition) {
        if (condition_list != null && condition != null) this.condition_list.remove(condition);
    }

    public void swapCondition(SECondition data, SECondition old) {
        Useless.swap(condition_list, data, old);
    }

    public boolean isEmpty() {
        return scene_condition == null && Useless.isEmpty(condition_list);
    }

    public void replaceOrAddCondition(SECondition t, SECondition old) {
        if(old == scene_condition){
            scene_condition = t;
        } else if(!Useless.replace(condition_list, old, t)) {
            addCondition(t);
        }
    }


    /*public void addCondition(SECondition condition){
        if(scene_condition == null) scene_condition = condition;
        else {
            if (this.condition_list == null) this.condition_list = new ArrayList<>();
            if (condition != null) this.condition_list.add(condition);
        }
    }

    public void removeCondition(SECondition condition){
        if(condition != null){
            if(scene_condition != null && CUtils.eqauls(condition.getCondition_id(), scene_condition.getCondition_id())){
                scene_condition = null;
                return;
            }
            if(condition_list != null){
                SECondition tar = null;
                for(SECondition c : condition_list){
                    if(c == null) continue;
                    if(CUtils.eqauls(c.getCondition_id(), condition.getCondition_id())){
                        tar = c;
                        break;
                    }
                }
                if(tar != null) this.condition_list.remove(tar);
            }
        }

    }*/
}
