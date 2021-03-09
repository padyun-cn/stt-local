package com.padyun.scripttools.compat.data;

import com.padyun.scripttoolscore.compatible.data.model.actions.SEAction;
import com.padyun.scripttoolscore.compatible.data.model.item.SEItem;

import androidx.annotation.NonNull;


/**
 * Created by daiepngfei on 8/16/19
 */
public interface ICo<ITEM extends SEItem, ACTION extends SEAction> {

    @NonNull
    ITEM getMainSEItem();

    @NonNull
    ACTION getMainSEAction();

    /**
     *
     * @param item
     */
    void setMainSEItem(@NonNull ITEM item);

    /**
     *
     * @param action
     */
    void setMainSEAction(@NonNull ACTION action);

}
