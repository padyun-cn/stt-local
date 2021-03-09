package com.padyun.scripttoolscore.compatible.data.model;


import com.padyun.scripttoolscore.compatible.data.util.SEModelUtil;
import com.padyun.scripttoolscore.compatible.data.model.brain.SEBrain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.core.util.Consumer;
import com.uls.utilites.un.Useless;

/**
 * Created by daiepngfei on 1/17/19
 */
public class SEScript extends SEBase {
    private List<SEScene> scenes;
    private List<SEBrain> brain_list;
    private List<SEImage> image_list;
    private final String id;
    
    public SEScript(String id){
        this.id = id;
    }

    // ou...
    private LinkedHashMap<String, String> user_config;

    public String buildToJson(){
        return SEModelUtil.buildDataString(this);
    }

    public void swapScene(SEScene data, SEScene old) {
        Useless.swap(scenes, data, old);
    }

    public List<SEScene> getScenes() {
        return scenes;
    }

    public void setScenes(List<SEScene> scenes) {
        this.scenes = scenes;
    }


    public void addScene(SEScene seScene) {
        if (seScene != null) {
            if (scenes == null) scenes = new ArrayList<>();
            scenes.add(seScene);
        }
    }

    public void removeScene(SEScene seScene) {
        if (scenes != null) scenes.remove(seScene);
    }

    public List<SEBrain> getBrain_list() {
        return brain_list;
    }

    public void setBrain_list(List<SEBrain> brain_list) {
        this.brain_list = brain_list;
    }

    public List<SEImage> getImage_list() {
        if(image_list == null){
            image_list = new ArrayList<>();
            forEachAllSEImgs(image -> image_list.add(image));
        }
        return image_list;
    }

    public void setImage_list(List<SEImage> image_list) {
        this.image_list = image_list;
    }

    public boolean addBrain(SEBrain brain) {
        if (brain_list == null) brain_list = new ArrayList<>();
        for (SEBrain b : brain_list) {
            if (b != null && b.getId() == brain.getId()) {
                Useless.replace(brain_list, b, brain);
                return false;
            }
        }
        brain_list.add(brain);
        return true;
    }


    public void forEachAllSEImgs(Consumer<SEImage> img) {
        Useless.foreach(getScenes(), t -> {
            SECondition condition = t.getScene_condition();
            if (condition != null) condition.forEachSEImage(img);
            Useless.foreach(t.getCondition_list(), con -> con.forEachSEImage(img));
        });
    }

}
