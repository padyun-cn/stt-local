package com.mon.ui.list.compat.adapterForViews;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uls.utilites.content.ILifeAttachable;
import com.uls.utilites.un.Useless;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by daiepngfei on 8/30/17
 */
@SuppressWarnings({"WeakerAccess", "ConstantConditions", "unchecked"})
public class View_BaseAdapter extends RecyclerView.Adapter<View_BaseHolder> implements ILifeAttachable {
    public static final int LIST_NO_MORE_DATA = -999;
    private static final int INVALID_KEY = 0;
    protected LinkedList<View_IBaseModel> mData = new LinkedList<>();
    protected LinkedList<View_IBaseModel> mFooters = new LinkedList<>();
    protected Context mAct;
    private VHComponent mCreator;
    private HashMap<Integer, Class<? extends View_BaseHolder>> mHolderCase;
    private OnItemClickListener mOnItemClickListener;
    private SparseArray<View_BaseHolder> mHolders = new SparseArray<>();
    private boolean mIsAttachable;
    private Handler mMessenger;
    private boolean isWrappingData;
    private int mDataSizeLimit;


    /**
     * @param act
     * @param creator
     */
    public View_BaseAdapter(@NonNull final Context act, @NonNull VHComponent creator, @NonNull Handler messenger, boolean isAttachable) {
        this(act, Collections.emptyList(), creator, messenger, isAttachable);
    }

    /**
     * @param act
     * @param creator
     */
    public View_BaseAdapter(@NonNull final Context act, @NonNull VHComponent creator, @NonNull Handler messenger) {
        this(act, Collections.emptyList(), creator,  messenger, true);
    }

    /**
     * @param act
     * @param data
     */
    public View_BaseAdapter(@NonNull final Context act, @NonNull List<? extends View_IBaseModel> data, @NonNull VHComponent creator, @NonNull Handler messenger, boolean isAttachable) {
        construct(act, data);
        mCreator = creator;
        mIsAttachable = isAttachable;
        mMessenger = messenger;
        setHasStableIds(true);
    }

    /**
     * @param act
     * @param cls
     *
     * @hide
     */
    public View_BaseAdapter(@NonNull final Activity act, @NonNull Class<? extends View_BaseHolder> cls) {
        this(act, null, cls);
    }

    /**
     * @param act
     * @param data
     *
     * @hide
     */
    public View_BaseAdapter(@NonNull final Activity act, @NonNull List<? extends View_IBaseModel> data, @NonNull HashMap<Integer, Class<? extends View_BaseHolder>> hoderCase) {
        construct(act, data);
        mHolderCase = hoderCase;
    }

    /**
     * @param act
     * @param data
     *
     * @hide
     */
    @SuppressLint("UseSparseArrays")
    public View_BaseAdapter(@NonNull final Activity act, @NonNull List<? extends View_IBaseModel> data, @NonNull Class<? extends View_BaseHolder> cls) {
        construct(act, data);
        mHolderCase = new HashMap<>();
        mHolderCase.put(INVALID_KEY, cls);

    }

    public void feedback(Message message){
        if(mMessenger != null) {
            mMessenger.sendMessage(message);
        }
    }

    public void feedback(int what, @NonNull Object obj){
        if(mMessenger != null) {
            Message message = mMessenger.obtainMessage();
            message.obj = obj;
            message.what = what;
            message.sendToTarget();
        }
    }

    public void feedback(int emptyMessage) {
        if (mMessenger != null) {
            mMessenger.sendEmptyMessage(emptyMessage);
        }
    }


    public boolean isWrappingData() {
        return isWrappingData;
    }

    public void setWrappingData(boolean wrappingData) {
        isWrappingData = wrappingData;
    }

    /**
     * @param indexLeft
     * @param indexRight
     */
    @SuppressWarnings("unused")
    public void swapData(int indexLeft, int indexRight) {
        if (!Useless.isIndexesValid(mData, indexLeft, indexRight)) {
            Collections.swap(mData, indexLeft, indexRight);
            notifyItemMoved(indexLeft, indexRight);
        }
    }

    /**
     * @return
     */
    public List<View_IBaseModel> getData() {
        return mData;
    }

    /**
     * @param index
     *
     * @return
     */
    public View_IBaseModel get(int index) {
        return getItemWithPosition(index);
    }

    /**
     * @param data
     */
    protected void addData(@NonNull List<? extends View_IBaseModel> data) {
        if (data != null) {
            mData.addAll(data);
        }
    }


