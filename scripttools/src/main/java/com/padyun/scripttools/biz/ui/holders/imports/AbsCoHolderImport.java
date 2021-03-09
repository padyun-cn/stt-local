package com.padyun.scripttools.biz.ui.holders.imports;

/**
 * Created by daiepngfei on 6/19/19
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.View;
import android.widget.ImageView;

import com.mon.ui.buildup.CvImageCheckbox;
import com.mon.ui.list.compat.adapter.BaseRecyclerHolder;
import com.mon.ui.list.compat.adapter.BaseV2RecyclerAdapter;
import com.mon.ui.list.compat.adapter.IBaseRecyclerModel;
import com.padyun.scripttools.R;
import com.padyun.scripttools.biz.ui.data.AbsCoImportCondition;
import com.padyun.scripttools.biz.ui.fragment.ViewImportDetailList;
import com.padyun.scripttools.common.utils.CoBitmapWorker;
import com.uls.utilites.common.IReject;

import androidx.annotation.NonNull;

@SuppressWarnings("WeakerAccess")
public abstract class AbsCoHolderImport<B extends AbsCoImportCondition> extends BaseRecyclerHolder<B> {
    private B b;
    private CvImageCheckbox checkbox;

    public AbsCoHolderImport(@NonNull View itemView) {
        super(itemView);
        init(itemView);
        checkbox = itemView.findViewById(R.id.checkbox);
    }


    public abstract void init(View root);

    @Override
    protected void set(@NonNull Activity act, @NonNull BaseV2RecyclerAdapter adapter, @NonNull B item, int position) {
        this.b = item;
        onSet(act, adapter, item, position);
        if(checkbox != null){
            checkbox.setCheckWithoutNotifing(item.isChecked());
            checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                item.setChecked(isChecked);
                sendItemMessage(ViewImportDetailList.ON_CHECKED_CHANGED);
            });
        }
    }

    protected abstract void onSet(@NonNull Activity act, @NonNull BaseV2RecyclerAdapter adapter, @NonNull B item, int position);


    protected B getCurItem() {
        return b;
    }

    protected <T extends IBaseRecyclerModel> void runWhenCurItem(T item, Runnable run) {
        if (item == getCurItem() && run != null) {
            run.run();
        }
    }

    protected <T extends IBaseRecyclerModel> void setImageWhenCurItem(ImageView img, Bitmap bitmap, T item) {
        runWhenCurItem(item, () -> {
            if (img != null) {
                img.setImageBitmap(bitmap);
            }
        });
    }

    protected <T extends AbsCoImportCondition> void loadIdentifyImage(T t, String path, ImageView imageView) {
        loadIdentifyImage(t, path, imageView, null);
    }

    protected <T extends AbsCoImportCondition> void loadIdentifyImage(T t, String path, ImageView imageView, IReject reject) {
        CoBitmapWorker.lruLoad(path, bm -> setImageWhenCurItem(imageView, bm, t), reject);
    }


    protected <T extends AbsCoImportCondition> void loadCroppedImageFromOrigin(T t, String path, Rect rect, ImageView imageView) {
        loadCroppedImageFromOrigin(t, path, rect, imageView, null);
    }

    protected <T extends AbsCoImportCondition> void loadCroppedImageFromOrigin(T t, String path, Rect rect, ImageView imageView, IReject reject) {
        CoBitmapWorker.lruLoad(path, rect, bm -> setImageWhenCurItem(imageView, bm, t), reject);
    }

}
