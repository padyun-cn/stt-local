package com.padyun.scripttools.biz.ui.data;


import com.mon.ui.list.compat.adapter.IBaseRecyclerModel;
import com.padyun.scripttools.R;

/**
 * Created by daiepngfei on 6/24/19
 */
public class VHMText implements IBaseRecyclerModel {
    private String content;
    private Integer textColor;
    private boolean disable;

    public VHMText(String content) {
        this.content = content;
    }

    public VHMText(String content, Integer textColor) {
        this.content = content;
        this.textColor = textColor;
    }

    public VHMText(String content, Integer textColor, boolean disable) {
        this.content = content;
        this.textColor = textColor;
        this.disable = disable;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getTextColor() {
        return textColor;
    }

    public void setTextColor(Integer textColor) {
        this.textColor = textColor;
    }


    @Override
    public int getTypeItemLayoutId() {
        return R.layout.item_dg_v2_text;
    }
}
