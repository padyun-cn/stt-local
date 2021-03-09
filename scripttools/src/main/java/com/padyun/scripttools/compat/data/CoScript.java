package com.padyun.scripttools.compat.data;

import com.google.gson.Gson;
import com.padyun.scripttools.content.data.CoIDGen;
import com.padyun.scripttoolscore.compatible.ScriptCons;
import com.padyun.scripttoolscore.compatible.data.model.SECondition;
import com.padyun.scripttoolscore.compatible.data.model.SEImage;
import com.padyun.scripttoolscore.compatible.data.model.SEScene;
import com.padyun.scripttoolscore.compatible.data.model.SEScript;
import com.padyun.scripttoolscore.compatible.data.model.actions.SEActionBrain;
import com.padyun.scripttoolscore.compatible.data.model.brain.SEBrainAlarm;
import com.padyun.scripttoolscore.compatible.data.model.brain.SEBrainCounter;
import com.padyun.scripttoolscore.compatible.data.model.item.SEItemBrain;
import com.uls.utilites.un.Useless;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.core.util.Consumer;

/**
 * Created by daiepngfei on 8/19/19
 */
public class CoScript {

    private final String co_version = "1";
    private final String id;
    private CoScene scene;

    private transient static final AtomicInteger BRAIN_ID_GENER = new AtomicInteger();

    public long getPublishingCode() {
        synchronized (this) {
            publishingCode++;
            return publishingCode;
        }
    }

    public void setPublishingCode(long code){
        this.publishingCode = code;
    }

    private long publishingCode = 0;
    private String publishedID = null;

    public String getPublishedID() {
        return publishedID;
    }

    public void setPublishedID(String publishedID) {
        this.publishedID = publishedID;
    }

    public static CoScript newScript(){
        return new CoScript(CoIDGen.genScriptID());
    }

    public String getId() {
        return id;
    }

    public CoScript(String id) {
        this.id = id;
        scene = new CoScene();
    }

    public List<SEImage> getAllSEImages() {
        List<SEImage> image_list = new ArrayList<>();
        SEScript seScript = buildToSEScript();
        seScript.forEachAllSEImgs(image_list::add);
        return image_list;
    }

    public List<SEImage> getAllSEImageDeDuplicated() {
        ArrayList<SEImage> images = new ArrayList<>();
        SEScript seScript = buildToSEScript();
        seScript.forEachAllSEImgs(image -> {
            if(!images.contains(image)){
                images.add(image);
            }
        });
        return images;
    }


    public void foreachSEImages(Consumer<SEImage> imageConsumer) {
        if(imageConsumer != null) {
            SEScript seScript = buildToSEScript();
            seScript.forEachAllSEImgs(imageConsumer);
        }
    }

    public void setConditions(List<AbsCoConditon> list){
        if(scene != null) {
            scene.setCondition_list(list);
        }
    }

    public List<AbsCoConditon> getConditions() {
        return scene.getCondition_list();
    }

    public String buildToJson() {
        return new Gson().toJson(this);
    }

    public void setScene(CoScene scene) {
        this.scene = scene;
    }

    public SEScript buildToSEScript() {
        if(scene != null && !Useless.isEmpty(id)) {

            SEScript script = new SEScript(id);
            SEScene sce = new SEScene();

            Useless.foreach(getConditions(), cond -> {
                if(cond.isDisabled()){
                    return;
                }
                // toCreate se-condition
                SECondition condition = new SECondition();
                condition.setCondition_id(cond.getCondition_id());

                // condition add item
                if(cond.getItem_list() != null) {
                    condition.setItem_list(cond.getItem_list());
                    //condition.addItem(cond.getItem_list().httpGet(0));
                }
                // condition add action
                if(cond.getAction_list() != null){
                    condition.addAction(cond.getAction_list().get(0));
                }

                // check counter - condition add brain item & action
                if(cond.getCo_brain_count() > 0) {

                    // toCreate new brain counter
                    SEBrainCounter counter = new SEBrainCounter();
                    counter.setId(BRAIN_ID_GENER.incrementAndGet());
                    counter.setThreshold(cond.getCo_brain_count());

                    // add new counter into script
                    script.addBrain(counter);

                    // -------item-------
                    // toCreate new brain item
                    SEItemBrain itemBrain = new SEItemBrain();
                    itemBrain.setRelation(ScriptCons.JRelation.FLAG_AND_NOT);
                    itemBrain.setId(counter.getId());

                    // add item into condition
                    condition.addItem(itemBrain);

                    // -------action-------
                    SEActionBrain actionBrain = new SEActionBrain();
                    actionBrain.setAction(SEActionBrain.ACTION_PLUS);
                    actionBrain.setId(counter.getId());

                    // add action into condition
                    condition.addAction(actionBrain);

                }

                if(cond.getCo_brainAlarm() != null){
                    SEBrainAlarm alarm = cond.getCo_brainAlarm();
                    alarm.setId(BRAIN_ID_GENER.incrementAndGet());
                    script.addBrain(alarm);

                    SEItemBrain itemBrain = new SEItemBrain();
                    itemBrain.setId(alarm.getId());
                    condition.addItem(itemBrain);
                }

                sce.addCondition(condition);

            });

            script.addScene(sce);
            return script;
        }
        return null;
    }


    public void addContions(List<AbsCoConditon> conditons) {
        if(Useless.isEmpty(conditons)) return;
        Useless.foreach(conditons, t -> {
            if(scene != null) scene.addCondtion(t);
        });
    }

    /**
     *
     * @param pos
     * @param tarPos
     */
    public void swapConditions(int pos, int tarPos){
        if(scene != null && scene.getCondition_list() != null){
            Collections.swap(scene.getCondition_list(), pos, tarPos);
        }
    }

    public void removeCondition(AbsCoConditon conditon) {
        if(scene != null){
            final String id = conditon.getCondition_id();
            List<AbsCoConditon> conditons = scene.getCondition_list();
            if(conditons.remove(conditon)) return;
            AbsCoConditon target = null;
            for (AbsCoConditon coConditon : conditons) {
                if(coConditon != null && Useless.equals(id, coConditon.getCondition_id())){
                    target = coConditon;
                    break;
                }
            }
            if(target != null){
                conditons.remove(target);
            }
        }
    }
}
