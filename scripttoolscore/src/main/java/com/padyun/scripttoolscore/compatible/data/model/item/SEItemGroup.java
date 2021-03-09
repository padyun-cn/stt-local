package com.padyun.scripttoolscore.compatible.data.model.item;



import java.util.ArrayList;
import java.util.List;

/**
 * Created by daiepngfei on 1/16/19
 */
@SuppressWarnings("FieldCanBeLocal")
public class SEItemGroup extends SEItem {

    /**
     * type : group
     * name :
     * relation :
     */

    private final String type = TYPE_GROUP;
    private String name;
    private List<SEItem> list;

    @Override
    protected String getSeType() {
        return "item_group";
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SEItem> getList() {
        return list;
    }

    public void setList(List<SEItem> list) {
        this.list = list;
    }

    @Override
    public SEItem duplicate() throws CloneNotSupportedException {
        SEItemGroup group = (SEItemGroup) clone();
        group.newTID();
        group.list = new ArrayList<>();
        for(SEItem item : list){
            group.list.add(item.duplicate());
        }
        return group;
    }
}
