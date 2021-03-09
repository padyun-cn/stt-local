package com.padyun.scripttoolscore.compatible.data.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.padyun.scripttoolscore.compatible.ScriptCons;
import com.padyun.scripttoolscore.compatible.data.model.IDGen;
import com.padyun.scripttoolscore.compatible.data.model.ImageInfo;
import com.padyun.scripttoolscore.compatible.data.model.SEBaseType;
import com.padyun.scripttoolscore.compatible.data.model.SECondition;
import com.padyun.scripttoolscore.compatible.data.model.SEImage;
import com.padyun.scripttoolscore.compatible.data.model.SEScene;
import com.padyun.scripttoolscore.compatible.data.model.SEScript;
import com.padyun.scripttoolscore.compatible.data.model.actions.SEAction;
import com.padyun.scripttoolscore.compatible.data.model.actions.SEActionBrain;
import com.padyun.scripttoolscore.compatible.data.model.actions.SEActionFinish;
import com.padyun.scripttoolscore.compatible.data.model.actions.SEActionImage;
import com.padyun.scripttoolscore.compatible.data.model.actions.SEActionInput;
import com.padyun.scripttoolscore.compatible.data.model.actions.SEActionSlide;
import com.padyun.scripttoolscore.compatible.data.model.actions.SEActionTap;
import com.padyun.scripttoolscore.compatible.data.model.actions.SEActionVoid;
import com.padyun.scripttoolscore.compatible.data.model.brain.SEBrain;
import com.padyun.scripttoolscore.compatible.data.model.brain.SEBrainAlarm;
import com.padyun.scripttoolscore.compatible.data.model.brain.SEBrainCounter;
import com.padyun.scripttoolscore.compatible.data.model.brain.SEBrainJudge;
import com.padyun.scripttoolscore.compatible.data.model.brain.SEBrainTimer;
import com.padyun.scripttoolscore.compatible.data.model.coord.SECoord;
import com.padyun.scripttoolscore.compatible.data.model.coord.SECoordFixed;
import com.padyun.scripttoolscore.compatible.data.model.coord.SECoordImage;
import com.padyun.scripttoolscore.compatible.data.model.item.SEItem;
import com.padyun.scripttoolscore.compatible.data.model.item.SEItemBrain;
import com.padyun.scripttoolscore.compatible.data.model.item.SEItemColor;
import com.padyun.scripttoolscore.compatible.data.model.item.SEItemGroup;
import com.padyun.scripttoolscore.compatible.data.model.item.SEItemImage;
import com.padyun.scripttoolscore.compatible.data.model.item.SEItemUser;
import com.padyun.scripttoolscore.compatible.data.model.range.SERange;
import com.padyun.scripttoolscore.compatible.data.model.range.SERangeImage;
import com.padyun.scripttoolscore.compatible.data.model.range.SERangeSize;
import com.padyun.scripttoolscore.compatible.plugin.ImageModule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import com.uls.utilites.un.Useless;

/**
 * Created by daiepngfei on 1/16/19
 */
public class SEModelUtil {
    private static final HashMap<String, Class<? extends SEBaseType>> sRegisteredClasses = new HashMap<>();
    private static final Gson sGson;

