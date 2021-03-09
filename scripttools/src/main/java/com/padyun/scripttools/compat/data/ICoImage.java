package com.padyun.scripttools.compat.data;


import com.padyun.scripttoolscore.compatible.data.model.SEImage;
import com.padyun.scripttoolscore.compatible.data.model.actions.SEAction;
import com.padyun.scripttoolscore.compatible.data.model.item.SEItem;

/**
 * Created by daiepngfei on 8/16/19
 */
public interface ICoImage<ITEM extends SEItem, ACTION extends SEAction> extends ICo<ITEM, ACTION> {
    /**
     *
     * @return
     */
    SEImage getIdentifyImage();

    /**
     *
     * @param img
     */
    void setIdentifyImage(SEImage img);
}
