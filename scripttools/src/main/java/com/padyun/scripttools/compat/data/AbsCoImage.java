package com.padyun.scripttools.compat.data;

import android.graphics.Rect;

import com.padyun.scripttoolscore.compatible.data.model.SEImage;
import com.padyun.scripttoolscore.compatible.data.model.actions.SEAction;
import com.padyun.scripttoolscore.compatible.data.model.item.SEItem;
import com.padyun.scripttoolscore.compatible.data.model.item.SEItemImage;
import com.uls.utilites.un.Useless;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;


/**
 * Created by daiepngfei on 8/16/19
 */
@SuppressWarnings("unchecked")
public abstract class AbsCoImage<T extends SEAction> extends AbsCoConditon implements ICoImage<SEItemImage, T> {

    private SEImage v3Image = null;
    private Rect co_range;

    public Rect getCo_range() {
        return co_range;
    }


    private void setCo_rangeInner(Rect co_range) {
        this.co_range = co_range;
    }

    public void setCo_range(Rect co_range) {
        if (co_range != null) {
            setCo_rangeInner(co_range);
            getMainSEItem().setSearchRect(co_range);
        }
    }

    @NonNull
    protected abstract T genActionWithSEImage(@NonNull SEImage image);

    protected void onCreateItemImage(@NonNull SEItemImage itemImage, @NonNull SEImage image) {
    }

    public AbsCoImage(@NonNull SEImage image) {
        SEItemImage itemImage = new SEItemImage();
        itemImage.setImage_info(image.getCropFileName());
        itemImage.setImage_detail(image);
        onCreateItemImage(itemImage, image);
        setMainSEItem(itemImage);
        setMainSEAction(genActionWithSEImage(image));
        this.v3Image = image;
    }

    public void setItemState(int state) {
        getMainSEItem().setState(state);
    }

    public void setItemTimeout(int timeout) {
        getMainSEItem().setTimeout(timeout);
    }

    @Override
    public SEImage getIdentifyImage() {
        return v3Image;
    }

    @Override
    public void setIdentifyImage(@NonNull SEImage img) {
        this.v3Image = img;
        getMainSEItem().setImage_detail(img);
        getMainSEItem().setImage_info(img.getCropFileName());
    }

    @NonNull
    @Override
    public SEItemImage getMainSEItem() {
        return (SEItemImage) getItem_list().get(0);
    }

    @NonNull
    @Override
    public T getMainSEAction() {
        return (T) getAction_list().get(0);
    }

    @Override
    public void setMainSEItem(@NonNull SEItemImage item) {
        if (getItem_list() == null) {
            setItem_list(new ArrayList<>());
        }
        if (getItem_list().size() == 0) {
            getItem_list().add(item);
        } else {
            getItem_list().set(0, item);
        }
        setCo_rangeInner(item.getSearchRangeRect());
    }

    @Override
    public void setMainSEAction(@NonNull T action) {
        Useless.clear(getAction_list());
        addAction(action);
    }

    /**
     * @param nonExsitExtra
     */
    public void setNonExsitExtra(SEImage nonExsitExtra) {

        final List<SEItem> items = getItem_list();
        if(items == null || items.size() == 0){
            return;
        }

        if(nonExsitExtra == null) {
            if(items.size() == 2){
                items.remove(1);
            }
            return;
        }

        final SEItemImage image = new SEItemImage();
        image.setState(SEItemImage.STATE_WITHOUT);
        image.setImage_detail(nonExsitExtra);

        if(items.size() >= 1){
            items.add(image);
        } else {
            items.set(1, image);
        }

    }

    public void mergeBaseInfo(AbsCoImage coImage) {
        if (coImage != null) {
            this.setCo_range(coImage.getCo_range());
            this.setItemRelation(coImage.getMainSEItem().getRelation());
            this.setItemState(coImage.getMainSEItem().getState());
            this.setItemTimeout(coImage.getMainSEItem().getTimeout());
            this.setCo_brain_count(coImage.getCo_brain_count());
        }
    }


    public SEImage getNonExsitExtraImage() {
        SEItemImage no = getNonExistExtraItemImage();
        return  no != null ? no.getSEImage() : null;
    }

    public void setNonExistExtraItemImage(SEItemImage image) {
        if (image == null) {
            return;
        }
        List<SEItem> items = getItem_list();
        if (items != null) {
            if (items.size() == 1) {
                items.add(image);
            } else if (items.size() == 2) {
                items.set(1, image);
            }
        }
    }

    public SEItemImage getNonExistExtraItemImage() {
        SEItemImage image = null;

        List<SEItem> items = getItem_list();
        if (items != null && items.size() == 2 && items.get(1) instanceof SEItemImage) {
            SEItemImage itemImage = (SEItemImage) items.get(1);
            if (itemImage.getState() == SEItem.STATE_WITHOUT) {
                image = itemImage;
            }
        }

        return image;
    }

    public boolean hasNonExist() {
        return getNonExistExtraItemImage() != null;
    }

    public void setItemRelation(int optInt) {
        getMainSEItem().setRelation(optInt);
    }
}
