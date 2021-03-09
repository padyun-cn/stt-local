package com.padyun.scripttools.biz.ui.dialogs;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mon.ui.list.compat.adapter.BaseV2RecyclerAdapter;
import com.mon.ui.list.compat.adapter.IBaseRecyclerModel;
import com.uls.utilites.un.Useless;
import com.padyun.scripttools.R;
import com.padyun.scripttools.biz.ui.data.VHMText;
import com.padyun.scripttools.biz.ui.holders.IVHGenorCommon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daiepngfei on 6/20/19
 */
public class DgV2Common extends DgV2Base  implements BaseV2RecyclerAdapter.OnItemClickListener<IBaseRecyclerModel>{
    @Override
    public void onItemClick(@NonNull BaseV2RecyclerAdapter adapter, @NonNull IBaseRecyclerModel item, int position) {
        if (mIsDefaultCancelButton && position == adapter.getItemCount() - 1) {
            dismiss();
        } else if(mOnItemClickListener != null ){
            if(VHMText.class.isInstance(item) && ((VHMText)item).isDisable()) return;
            mOnItemClickListener.onItemClick(this, adapter, item, position);
        }
    }

    public enum EDCType {
        NONE,
        ADD,
        DEL,
        MODF,
        FORWARD,
        BACKEND
    }
    private RecyclerView mListView;
    private TextView mTitle, mTips;
    private Activity mAct;
    private BaseV2RecyclerAdapter.VHComponent mIVHGenorCommon;
    private boolean mIsDefaultCancelButton;
    private OnItemClickListener mOnItemClickListener;
    private OnItemDataChangeListener mOnItemDataChangedListener;


    public interface OnItemClickListener {
        void onItemClick(DgV2Common dg, BaseV2RecyclerAdapter adapter, IBaseRecyclerModel item, int position);
    }

    public interface OnItemDataChangeListener {
        void onItemDataChange(DgV2Common dg, BaseV2RecyclerAdapter adapter, EDCType type, IBaseRecyclerModel item, int position);
    }

    public static Builder buildWith(Activity act){
        return Builder.with(act);
    }

    public static class Builder {
        private Activity act;
        private List<IBaseRecyclerModel> items;
        private boolean defaultDismiss = true;
        private String title;
        private String titleTips;
        private BaseV2RecyclerAdapter.VHComponent comm;
        private OnItemClickListener il;
        private OnItemDataChangeListener dcl;
        private DgV2Common dg;
        private Builder(Activity act){
            this.act = act;
        }

        public static Builder with(Activity act){ return new Builder(act); }

        public DgV2Common create(){
            dg = new DgV2Common(act);
            dg.setIsDefaultCancelButton(defaultDismiss);
            dg.setIVHGenorCommon(comm);
            dg.show(title, titleTips, items, il, dcl);
            return dg;
        }

        public void show(){
            if(dg == null) create();
            dg.show();
        }

        public Builder setItems(List<IBaseRecyclerModel> items){ this.items = items; return this;}
        public Builder setItems(IBaseRecyclerModel... item){ this.items = Useless.asList(item); return this;}
        public Builder setItems(String... items){
            return setItems(null, items);
        }

        public Builder setItems(Integer color, String... items){
            ArrayList<IBaseRecyclerModel> strItems = new ArrayList<>();
            Useless.foreach(items, t -> strItems.add(new VHMText(t, color)));
            setItems(strItems);
            return this;
        }
        public Builder setDefaultDismiss(boolean defaultDismiss){ this.defaultDismiss = defaultDismiss;  return this;}
        public Builder setTitle(String title){this.title = title; return this;}
        public Builder setTitleTips(String titleTips){this.titleTips = titleTips; return this;}
        public Builder setCommVHG(BaseV2RecyclerAdapter.VHComponent comm){this.comm = comm; return this;}
        public Builder setOnItemClickListener(OnItemClickListener il){this.il = il; return this;}
        public Builder setOnItemChangedListener(OnItemDataChangeListener dcl){this.dcl = dcl; return this;}
    }

    public static DgV2Common show(Activity ctx, List<IBaseRecyclerModel> items, BaseV2RecyclerAdapter.VHComponent common, OnItemClickListener l) {
        return show(ctx, null, null, items, common, l);
    }

    public static DgV2Common show(Activity ctx, String title, String tips, List<IBaseRecyclerModel> items, BaseV2RecyclerAdapter.VHComponent common, OnItemClickListener l) {
        return show(ctx, title, tips, true, items, common, l, null);
    }

    public static DgV2Common show(Activity ctx, String title, String tips,  List<IBaseRecyclerModel> items, BaseV2RecyclerAdapter.VHComponent common, OnItemClickListener l, OnItemDataChangeListener il) {
        return show(ctx, title, tips, true, items, common, l, il);
    }

    public static DgV2Common show(Activity ctx, String title, String tips, boolean defaultCancelButton, List<IBaseRecyclerModel> items, BaseV2RecyclerAdapter.VHComponent common,
                                  OnItemClickListener l, OnItemDataChangeListener il) {
        final DgV2Common dg = new DgV2Common(ctx);
        dg.setIsDefaultCancelButton(defaultCancelButton);
        dg.setIVHGenorCommon(common);
        dg.show(title, tips, items, l, il);
        return dg;
    }

    private void setIVHGenorCommon(BaseV2RecyclerAdapter.VHComponent mIVHGenorCommon) {
        this.mIVHGenorCommon = mIVHGenorCommon;
    }

    private void setIsDefaultCancelButton(boolean mIsDefaultCancelButton) {
        this.mIsDefaultCancelButton = mIsDefaultCancelButton;
    }

    private DgV2Common(@NonNull Activity context) {
        super(context);
        mAct = context;
    }

    @Override
    protected void onInitialize() {
        mListView = findViewById(R.id.list);
        mTitle = findViewById(R.id.title);
        mTips = findViewById(R.id.tips);
        mListView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dg_v2_title_item_group;
    }

    public void show(String title, String tips, List<IBaseRecyclerModel> items, OnItemClickListener l, OnItemDataChangeListener il) {
        this.mOnItemClickListener = l;
        this.mOnItemDataChangedListener = il;
        final boolean hasNoTilte = Useless.isEmpty(title);
        if (!hasNoTilte) {
            findViewById(R.id.title_bar).setVisibility(View.VISIBLE);
            findViewById(R.id.title_bar).setClickable(true);
            mTitle.setText(title);
            mListView.setBackgroundResource(R.drawable.bg_shadow_card_half_b);
        }
        if (!Useless.isEmpty(tips)) mTips.setText(tips);
        else mTips.setVisibility(View.GONE);
        final BaseV2RecyclerAdapter adapter = new BaseV2RecyclerAdapter(mAct, mIVHGenorCommon == null ? new IVHGenorCommon() : mIVHGenorCommon, null);
        adapter.addAll(items);
        if(mIsDefaultCancelButton) {
            adapter.addItem(new VHMText("返回", mAct.getResources().getColor(R.color.color_button_text_cancel)));
            // adapter.setIDataSizeCounter(a -> a.getItemCount() - 1);
        }
        adapter.setOnItemClickListener(this);
        mListView.setAdapter(adapter);
        super.show();
    }

    @Override
    public void show() {
        // super.show();
    }


}