    /**
     * @param index
     * @param model
     */
    public void addItem(int index, View_IBaseModel model) {
        if (Useless.nulls(model) || index < 0 || index > mData.size()) return;
        checkLimitedSizeAndPop(1);
        mData.add(index, model);
        notifyDataSetChanged();
    }

    public void addItem(View_IBaseModel model) {
        addItem(mData.size(), model);
    }

    /**
     * @param index
     */
    public void removeItemAtPosition(int index) {
        if (!Useless.isIndexesValid(getData(), index)) {
            this.mData.remove(index);
        }
    }

    /**
     *
     */
    public void clear() {
        if (mData != null) {
            mData.clear();
            notifyDataSetChanged();
        }
    }

    /**
     * @param models
     */
    public void addAll(List<? extends View_IBaseModel> models) {
        addAll(-1, models);
    }

    /**
     *
     * @param index
     * @param models
     */
    public void addAll(int index, List<? extends View_IBaseModel> models) {
        addDataInner(index, models);
    }

    /**
     * @param exist
     * @param newTarget
     */
    public void repalce(View_IBaseModel exist, View_IBaseModel newTarget) {
        if (Useless.replace(mData, exist, newTarget)) {
            notifyDataSetChanged();
        }
    }

    /*public interface OnDataSetChangedListener {
        void onDataSetChanged()
    }*/

    /**
     * @param act
     * @param data
     */
    private void construct(@NonNull Context act, @Nullable List<? extends View_IBaseModel> data) {
        mAct = act;
        addDataInner(-1, data);
    }

    /**
     * @param data
     */
    public void update(@NonNull List<? extends View_IBaseModel> data, boolean cleanMode) {
        update(-1, data, cleanMode);
    }

    /**
     *
     * @param index
     * @param data
     * @param cleanMode
     */
    public void update(int index, @NonNull List<? extends View_IBaseModel> data, boolean cleanMode) {
        if (cleanMode) mData.clear();
        addDataInner(index, data);
        notifyDataSetChanged();
    }

    /**
     * @return
     */
    public int dataSize() {
        return mData == null ? 0 : mData.size();
    }

    /**
     * @return
     */
    public boolean isEmpty() {
        return dataSize() == 0;
    }

    /**
     * @param data
     */
    protected void addDataInner(int index, @NonNull List<? extends View_IBaseModel> data) {
        if (data != null) {
            //Log.d("ScriptTestingService", "list-append-in-inner " + data.size());
            // checkLimitedSizeAndPop(data.size());
            //Log.d("ScriptTestingService", "list-append-in-inner-check-over " + mData.size());
            if(index == -1) {
                mData.addAll(data);
                if (mDataSizeLimit > 0 && mData.size() > mDataSizeLimit) {
                    for (int i = mData.size() - mDataSizeLimit + (mData.size() / 2); i > 0; i--) {
                        mData.pop();
                    }
                    notifyDataSetChanged();
                }
            } else {
                mData.addAll(index, data);
                notifyDataSetChanged();
            }
            //Log.d("ScriptTestingService", "list-append-in-inner-add-over " + mData.size());
        }
    }

    private void checkLimitedSizeAndPop(int newComingSize) {
        if (mDataSizeLimit > 0 && newComingSize + mData.size() > mDataSizeLimit) {
            for (int i = newComingSize + mData.size() - mDataSizeLimit; i >= mData.size(); i--) {
                mData.pop();
                notifyDataSetChanged();
            }
        }
    }


    public void addDataFooter(View_IBaseModel model) {
        if (model != null) {
            mFooters.add(model);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public View_BaseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            //viewType = R.layout.item_v2_empty;
            throw new IllegalArgumentException("You must return a type(R.layout.xxxx) from Your model that implement BaseRecyclerModel!");
        }

        // gen-item ivew
//        final View itemView = LayoutInflater.from(mAct).inflate(viewType, null, false);
        final View itemView = LayoutInflater.from(mAct).inflate(viewType, parent, false);

        // initialize from holder-case
        View_BaseHolder holder;

        // for testing :: not main-logic-code here
        {
            holder = getBaseRecyclerHolderWithHolderCase(viewType, itemView);
        }

        // initialize from creator
        if (holder == null && mCreator != null) {
            holder = mCreator.generateVHByType(itemView, viewType);
        }

        if (holder == null) {
            throw new NullPointerException("The Holder you generated sameId null!");
        }

        // initialize
        holder.initialize();
        // set messener
        holder.setMessenger(mMessenger);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull View_BaseHolder holder, int position) {
        mHolders.put(position, holder);
        final View_IBaseModel item = getItemWithPosition(position);
        if (item != null) {
            holder.autoSet(mAct, this, item, position, mOnItemClickListener);
        }
    }