    public static void main(String[] args) {
//        SETypeBeanParser.fromJson("", String.class);
//        SECoord coord = new SECoordFixed();
//        String json = new Gson().toJson(coord);
//        System.out.println("json => " + json);
//        SECoord cord = fromJson(json, SECoord.class);
//        List<SEScene> sceneList = fromJson(json, new TypeToken<List<SEScene>>(){}.getType()) ;
//        SEScript script = fromJson("{\n" +
//                "\t\"brain_list\": [{\n" +
//                "\t\t\"threshold\": 8,\n" +
//                "\t\t\"type\": \"counter\",\n" +
//                "\t\t\"id\": 1,\n" +
//                "\t\t\"name\": \"jishu\",\n" +
//                "\t\t\"se_type\": \"brain_counter\"\n" +
//                "\t}, {\n" +
//                "\t\t\"defaultValue\": true,\n" +
//                "\t\t\"type\": \"judge\",\n" +
//                "\t\t\"id\": 2,\n" +
//                "\t\t\"name\": \"bool\",\n" +
//                "\t\t\"se_type\": \"brain_judge\"\n" +
//                "\t}],\n" +
//                "\t\"scenes\": [{\n" +
//                "\t\t\"condition_list\": [{\n" +
//                "\t\t\t\"condition_id\": \"condition_id_1548840161847_3\",\n" +
//                "\t\t\t\"item_list\": [{\n" +
//                "\t\t\t\t\"image_detail\": {\n" +
//                "\t\t\t\t\t\"fileName\": \"crop_1548840181769.png\",\n" +
//                "\t\t\t\t\t\"info\": {\n" +
//                "\t\t\t\t\t\t\"flag\": 1,\n" +
//                "\t\t\t\t\t\t\"h\": 275,\n" +
//                "\t\t\t\t\t\t\"maxval\": 255,\n" +
//                "\t\t\t\t\t\t\"sim\": 80,\n" +
//                "\t\t\t\t\t\t\"threshold\": 0,\n" +
//                "\t\t\t\t\t\t\"type\": 0,\n" +
//                "\t\t\t\t\t\t\"w\": 639,\n" +
//                "\t\t\t\t\t\t\"x\": 0,\n" +
//                "\t\t\t\t\t\t\"y\": 0\n" +
//                "\t\t\t\t\t},\n" +
//                "\t\t\t\t\t\"name\": \"nxjx\",\n" +
//                "\t\t\t\t\t\"original\": \"/storage/emulated/0/Android/data/com.padyun.summer/cache/image_cache/screen/screen_1548840181769.png\",\n" +
//                "\t\t\t\t\t\"cropPath\": \"/storage/emulated/0/Android/data/com.padyun.summer/cache/image_cache/crop/crop_1548840181769.png\"\n" +
//                "\t\t\t\t},\n" +
//                "\t\t\t\t\"state\": 0,\n" +
//                "\t\t\t\t\"timeout\": 0,\n" +
//                "\t\t\t\t\"type\": \"image\",\n" +
//                "\t\t\t\t\"TID\": 1548840181769,\n" +
//                "\t\t\t\t\"relation\": 1,\n" +
//                "\t\t\t\t\"se_type\": \"item_image\"\n" +
//                "\t\t\t}]\n" +
//                "\t\t}, {\n" +
//                "\t\t\t\"condition_id\": \"condition_id_1548840214770_132\",\n" +
//                "\t\t\t\"item_list\": [{\n" +
//                "\t\t\t\t\"id\": 1,\n" +
//                "\t\t\t\t\"type\": \"brain\",\n" +
//                "\t\t\t\t\"TID\": 1548840218304,\n" +
//                "\t\t\t\t\"relation\": 1,\n" +
//                "\t\t\t\t\"se_type\": \"item_brain\"\n" +
//                "\t\t\t}]\n" +
//                "\t\t}],\n" +
//                "\t\t\"name\": \"haha\",\n" +
//                "\t\t\"scene_condition\": {\n" +
//                "\t\t\t\"condition_id\": \"condition_id_1548840161847_611\"\n" +
//                "\t\t},\n" +
//                "\t\t\"scene_id\": 0\n" +
//                "\t}]\n" +
//                "}", SEScript.class);
//        script.getBrain_list();
        /*LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("123", "123");
        map.put("234", "234");
        final String json  =new Gson().toJson(map);
        System.out.println(json);
        LinkedHashMap<String, String> s = new Gson().fromJson(json, LinkedHashMap.class);
        System.out.println(s);*/

        SEScript seScript = new SEScript(IDGen.genTmsStrRandom("script_"));
        SEScene seScene = new SEScene();
        SECondition condition = new SECondition();
        seScene.addCondition(condition);
        SEAction action = new SEActionVoid();
        condition.addAction(action);
        condition.addAction(action);
        condition.addAction(action);
        condition.addAction(action);
        condition.addAction(action);
        SEAction action2 = new SEActionTap();
        condition.addAction(action2);
        seScript.addScene(seScene);
        String s = buildDataString(seScript);
        System.out.println(s);
        SEScript seScript1 = fromJson(s, SEScript.class);
        seScript1.getScenes();

    }

    static {
        initRegisterClasses();
        sGson = getRegisteredGsonBuilder().create();
    }

    private static void initRegisterClasses() {
        // actions
        sRegisteredClasses.put("action_tap", SEActionTap.class);
        sRegisteredClasses.put("action_finish", SEActionFinish.class);
        sRegisteredClasses.put("action_image", SEActionImage.class);
        sRegisteredClasses.put("action_brain", SEActionBrain.class);
        sRegisteredClasses.put("action_slide", SEActionSlide.class);
        sRegisteredClasses.put("action_input", SEActionInput.class);
        sRegisteredClasses.put("action_void", SEActionVoid.class);
        // coord
        sRegisteredClasses.put("coord_fixed", SECoordFixed.class);
        sRegisteredClasses.put("coord_image", SECoordImage.class);
        // range
        sRegisteredClasses.put("range_size", SERangeSize.class);
        sRegisteredClasses.put("range_image", SERangeImage.class);
        // item
        sRegisteredClasses.put("item_brain", SEItemBrain.class);
        sRegisteredClasses.put("item_color", SEItemColor.class);
        sRegisteredClasses.put("item_group", SEItemGroup.class);
        sRegisteredClasses.put("item_image", SEItemImage.class);
        sRegisteredClasses.put("item_user", SEItemUser.class);
        // brain
        sRegisteredClasses.put("brain_counter", SEBrainCounter.class);
        sRegisteredClasses.put("brain_judge", SEBrainJudge.class);
        sRegisteredClasses.put("brain_timer", SEBrainTimer.class);
        sRegisteredClasses.put("brain_alarm", SEBrainAlarm.class);
    }

