package com.padyun.scripttools.biz.ui.holders;

/**
 * Created by daiepngfei on 6/19/19
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mon.ui.list.compat.adapter.BaseRecyclerHolder;
import com.mon.ui.list.compat.adapter.BaseV2RecyclerAdapter;
import com.mon.ui.list.compat.adapter.IBaseRecyclerModel;
import com.padyun.scripttools.R;
import com.padyun.scripttools.common.utils.CoBitmapWorker;
import com.padyun.scripttools.compat.data.AbsCoConditon;
import com.padyun.scripttools.compat.data.AbsCoImage;
import com.padyun.scripttools.content.la.ISEActivityAttatchable;
import com.padyun.scripttools.content.la.ISEActivityAttatcher;
import com.padyun.scripttoolscore.compatible.data.model.brain.SEBrainAlarm;
import com.uls.utilites.common.IReject;

import androidx.annotation.NonNull;

@SuppressWarnings("WeakerAccess")
public abstract class AbsCoHolder<B extends IBaseRecyclerModel> extends BaseRecyclerHolder<B> implements ISEActivityAttatcher, ISEActivityAttatchable {
    private final ISEActivityAttatchable attachable;
    private boolean mIsRecycled;
    private B b;


    protected void maySetBrainAlarm(AbsCoConditon conditon){
        SEBrainAlarm alarm = conditon.getCo_brainAlarm();
        View root = getRootView();
        View mLayoutAlarm = root.findViewById(R.id.layoutScriptAlarm);
        if(alarm != null) {
            TextView mTextAlarm = root.findViewById(R.id.textScriptAlarm);
            mLayoutAlarm.setVisibility(View.VISIBLE);
            String sb = alarm.getStartHour() + ":" + alarm.getStarMin() +
                    "\n" +
                    alarm.getEndHour() + ":" + alarm.getEndMin();
            mTextAlarm.setText(sb);
        } else {
            mLayoutAlarm.setVisibility(View.GONE);
        }
    }

    @Override
    public void attacherStartActivityForResult(Intent intent, int requestCode) {
        if (getAttachable() != null) {
            getAttachable().attacherStartActivityForResult(intent, requestCode);
        }
    }

    public AbsCoHolder(@NonNull View itemView) {
        this(itemView, null);
    }

    public AbsCoHolder(@NonNull View itemView, ISEActivityAttatchable attachable) {
        super(itemView);
        this.attachable = attachable;
        init(itemView);
    }

    public void attach(ISEActivityAttatcher iala) {
        if (this.attachable != null && iala != null) this.attachable.attach(iala);
    }

    public void dettach(ISEActivityAttatcher iala) {
        if (this.attachable != null && iala != null) this.attachable.dettach(iala);
    }

    public abstract void init(View root);

    @Override
    protected void set(@NonNull Activity act, @NonNull BaseV2RecyclerAdapter adapter, @NonNull B item, int position) {
        this.b = item;
        if(item instanceof AbsCoConditon) {
             maySetBrainAlarm((AbsCoConditon) item);
        }
        onSet(act, adapter, item, position);
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


    protected ISEActivityAttatchable getAttachable() {
        return attachable;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    @Override
    public boolean isRecyled() {
        return mIsRecycled;
    }

    public void onHolderMenuClick(BaseV2RecyclerAdapter adapter, int position) {

    }

    protected <T extends AbsCoImage> void loadIdentifyImage(T t, ImageView imageView) {
        loadIdentifyImage(t, imageView, null);
    }

    protected <T extends AbsCoImage> void loadIdentifyImage(T t, ImageView imageView, IReject reject) {
        CoBitmapWorker.lruLoad(t.getIdentifyImage().getImageCropPath(), bm -> setImageWhenCurItem(imageView, bm, t), reject);
    }

    protected <T extends AbsCoImage> void loadCroppedImageFromOrigin(T t, Rect rect, ImageView imageView) {
        loadCroppedImageFromOrigin(t, rect, imageView, null);
    }

    protected <T extends AbsCoImage> void loadCroppedImageFromOrigin(T t, Rect rect, ImageView imageView, IReject reject) {
        CoBitmapWorker.lruLoad(t.getIdentifyImage().getImageOriginal(), rect, bm -> setImageWhenCurItem(imageView, bm, t), reject);
    }

}
