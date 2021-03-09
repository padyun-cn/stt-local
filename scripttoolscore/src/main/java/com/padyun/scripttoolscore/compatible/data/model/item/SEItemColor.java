package com.padyun.scripttoolscore.compatible.data.model.item;


import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import androidx.annotation.NonNull;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;

import com.uls.utilites.un.Useless;
import com.padyun.scripttoolscore.compatible.data.model.IDGen;
import com.padyun.scripttoolscore.compatible.data.model.coord.SECoord;
import com.padyun.scripttoolscore.compatible.data.model.coord.SECoordFixed;
import com.padyun.scripttoolscore.compatible.data.model.range.SERange;
import com.padyun.scripttoolscore.compatible.data.model.range.SERangeSize;

import java.util.List;

/**
 * Created by daiepngfei on 1/16/19
 */
@SuppressWarnings("FieldCanBeLocal")
public class SEItemColor extends SEItem implements Cloneable {
    /**
     * type : color
     * state :
     * relation :
     * timeout :
     * color :
     * coord : {}
     * range : {}
     */
    private final String type = TYPE_COLOR;
    private int state = STATE_UNCHANGE;
    private int timeout = 1000;
    private int color;
    private int condtionCount;
    private int colorNumber;
    private int pixelX, pixelY;
    /*
     * 1： state 相关：当state为:
     *       {STATE_MORE}
     *       或者
     *       {STATE_LESS}
     *       有效
     * 2： 设定这个<阈值>，针对"当前每一帧"判断是否当前帧包含的颜色数量'多于'或者'少于'这个阈值，多于或者少于用state来标示
     * */
    private int colorNumberThreshold;
    /*
     * 1： state 相关：当state为:
     *       {STATE_UNCHANGE}
     *       或者
     *       {STATE_CHANGE}
     *       有效
     * 2： 设定这个<容错系数>，
     *     用于判断在一段时间内，颜色数量是否有变化的容错系数
     *     是否颜色数量变化 增多/减少 超过这个 <容错系数>
     * */
    private int colorNumberFloating;
    private float sim = 0.8f;
    private SECoord coord;
    private SERange range;
    private String name;
    private String original;

    public int setColorNumberCurrentWithState(int num) {
        int r = 0;
        switch (state) {
            case STATE_UNCHANGE:
            case STATE_CHANGE:
                setColorNumberFloating(num);
                break;
            case STATE_MORE:
            case STATE_LESS:
                setColorNumberThreshold(num);
                break;
            default:
        }
        return r;
    }

    public int getColorNumberCurrentWithState() {
        int r = 0;
        switch (state) {
            case STATE_UNCHANGE:
            case STATE_CHANGE:
                r = getColorNumberFloating();
                break;
            case STATE_MORE:
            case STATE_LESS:
                r = getColorNumberThreshold();
                break;
            default:
        }
        return r;
    }

    public int getColorNumberThreshold() {
        return colorNumberThreshold;
    }

    public void setColorNumberThreshold(int colorNumberThreshold) {
        this.colorNumberThreshold = colorNumberThreshold;
    }

    public int getColorNumberFloating() {
        return colorNumberFloating;
    }

    public void setColorNumberFloating(int colorNumberFloating) {
        this.colorNumberFloating = colorNumberFloating;
    }

    public int getColorNumber() {
        return colorNumber;
    }

    public void setColorNumber(int colorNumber) {
        this.colorNumber = colorNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id = IDGen.genTmsStrRandom("color");

    private void newId() {
        id = IDGen.genTmsStrRandom("color");
    }

    public Rect getBounds() {
        Rect rect = new Rect();
        if (coord instanceof SECoordFixed) {
            rect.left = ((SECoordFixed) coord).getX();
            rect.top = ((SECoordFixed) coord).getY();
        }
        if (range instanceof SERangeSize) {
            rect.right = rect.left + ((SERangeSize) range).getW();
            rect.bottom = rect.top + ((SERangeSize) range).getH();
        }
        return rect.isEmpty() ? null : rect;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    @Override
    protected String getSeType() {
        return "item_color";
    }

    public String getType() {
        return type;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public float getSim() {
        return sim;
    }

    public void setSim(float sim) {
        this.sim = sim;
    }

    public int getCondtionCount() {
        return condtionCount;
    }

    public void setCondtionCount(int condtionCount) {
        this.condtionCount = condtionCount;
    }


    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public SECoord getCoord() {
        return coord;
    }

    public void setCoord(SECoord coord) {
        this.coord = coord;
    }

    public SERange getRange() {
        return range;
    }

    public void setRange(SERange range) {
        this.range = range;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public SEItem duplicate() throws CloneNotSupportedException {
        return seClone(false);
    }

    public SEItemColor seClone(boolean deep) {
        SEItemColor color = null;
        try {
            color = (SEItemColor) this.clone();
            if (color != null) {
                if (deep) {
                    color.fromItemWitTid(this);
                    color.setId(this.getId());
                } else {
                    color.newTID();
                    color.newId();
                }
                if (this.range != null) {
                    color.setRange(this.range.duplicate());
                }
                if (this.coord != null) {
                    color.setCoord(this.coord.duplicate());
                }
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return color;
    }

    public void merge(SEItemColor color) {
        if (color == null || !Useless.equals(color.getId(), id)) return;
        this.setColor(color.getColor());
        this.setRange(color.getRange());
        this.setCoord(color.getCoord());
        this.setName(color.getName());
        this.setId(color.getId());
        this.setColorNumber(color.getColorNumber());
        this.setColorNumberFloating(color.getColorNumberFloating());
        this.setColorNumberThreshold(color.getColorNumberThreshold());
        this.setCondtionCount(color.getCondtionCount());
        this.setOriginal(color.getOriginal());
        this.setSim(color.getSim());
        this.setState(color.getState());
        this.setTimeout(color.getTimeout());
        this.setRelation(color.getRelation());
        this.setPixel(color.getPixel());
    }

    public Point getPixel() {
        return new Point(this.pixelX, this.pixelY);
    }

    public void setPixel(Point pixel) {
        if(pixel != null) {
            this.pixelX = pixel.x;
            this.pixelY = pixel.y;
        }
    }

    //    public SEItemColor duplicate() {
//        Object obj = null;
//        try {
//            obj = clone();
//        } catch (CloneNotSupportedException e) {
//            e.printStackTrace();
//        }
//        return getClass().isInstance(obj) ? getClass().objectCasting(obj) : null;
//    }
}
