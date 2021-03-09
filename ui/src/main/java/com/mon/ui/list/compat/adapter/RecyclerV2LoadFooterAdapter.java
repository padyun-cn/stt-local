package com.mon.ui.list.compat.adapter;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mon.ui.R;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * Created by daiepngfei on 9/15/17
 */

public class RecyclerV2LoadFooterAdapter extends BaseV2RecyclerAdapter {
    private FooterModel mFooterModel = new FooterModel();
    private boolean mLocked;
    private boolean mEnable;
    private BaseRecyclerHolder mFooterHolder;

    Activity ac;
    public RecyclerV2LoadFooterAdapter(@NonNull Activity act, @NonNull VHComponent creator, @NonNull Handler messenger) {
        super(act, creator, messenger);

        ac=act;
    }

    public void setEnable(boolean enable) {
        mEnable = enable;
    }

    public synchronized void showFooterWithLoading() {
        unlock();
        lockFooterWithLoading(FooterModel.STATE_LOADING, false);
    }

    public synchronized void lockFooterWithLoading() {
        lockFooterWithLoading(FooterModel.STATE_LOADING, true);
    }

    public synchronized void showLoadFooterWithError() {
        unlock();
        lockFooterWithLoading(FooterModel.STATE_ERROR, false);
    }

    public synchronized void lockFooterWithFinishing() {
        lockFooterWithLoading(FooterModel.STATE_COMPELET, true);
    }
    public synchronized void lockFooterWithFinishing(boolean isShowTip) {
        if(!isShowTip){
            lockFooterWithLoading(FooterModel.STATE_COMPELET_NOTIP, true);
        }
    }

    public synchronized void removeLoadFooter() {
        if (isPermitted()) {
            unlock();
            if (mData.contains(mFooterModel)) {
                mData.remove(mFooterModel);
            }
            notifyDataSetChanged();
        }
    }

    private synchronized void lockFooterWithLoading(int state, boolean lock) {
        if(lock) mLocked = true;
        if (isPermitted() ) {
            mFooterModel.loadState = state;
            if (showFooter()) {
                notifyDataSetChanged();
            }
        }
    }


//    public synchronized void lockFooterEnd() {
//        mLocked = true;
//        mEnable=true;
//
//            mFooterModel.loadState = FooterModel.STATE_COMPELET;
//        mData.add(mFooterModel);
//                notifyDataSetChanged();
//
//
//    }


    private boolean showFooter() {
        boolean show = mData.size() >= 0;
        if (show && !mData.contains(mFooterModel)) {
            mData.add(mFooterModel);
        }
        return show;
    }

    private synchronized void unlock() {
        mLocked = false;
    }

    public boolean isLocked() {
        return mLocked;
    }

    private boolean isPermitted() {
        return mEnable;
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() || (dataSize() == 1 && getData().get(0) == mFooterModel);
    }

    @Override
    protected void addData(@NonNull List<? extends IBaseRecyclerModel> data) {
        mData.addAll(mData.contains(mFooterModel) ? mData.size() - 1 : mData.size(), data);
    }

    @Override
    public void addItem(int index, IBaseRecyclerModel model) {
        super.addItem(mData.contains(mFooterModel) ? index - 1 : index, model);
    }

    @NonNull
    @Override
    public BaseRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == R.layout.item_load_footer) {
            mFooterHolder = new FooterHolder(LayoutInflater.from(mAct).inflate(viewType, parent, false));
            return mFooterHolder;
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    public boolean isFooterView(View view) {
        return mFooterHolder != null && view == mFooterHolder.itemView;
    }

    private static class FooterModel implements IBaseRecyclerModel {
        int loadState;
        static final int STATE_COMPELET = 0;
        static final int STATE_LOADING = 1;
        static final int STATE_ERROR = 2;
        static final int STATE_COMPELET_NOTIP = 3;

        @Override
        public int getTypeItemLayoutId() {
            return R.layout.item_load_footer;
        }
    }

    private static class FooterHolder extends AbsSelfInitRecyclerHolder<FooterModel> {

        private TextView text;
        private ProgressBar progressBar;
        private String[] textStr;

        FooterHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void init(@NonNull View view) {
            text = (TextView) view.findViewById(R.id.footerText);
            progressBar = (ProgressBar) view.findViewById(R.id.footerProgressbar);
        }

        @Override
        public void set(@NonNull Activity act, @NonNull BaseV2RecyclerAdapter adapter, @NonNull FooterModel item, int position) {
            if (textStr == null) {
                textStr = new String[4];
                textStr[0] = act.getString(R.string.string_load_adapter_recyclerloadfooteradapter_finished);
                textStr[1] = act.getString(R.string.string_load_adapter_recyclerloadfooteradapter_loading);
                textStr[2] = act.getString(R.string.string_load_adapter_recyclerloadfooteradapter_error);
                textStr[3] = "";
            }
            text.setText(textStr[item.loadState]);
            progressBar.setVisibility(item.loadState == FooterModel.STATE_LOADING ? View.VISIBLE : View.GONE);
        }
    }
}
