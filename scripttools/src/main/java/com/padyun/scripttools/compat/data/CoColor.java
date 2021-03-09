package com.padyun.scripttools.compat.data;

import android.graphics.Point;
import android.graphics.Rect;
import androidx.annotation.NonNull;

import com.uls.utilites.un.Useless;
import com.padyun.scripttools.R;
import com.padyun.scripttoolscore.compatible.data.model.actions.SEActionTap;
import com.padyun.scripttoolscore.compatible.data.model.coord.SECoordFixed;
import com.padyun.scripttoolscore.compatible.data.model.item.SEItemColor;
import com.padyun.scripttoolscore.compatible.data.model.range.SERangeSize;


/**
 * Created by daiepngfei on 8/19/19
 */
public class CoColor extends AbsCoConditon implements ICo<SEItemColor,SEActionTap> {
    private int co_color;
    private Rect co_rect;

    public String getCo_origin_path() {
        return co_origin_path;
    }

    public void setCo_origin_path(String co_origin_path) {
        this.co_origin_path = co_origin_path;
    }

    private String co_origin_path;
   /* public CoColor(SEItemColor color){
        setCo_color(color.getColor());
        setCo_rect(color.getBounds());
        setSEItem(color);
        SEActionTap tap = new SEActionTap();
        tap.setCoord(color.getCoord());
        tap.setRange(color.getRange());
        setSEAction(tap);
    }*/

    public CoColor(int color, String path, Rect rect){
        this.co_rect = rect;
        this.co_color = color;
        this.co_origin_path = path;
        SEItemColor itemColor = new SEItemColor();
        itemColor.setPixel(new Point(rect.centerX(), rect.centerY()));
        SECoordFixed fixed = new SECoordFixed(rect.left, rect.top);
        SERangeSize rangeSize = new SERangeSize(rect.width(), rect.height());
        itemColor.setOriginal(path);
        itemColor.setColor(color);
        itemColor.setCoord(fixed);
        itemColor.setRange(rangeSize);
        setMainSEItem(itemColor);
        SEActionTap tap = new SEActionTap();
        tap.setRange(rangeSize);
        tap.setCoord(fixed);
        setMainSEAction(tap);
    }

    void setCo_color(int color) {
        this.co_color = color;
    }

    void setCo_rect(Rect rect) {
        this.co_rect = rect;
    }

    @NonNull
    @Override
    public SEItemColor getMainSEItem() {
        return (SEItemColor) getItem_list().get(0);
    }

    @NonNull
    @Override
    public SEActionTap getMainSEAction() {
        return (SEActionTap) getAction_list().get(0);
    }

    @Override
    public void setMainSEItem(@NonNull SEItemColor item) {
        Useless.clear(getItem_list());
        addItem(item);
    }

    @Override
    public void setMainSEAction(@NonNull SEActionTap action) {
        Useless.clear(getAction_list());
        addAction(action);
    }

    @Override
    protected void onSettingCoTimeout(int co_timeout) {
        getMainSEAction().setDelay(co_timeout);
    }

    @Override
    public String getCo_type() {
        return "color";
    }

    @Override
    public int getTypeItemLayoutId() {
        return  R.layout.item_ui_condition_color_click;
    }
}