    private View_IBaseModel getItemWithPosition(int position) {
        return isInvalidPosition(position) ? null : position >= mData.size() ? mFooters.get(position - mData.size()) : mData.get(position);
    }

    private boolean isInvalidPosition(int position) {
        return position >= getItemCount() || position < 0;
    }

    /**
     * @param position
     *
     * @return
     */
    @SuppressWarnings("unused")
    public View_BaseHolder getHolderWithPosition(int position) {
        return mHolders.get(position);
    }

    /**
     * @param position
     */
    public void resetHolderWithPosition(int position) {
        View_BaseHolder holder = mHolders.get(position);
        if (holder != null)
            holder.autoSet(mAct, this, getItemWithPosition(position), position, mOnItemClickListener);
    }

    @Override
    public int getItemViewType(int position) {
        return !isInvalidPosition(position) ? getItemWithPosition(position).getTypeItemLayoutId() : 0;
    }

    @Override
    public int getItemCount() {
        return mData.size() > 0 ? mData.size() + mFooters.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        mOnItemClickListener = l;
    }

    public void performItemClick(int position){
        if(mOnItemClickListener != null){
            mOnItemClickListener.onItemClick(this, get(position), position);
        }
    }

    public interface VHComponent {
        View_BaseHolder generateVHByType(@NonNull View itemView, int viewType);
    }

    public interface OnItemClickListener<T extends View_IBaseModel> {
        void onItemClick(@NonNull View_BaseAdapter adapter, @NonNull T item, int position);
    }


    /**
     * @param viewType
     * @param itemView
     *
     * @return
     */
    private View_BaseHolder getBaseRecyclerHolderWithHolderCase(int viewType, View itemView) {
        View_BaseHolder holder = null;
        if (mHolderCase != null && mHolderCase.size() > 0) {
            try {
                Class<? extends View_BaseHolder> cls;
                if (mHolderCase.size() == 1 && mHolderCase.containsKey(INVALID_KEY)) {
                    cls = mHolderCase.get(INVALID_KEY);
                } else {
                    cls = mHolderCase.get(viewType);
                }
                if (cls != null) {
                    Constructor<? extends View_BaseHolder> constructor = cls.getConstructor(View.class);
                    if (constructor != null) {
                        holder = constructor.newInstance(itemView);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return holder;
    }

    public void setDataLimit(int dataLimit) {
        this.mDataSizeLimit = dataLimit;
    }

    @Override
    public boolean attachable() {
        return mIsAttachable;
    }

    @Override
    public void onCreate() {
        if (attachable()) {
            for (int i = 0; i < mHolders.size(); i++) {
                if (mHolders.get(i).attachable()) {
                    mHolders.get(i).onCreate();
                }
            }
        }
    }

    @Override
    public void onStart() {
        if (attachable()) {
            for (int i = 0; i < mHolders.size(); i++) {
                if (mHolders.get(i).attachable()) {
                    mHolders.get(i).onStart();
                }
            }
        }
    }

    @Override
    public void onResume() {
        if (attachable()) {
            for (int i = 0; i < mHolders.size(); i++) {
                if (mHolders.get(i).attachable()) {
                    mHolders.get(i).onResume();
                }
            }
        }
    }

    @Override
    public void onPause() {
        if (attachable()) {
            for (int i = 0; i < mHolders.size(); i++) {
                if (mHolders.get(i).attachable()) {
                    mHolders.get(i).onPause();
                }
            }
        }
    }

    @Override
    public void onStop() {
        if (attachable()) {
            for (int i = 0; i < mHolders.size(); i++) {
                if (mHolders.get(i).attachable()) {
                    mHolders.get(i).onStop();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        if (attachable()) {
            for (int i = 0; i < mHolders.size(); i++) {
                if (mHolders.get(i).attachable()) {
                    mHolders.get(i).onDestroy();
                }
            }
        }
    }

    @Override
    public void onIntent(Intent intent) {
        if (attachable()) {
            for (int i = 0; i < mHolders.size(); i++) {
                if (mHolders.get(i).attachable()) {
                    mHolders.get(i).onIntent(intent);
                }
            }
        }
    }


}
