package com.mon.ui.list.compat.adapterForViews;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mon.ui.list.compat.adapter.IBaseRecyclerModel;
import com.uls.utilites.content.ILifeAttachable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by daiepngfei on 9/11/17
 */

@SuppressWarnings("WeakerAccess")
public abstract class View_BaseHolder<B extends View_IBaseModel> extends RecyclerView.ViewHolder implements ILifeAttachable {
    public static final int LOADING_SHOW = 123;
    public static final int LOADING_DISSMISS = 321;
    private View mRootView;
    private Handler mMessenger;
    private boolean mIsInialized = false;

    public View_BaseHolder(View itemView) {
        super(itemView);
        mRootView = itemView;
    }

    public void initialize(){
        if(!isInialized()) {
            mIsInialized = true;
            init(mRootView);
        }
    }

    /**
     * Init a rootview from item layout resouce which sameId got by
     * , and find all views you
     * need to generate to variables.
     *
     * @param view
     *         The activity(Context) that the holder lives in
     *
     * @return The initialized view
     */
    protected abstract void init(@NonNull View view);

    /**
     * Everytime's holder-view userSetting in {@link BaseAdapter#getView(int, View, ViewGroup)} will
     * trigger call of this userSetting method, you need handle all userSetting of root view and sub views of this
     * item layout with given item model, the model which inherit from {@link IBaseRecyclerModel} will be pass
     * into this me- thod. Make sure to userSetting the views step by step, and be sure to keep it clearly.
     *
     * @param act
     * @param adapter
     * @param item
     * @param position
     */
    protected abstract void set(@NonNull final Context act, @NonNull final View_BaseAdapter adapter, @NonNull final B item, final int position);

    /**
     *
     * @param act
     * @param adapter
     * @param item
     * @param position
     */
    public void outSet(@NonNull final Context act, @NonNull final View_BaseAdapter adapter, @NonNull final B item, final int position){
        initialize();
        set(act, adapter, item, position);
    }

    /**
     * @param act
     * @param adapter
     * @param item
     * @param position
     * @param l
     */
    void autoSet(@NonNull final Context act,
                 @NonNull final View_BaseAdapter adapter,
                 @NonNull final B item, final int position,
                 @Nullable final View_BaseAdapter.OnItemClickListener l) {

        set(act, adapter, item, position);
        if (l != null) {
            itemView.setOnClickListener(v -> {
                //noinspection unchecked
                l.onItemClick(adapter, item, position);
            });
        }
    }

    /**
     *
     * @return
     */
    protected boolean isInialized() {
        return mIsInialized;
    }

    protected void sendItemMessage(Message message){
        if(mMessenger != null) {
            mMessenger.sendMessage(message);
        }
    }

    protected void sendItemMessage(int what, @NonNull B obj){
        if(mMessenger != null) {
            Message message = mMessenger.obtainMessage();
            message.obj = obj;
            message.what = what;
            message.sendToTarget();
        }
    }

    protected void sendItemMessage(int what, @NonNull Object obj){
        if(mMessenger != null) {
            Message message = mMessenger.obtainMessage();
            message.obj = obj;
            message.what = what;
            message.sendToTarget();
        }
    }

    protected void sendItemMessage(int emptyMessage) {
        if (mMessenger != null) {
            mMessenger.sendEmptyMessage(emptyMessage);
        }
    }

    protected void showLoading(){
        sendItemMessage(LOADING_SHOW);
    }

    protected void dismissLoading() {
        sendItemMessage(LOADING_DISSMISS);
    }

    /**
     *
     * @return
     */
    protected View getRootView() {
        return mRootView;
    }

    @Override
    public boolean attachable() {
        return true;
    }

    @Override
    public void onCreate() {
        // do nothing
    }

    @Override
    public void onStart() {
        // do nothing
    }

    @Override
    public void onResume() {
        // do nothing
    }

    @Override
    public void onPause() {
        // do nothing
    }

    @Override
    public void onStop() {
        // do nothing
    }

    @Override
    public void onDestroy() {
        // do nothing
    }

    @Override
    public void onIntent(Intent intent) {
        // do nothing
    }

    public void setMessenger(Handler messenger) {
        this.mMessenger = messenger;
    }
}