    @NonNull
    private static GsonBuilder getRegisteredGsonBuilder() {
        GsonBuilder builder = new GsonBuilder();
        register(builder, new TypeToken<SEAction>() {
        }.getType());
        register(builder, new TypeToken<SERange>() {
        }.getType());
        register(builder, new TypeToken<SECoord>() {
        }.getType());
        register(builder, new TypeToken<SEItem>() {
        }.getType());
        register(builder, new TypeToken<SEBrain>() {
        }.getType());
        return builder;
    }

    private static <T extends SEBaseType> void register(GsonBuilder builder, Type t) {
        builder.registerTypeAdapter(t, (JsonDeserializer<T>) (json, typeOfT, context) -> {
            T t1 = null;
            final JsonObject obj = json.getAsJsonObject();
            final String typeName = "se_type";
            if (obj != null && obj.has(typeName)) {
                JsonElement element = obj.get(typeName);
                if (element != null) {
                    Class<? extends SEBaseType> cls = sRegisteredClasses.get(element.getAsString());
                    if (cls != null) t1 = context.deserialize(json, cls);
                }
            }
            return t1;
        });
    }

    public static <T> T fromJson(String json, Type type) {
        return sGson.fromJson(json, type);
    }

    public static String buildDataString(SEScript seScript) {
        GsonBuilder builder = new GsonBuilder();
//        builder.registerTypeAdapter(new TypeToken<List<SEAction>>() {
//        }.getType(), (JsonSerializer<List<SEAction>>) (src, typeOfSrc, codec_context) -> {
//            JsonArray arr = (JsonArray) new GsonBuilder().create().toJsonTree(src);
//            if (arr != null) {
//                Iterator<JsonElement> iterator = arr.iterator();
//                while (iterator.hasNext()) {
//                    ClzCaster.cast(iterator.next(), JsonObject.class, element -> {
//                        if (CMessyUtils.equals(element.get("se_type").getAsString(), "action_void")) {
//                            iterator.remove();
//                        }
//                    });
//                }
//            }
//            return arr;
//        });
        builder.registerTypeAdapter(new TypeToken<SEImage>() {
        }.getType(), (JsonSerializer<SEImage>) (src, typeOfSrc, context) -> {
            JsonObject img = (JsonObject) new GsonBuilder().create().toJsonTree(src);
            img.remove("path");
            img.remove("original");
            img.remove("signature");
            img.remove("name");
            return img;
        });
        builder.registerTypeAdapter(new TypeToken<SEActionSlide>() {
        }.getType(), (JsonSerializer<SEActionSlide>) (src, typeOfSrc, context) -> {
            JsonObject action = (JsonObject) new GsonBuilder().create().toJsonTree(src);
            if(action.has("orignalPath")) {
                action.remove("orignalPath");
            }
            return action;
        });
        return builder/*.setPrettyPrinting()*/.create().toJson(seScript);
    }


    public static ImageModule getImageMoudleFromSEImage(SEImage seImage){
        if(seImage == null || seImage.getImageInfo() == null){
            return null;
        }

        final ImageInfo info = seImage.getImageInfo();
        ImageModule imageModule = new ImageModule();
        imageModule.x = info.x;
        imageModule.y = info.y;
        imageModule.width = info.w;
        imageModule.height = info.h;
        imageModule.flag = info.flag;
        imageModule.thresh = info.threshold;
        imageModule.maxval = info.maxval;
        imageModule.type = info.type;
        imageModule.sim = info.sim;
        imageModule.name = seImage.getCropFileName();
        return imageModule;
    }

    public static SEScene parseCoscriptToSEScript(String json){

        try {
            final JSONObject coScript = new JSONObject(json);
            final JSONObject scene = coScript.getJSONObject("scene");
            final JSONArray conditions = scene.getJSONArray("");

        } catch (JSONException e) {
            e.printStackTrace();
        }


        final SEScene seScene = new SEScene();
        List<SECondition> target = new ArrayList<>();
/*
        Useless.foreach(conditions, cond -> {

            if(cond.isDisabled()){
                return;
            }
            // toCreate se-condition
            SECondition condition = new SECondition();
            condition.setCondition_id(cond.getCondition_id());

            // condition add item
            if(cond.getItem_list() != null) {
                condition.setItem_list(cond.getItem_list());
                //condition.addItem(cond.getItem_list().get(0));
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

            sce.addCondition(condition);

        });
        seScene.addCondition();*/
        return null;
    }

}
