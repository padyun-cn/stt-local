package com.padyun.scripttoolscore.compatible.plugin;


import com.google.gson.Gson;
import com.uls.utilites.un.Useless;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RuntimeInfo {

    private static final String KEY_SCENE_ID = "scene_id" ;
    private static final String KEY_CONDITION_ID = "condition_id";
    private JSONObject mInfo ;
    private int mSceneId ;
    private String mConditionId ;
    private Entity entity;
    /**
     * scene_id : 1
     * condition_id : xxx
     * images : [{"x":"1","y":"2","sim":"0.9","name":"name"}]
     * brains : [{"id":2,"value":""}]
     */

    private int scene_id;
    private String condition_id;


    public RuntimeInfo(){
        mInfo = new JSONObject();
    }

    public RuntimeInfo(String info) throws JSONException {
        mInfo = new JSONObject(Useless.nonNullStr(info));
        mSceneId = mInfo.optInt(KEY_SCENE_ID,-1) ;
        mConditionId = mInfo.optString(KEY_CONDITION_ID) ;
        entity = parseInfo(info);
    }

    private Entity parseInfo(String info) {
        // System.out.println("SELG: " + info);
        try {
            JSONObject object = new JSONObject(info);
            Entity entity = new Entity();
            entity.scene_id = object.optInt("scene_id");
            entity.condition_id = object.optString("condition_id");
            entity.is_trigger = object.optBoolean("is_trigger");
            JSONArray array = object.optJSONArray("items");
            if(array != null) {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject o = array.optJSONObject(i);
                    if(o != null){
                        final String type = o.optString("type");
                        switch (type){
                            case "brain":
                                entity.addBrains(new Gson().fromJson(o.toString(), BrainsBean.class));
                                break;
                            case "image":
                                entity.addImage(new Gson().fromJson(o.toString(), ImagesBean.class));
                                break;
                            case "color":
                                entity.addColors(new Gson().fromJson(o.toString(), ColorsBean.class));
                                break;
                            default:
                        }
                    }
                }
            }
            return entity;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void setSceneId(int id){
            mSceneId = id ;
    }
    public void resetConditionId(){
        mConditionId = "NULL" ;
    }
    public void resetSceneId(){
        mSceneId = -1 ;
    }
    public void setConditionId(String id){
        mConditionId = id ;
    }

    public int getSceneId(){
        return mSceneId ;
    }
    public String getConditionId(){
        return mConditionId ;
    }

    public String toJson(){
        try {
            mInfo.put(KEY_SCENE_ID, mSceneId) ;
            mInfo.put(KEY_CONDITION_ID, mConditionId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mInfo.toString() ;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public int getScene_id() {
        return scene_id;
    }

    public void setScene_id(int scene_id) {
        this.scene_id = scene_id;
    }

    public String getCondition_id() {
        return condition_id;
    }

    public void setCondition_id(String condition_id) {
        this.condition_id = condition_id;
    }



    public static class Entity {
        private int scene_id;
        private String condition_id;
        private boolean is_trigger;
        private List<ImagesBean> images;
        private List<BrainsBean> brains;
        private List<ColorsBean> colors;

        public void setScene_id(int scene_id) {
            this.scene_id = scene_id;
        }

        public void setCondition_id(String condition_id) {
            this.condition_id = condition_id;
        }

        public boolean isIs_trigger() {
            return is_trigger;
        }

        public void setIs_trigger(boolean is_trigger) {
            this.is_trigger = is_trigger;
        }

        public List<ColorsBean> getColors() {
            return colors;
        }

        public void setColors(List<ColorsBean> colors) {
            this.colors = colors;
        }

        public int getScene_id() {
            return scene_id;
        }


        public String getCondition_id() {
            return condition_id;
        }

        public List<ImagesBean> getImages() {
            return images;
        }

        public void setImages(List<ImagesBean> images) {
            this.images = images;
        }

        public List<BrainsBean> getBrains() {
            return brains;
        }

        public void setBrains(List<BrainsBean> brains) {
            this.brains = brains;
        }


        public void addImage(ImagesBean b){
            if(b == null) return;
            if(images == null) images = new ArrayList<>();
            images.add(b);
        }

        public void addBrains(BrainsBean b){
            if(b == null) return;
            if(brains == null) brains = new ArrayList<>();
            brains.add(b);
        }

        public void addColors(ColorsBean b){
            if(b == null) return;
            if(colors == null) colors = new ArrayList<>();
            colors.add(b);
        }


    }

    public static class ColorsBean {
        String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        int count;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    public static class ImagesBean {
        /**
         * x : 1
         * y : 2
         * sim : 0.9
         * name : name
         */
        private String id;
        private String x;
        private String y;
        private String sim;
        private String name;

        public String getX() {
            return x;
        }

        public void setX(String x) {
            this.x = x;
        }

        public String getY() {
            return y;
        }

        public void setY(String y) {
            this.y = y;
        }

        public String getSim() {
            return sim;
        }

        public void setSim(String sim) {
            this.sim = sim;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class BrainsBean {
        /**
         * id : 2
         * value :
         */

        private int id;
        private String value;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
