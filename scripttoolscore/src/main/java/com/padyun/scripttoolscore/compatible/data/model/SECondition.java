package com.padyun.scripttoolscore.compatible.data.model;

import com.uls.utilites.un.Useless;
import androidx.core.util.Consumer;
import com.padyun.scripttoolscore.compatible.data.model.actions.SEAction;
import com.padyun.scripttoolscore.compatible.data.model.item.SEItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daiepngfei on 1/16/19
 */
public class SECondition extends SEBase implements Cloneable, Duplicable<SECondition> {
    public static final int CONDITION_YES = 0;
    public static final int CONDITION_NO = 1;
    private String condition_id;
    private String name;
    private List<SEItem> item_list;
    private List<SEAction> action_list;
    private transient boolean isFolderAttrs;
    private int condition = CONDITION_YES;

    public String getCondition_id() {
        return condition_id;
    }

    public SECondition(String name) {
        this.name = name;
        setCondition_id(IDGen.genTmsStrRandom("condition_id_"));
    }

    public SECondition() {
        this("无名");
    }


    public void setCondition_id(String condition_id) {
        this.condition_id = condition_id;
    }

    public String getName() {
        return Useless.nonNullStr(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SEItem> getItem_list() {
        return item_list;
    }

    public void setItem_list(List<SEItem> item_list) {
        this.item_list = item_list;
    }

    public List<SEAction> getAction_list() {
        return action_list;
    }

    public void setAction_list(List<SEAction> action_list) {
        this.action_list = action_list;
    }

    public void addItem(SEItem item) {
        if (item_list == null) item_list = new ArrayList<>();
        if (item != null) item_list.add(item);
    }

    public void addOrReplaceItemWithId(SEItem item) {
        if (item == null) return;
        if (item_list == null) item_list = new ArrayList<>();
        int indexDelete = -1;
        for (int i = 0; i < item_list.size(); i++) {
            final SEItem it = item_list.get(i);
            if (it == null) continue;
            if (item.getTID() == it.getTID()) {
                indexDelete = i;
                break;
            }
        }
        if (indexDelete >= 0) {
            item_list.add(indexDelete, item);
            item_list.remove(indexDelete + 1);
        } else {
            item_list.add(item);
        }

    }

    public void removeItem(SEItem item) {
        if (item_list != null) item_list.remove(item);
    }
    public void removeItem(int item) {
        if (item_list != null) item_list.remove(item);
    }

    public void addAction(SEAction action) {
        if (action_list == null) action_list = new ArrayList<>();
        if (action != null) action_list.add(action);
    }

    public void removeAction(SEAction action) {
        if (action_list != null && action != null) {
            SEAction t = null;
            for (SEAction a : action_list) {
                if (a == null) continue;
                if (a.getAid().equals(action.getAid())) {
                    t = a;
                    break;
                }
            }
            if (t != null) action_list.remove(t);
            else action_list.remove(action);
        }
    }



    public void forEachSEImage(Consumer<SEImage> seImageConsumer) {
        if(seImageConsumer == null) return;
        Consumer<ISEImageContainer> containerConsumer = container -> {
            if(container.getSEImage() != null) seImageConsumer.accept(container.getSEImage());
        };
        //Useless.foreach(item_list, t -> ClzCaster.cast(t, ISEImageContainer.class, containerConsumer));
        Useless.foreach(item_list, t -> {
            if(t instanceof ISEImageContainer){
                containerConsumer.accept((ISEImageContainer) t);
            }
        });
        //Useless.foreach(action_list, t -> ClzCaster.cast(t, ISEImageContainer.class, containerConsumer ));
        Useless.foreach(action_list, t -> {
            if(t instanceof ISEImageContainer){
                containerConsumer.accept((ISEImageContainer) t);
            }
        });
    }

    @Override
    public SECondition duplicate() throws CloneNotSupportedException {
        SECondition condition = new SECondition(this.name + "_副本");
        if(item_list != null) {
            for (SEItem item : item_list){
                condition.addItem(item.duplicate());
            }
        }
        if(action_list != null) {
            for (SEAction action : action_list){
                condition.addAction((SEAction) action.duplicate());
            }
        }
        return condition;
    }

    public boolean isFolderAttrs() {
        return isFolderAttrs;
    }

    public void setFolderAttrs(boolean folderAttrs) {
        isFolderAttrs = folderAttrs;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }
//    @Override
//    public int getLayoutType() {
//        int type = R.layout.item_ui_condition_void;
//        if(CUtils.isEmpty(item_list) || CUtils.isEmpty(action_list)){
//            return type;
//        }
//
//        final SEItem item = item_list.get(0);
//        final SEAction action = action_list.get(0);
//
//        if(SEItemImage.class.isInstance(item)){
//
//            if(SEActionImage.class.isInstance(action)){
//                final SEActionImage actionImage = (SEActionImage) action;
//                return actionImage.isOffset() ? R.layout.item_ui_condition_offset_click : R.layout.item_ui_condition_click;
//            }
//
//            if(SEActionTap.class.isInstance(action)){
//                return R.layout.item_ui_condition_position_click;
//            }
//
//            if(SEActionSlide.class.isInstance(action)){
//                return R.layout.item_ui_condition_slide;
//            }
//        }
//
//        if(SEItemColor.class.isInstance(item)){
//            if(SEActionTap.class.isInstance(action)){
//                return R.layout.item_ui_condition_color_click;
//            }
//        }
//
//
//        return R.layout.item_ui_condition_click;
//    }

}
