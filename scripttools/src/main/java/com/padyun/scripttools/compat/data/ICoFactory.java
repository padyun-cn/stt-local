package com.padyun.scripttools.compat.data;


import com.padyun.scripttoolscore.compatible.data.model.SECondition;
import com.padyun.scripttoolscore.compatible.data.model.SEImage;
import com.padyun.scripttoolscore.compatible.data.model.actions.SEActionImage;
import com.padyun.scripttoolscore.compatible.data.model.item.SEItemImage;

/**
 * Created by daiepngfei on 8/16/19
 */
public class ICoFactory {

    public static SECondition createSimpleConditionClick(SEImage image) {
        SECondition condition = new SECondition();
        SEItemImage itemImage = createCommonItemImagee(image);
        SEActionImage actionImage = createCommonActionImagee(image);
        condition.addItem(itemImage);
        condition.addAction(actionImage);
        return condition;
    }

    public static void createSimpleConditionTap(SEImage image){

    }

    public static void setConditonOffset(SECondition condition, int offsetX, int offsetY) {
        try {
            SEActionImage actionImage = (SEActionImage) condition.getAction_list().get(0);
            actionImage.setShift_x(offsetX);
            actionImage.setShift_y(offsetY);
        } catch (Exception e){
            // do nothing
        }
    }

    private static SEActionImage createCommonActionImagee(SEImage image) {
        SEActionImage itemImage = new SEActionImage();
        itemImage.setImage_info(image.getCropFileName());
        itemImage.setImage_detail(image);
        return itemImage;
    }

    private static SEItemImage createCommonItemImagee(SEImage image) {
        SEItemImage itemImage = new SEItemImage();
        itemImage.setImage_info(image.getCropFileName());
        itemImage.setImage_detail(image);
        return itemImage;
    }
}
