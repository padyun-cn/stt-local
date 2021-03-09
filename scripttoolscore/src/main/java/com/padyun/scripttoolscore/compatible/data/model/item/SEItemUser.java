package com.padyun.scripttoolscore.compatible.data.model.item;

import com.padyun.scripttoolscore.compatible.data.model.IDGen;

/**
 * Created by daiepngfei on 1/16/19
 */
@SuppressWarnings("FieldCanBeLocal")
public class SEItemUser extends SEItem {

    /**
     * type : user
     * relation :
     * key :
     * value :
     */

    private final String type = TYPE_USER;
    private String name;
    private String key = IDGen.genTmsStrRandom("item_user_");
    private String value;

    @Override
    protected String getSeType() {
        return "item_user";
    }

    public String getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public SEItemUser duplicate() {
        SEItemUser user = new SEItemUser();
        user.setKey(key);
        user.setValue(value);
        user.setName(name);
        return user;
    }
}
