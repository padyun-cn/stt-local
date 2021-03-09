package com.mon.ui.buildup;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mon.ui.R;
import com.mon.ui.dialog.CoreProgressDialog;
import com.mon.ui.list.compat.adapter.BaseV2RecyclerAdapter;
import com.mon.ui.list.compat.adapter.IBaseRecyclerModel;
import com.padyun.core.dialogs.CoreDgV2Alert;
import com.uls.utilites.un.Useless;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by daiepngfei on 9/17/19
 */
public abstract class ViewSimpleList extends FrameLayout implements BaseV2RecyclerAdapter.VHComponent, Handler.Callback {

    private View mRootView;
    private SwipeRecyclerView mSwipeRecyclerView;
    private ConfigOption mConfigOption;
    private BaseV2RecyclerAdapter mListAdapter;
    private FrameLayout mEmptyView;
    private Handler mHandler = new Handler(this);
    private boolean mIsAutoScrollToBottomEnable;

    public ViewSimpleList(@NonNull Activity context) {
        super(context);
        onCreate();
    }

    public ViewSimpleList(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        onCreate();
    }

    public SwipeRecyclerView getSwipeRecyclerView() {
        return mSwipeRecyclerView;
    }

    public void onCreate() {
        mConfigOption = getCustomConfigOption();
        if (mConfigOption == null) {
            mConfigOption = genDefaultConfigOption();
        }
        addView(onCreateView(LayoutInflater.from(getContext()), this));
    }

    protected ConfigOption genDefaultConfigOption() {
        ConfigOption configOption = new ConfigOption();
        configOption.isAutoEmtpyView = true;
        configOption.isDefaultDecoration = true;
        configOption.isAutoFirstLoad = true;
        return configOption;
    }

    protected static class ConfigOption {
        private boolean isAutoEmtpyView = false;
        private boolean isCustomSetData = false;
        private boolean isDefaultDecoration = false;
        private Integer rootBackgroundColor = null;
        private boolean isAutoFirstLoad = false;

        public void setAutoEmtpyView(boolean autoEmtpyView) {
            isAutoEmtpyView = autoEmtpyView;
        }

        public void setCustomSetData(boolean customSetData) {
            isCustomSetData = customSetData;
        }

        public void setDefaultDecoration(boolean defaultDecoration) {
            isDefaultDecoration = defaultDecoration;
        }

        public void setRootBackgroundColor(Integer rootBackgroundColor) {
            this.rootBackgroundColor = rootBackgroundColor;
        }

        public void setAutoFirstLoad(boolean autoFirstLoad) {
            isAutoFirstLoad = autoFirstLoad;
        }

        public void setRootBackgroundColor(int white) {
            this.rootBackgroundColor = white;
        }
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {

        if (mRootView == null) {

            // root view
            mRootView = inflater.inflate(R.layout.fr_core_simp_list, container, false);
            if(mConfigOption.rootBackgroundColor != null){
                mRootView.setBackgroundColor(mConfigOption.rootBackgroundColor);
            }

            // empty view
            mEmptyView = mRootView.findViewById(R.id.idFrBaseEmptyContainer);
            final View emptyView = onCreateEmptyView(inflater, mEmptyView);
            if (mEmptyView.getChildCount() == 0 && emptyView != null) {
                mEmptyView.addView(emptyView);
            }

            // top layout
            FrameLayout mFragmentTopLayout = mRootView.findViewById(R.id.fragmentTopLayout);
            onInitTopView(inflater, mFragmentTopLayout);

            // bottom layout
            FrameLayout mFragmentBottomLayout = mRootView.findViewById(R.id.fragmentBottomBar);
            onInitBottomView(inflater, mFragmentBottomLayout);

            // overlay layout
            FrameLayout mFragmentOverlayLayout = mRootView.findViewById(R.id.fragmentOverlay);
            onInitOverlayView(inflater, mFragmentOverlayLayout);

            // swipe recyler view
            mSwipeRecyclerView = mRootView.findViewById(R.id.idFrBaseSwipeRecyclerView);
            mSwipeRecyclerView.setLayoutManager(onCreateLayoutManager());
            mSwipeRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    ViewSimpleList.this.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    ViewSimpleList.this.onScrolled(recyclerView, dx, dy);
                }
            });
            if (mConfigOption.isDefaultDecoration) {
                mSwipeRecyclerView.addItemDecoration(getDefaultDecor());
            }
            if (!mConfigOption.isCustomSetData) {
                mListAdapter = new BaseV2RecyclerAdapter(Objects.requireNonNull((Activity)getContext()), this, mHandler);
            }
            onInitSwipeReclcerView(mSwipeRecyclerView, mListAdapter);
            if (mListAdapter != null) mSwipeRecyclerView.setAdapter(mListAdapter);
            this.onLoadListFirstTime();
        }

        return mRootView;
    }

    @NonNull
    protected LinearLayoutManager onCreateLayoutManager() {
        return onCreateDefaultLayoutManger();
    }

    @NonNull
    private LinearLayoutManager onCreateDefaultLayoutManger() {
        return new LinearLayoutManager(getContext());
    }

    protected void onScrolled(RecyclerView recyclerView, int dx, int dy) {
    }

    protected void onScrollStateChanged(RecyclerView recyclerView, int newState) {

    }

    protected void onLoadListFirstTime(){}

    protected void listAppend(IBaseRecyclerModel data) {
        //Log.d("ScriptTestingService", "list-append-in");
        listUpdate(Useless.asList(data), false);
    }

    protected void listAppend(List<? extends IBaseRecyclerModel> data) {
        listUpdate(data, false);
    }

    protected void listRefresh(List<? extends IBaseRecyclerModel> data) {
        listUpdate(data, true);
    }

    /**
     *
     */
    private void listUpdate(List<? extends IBaseRecyclerModel> data, boolean clear) {
        if (clear) mListAdapter.clear();
        mListAdapter.addAll(data);
        if (mConfigOption.isAutoEmtpyView && mListAdapter.isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
            mSwipeRecyclerView.setVisibility(View.INVISIBLE);
        } else {
            mEmptyView.setVisibility(View.INVISIBLE);
            mSwipeRecyclerView.setVisibility(View.VISIBLE);
        }
        /*if (data.size() == 1) {
            mListAdapter.notifyItemInserted(mListAdapter.getItemCount() - 1);
        } else {*/
            mListAdapter.notifyDataSetChanged();
        /*}*/

        if (mIsAutoScrollToBottomEnable)
            mSwipeRecyclerView.scrollToPosition(mListAdapter.getItemCount() - 1);
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }

    /**
     * @param autoScrollToBottomEnable
     */
    public void setAutoScrollToBottomEnable(boolean autoScrollToBottomEnable) {
        if (!isAutoScrollToBottomEnable() && autoScrollToBottomEnable) {
            mSwipeRecyclerView.scrollToPosition(mListAdapter.getItemCount() - 1);
        }
        this.mIsAutoScrollToBottomEnable = autoScrollToBottomEnable;
    }

    /**
     * @return
     */
    public boolean isAutoScrollToBottomEnable() {
        return mIsAutoScrollToBottomEnable;
    }

    /**
     *
     */
    public void refreshList() {
        mListAdapter.notifyDataSetChanged();
    }

    /**
     * @return
     */
    protected ConfigOption getCustomConfigOption() {
        return null;
    }

    /**
     * @param inflater
     * @param container
     *
     * @return
     */
    @SuppressWarnings("unused")
    protected View onCreateEmptyView(LayoutInflater inflater, ViewGroup container) {
        return null;
    }

    /**
     * @param swipeRecyclerView
     * @param mListAdapter
     */
    @SuppressWarnings("unused")
    protected void onInitSwipeReclcerView(SwipeRecyclerView swipeRecyclerView, BaseV2RecyclerAdapter mListAdapter) {
    }

    /**
     * @param listener
     */
    public void setOnItemClickListener(BaseV2RecyclerAdapter.OnItemClickListener listener) {
        if (mListAdapter != null) mListAdapter.setOnItemClickListener(listener);
    }

    /**
     * @return
     */
    public BaseV2RecyclerAdapter getListAdapter() {
        return mListAdapter;
    }

    @NonNull
    private RecyclerView.ItemDecoration getDefaultDecor() {
        final Paint dividerPaint = new Paint();
        dividerPaint.setAntiAlias(true);
        dividerPaint.setColor(Color.parseColor("#e8e8e8"));
        return new RecyclerView.ItemDecoration() {

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                if (parent.getChildAdapterPosition(view) != 0) {
                    outRect.top = 1;
                }
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
                final int left = parent.getPaddingLeft();
                final int right = parent.getWidth() - parent.getPaddingRight();
                final int childCount = parent.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = parent.getChildAt(i);
                    c.drawRect(left, child.getTop() - 1, right, child.getTop(), dividerPaint);
                }
            }
        };
    }

    /**
     * @return
     */
    protected int getDataSize() {
        return mListAdapter == null ? 0 : mListAdapter.dataSize();
    }

    protected int getItemCount() {
        return mListAdapter == null ? 0 : mListAdapter.getItemCount();
    }

    /**
     *
     * @param cls
     * @param consumer
     * @param <T>
     */
    protected <T> void foreachData(Class<T> cls, Consumer<T> consumer) {
        Useless.foreach(mListAdapter.getData(), t -> {
            if (cls.isInstance(t)) {
                consumer.accept(cls.cast(t));
            }
        });
    }

    protected List<IBaseRecyclerModel> getData(){
        return mListAdapter.getData();
    }


    /**
     * @param inflater
     * @param topLayout
     */
    protected void onInitTopView(LayoutInflater inflater, FrameLayout topLayout) {
    }

    /**
     * @param inflater
     * @param topLayout
     */
    protected void onInitBottomView(LayoutInflater inflater, FrameLayout topLayout) {
    }

    /**
     * @param inflater
     * @param overlayLayout
     */
    protected void onInitOverlayView(LayoutInflater inflater, FrameLayout overlayLayout) {
    }

    private CoreProgressDialog mProgressDialog;

    protected void showLoading() {
        System.out.println("xiao xiao3 ");
        if (mProgressDialog == null) {
            // Integer color = getActivity() instanceof AcV2UiBase ? ((AcV2UiBase) getActivity()).getCustomStatusBarColor() : null;
            mProgressDialog = new CoreProgressDialog(getContext(), null);
            mProgressDialog.setCancelable(false);
        }
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    protected void dismissLoading() {
        System.out.println("xiao xiao4 ");
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    private CoreDgV2Alert mSimpleTipsDialog = null;

    protected void showSimpleTipsDialog(String s) {
        this.showSimpleTipsDialog(s, null);
    }

    protected void showSimpleTipsDialog(String s, Runnable runnable) {
        if (mSimpleTipsDialog == null) {
            mSimpleTipsDialog = CoreDgV2Alert.toCreate(Objects.requireNonNull(getContext()));
            mSimpleTipsDialog.setTitle("温馨提示")
                    .setExclusiveNaturalButton("知道了", (d, x) -> d.dismiss());
        }
        if (runnable != null) {
            mSimpleTipsDialog.setTitle("温馨提示")
                    .setExclusiveNaturalButton("知道了", (d, x) -> {
                        d.dismiss();
                        runnable.run();
                    });
        } else {
            mSimpleTipsDialog.setTitle("温馨提示")
                    .setExclusiveNaturalButton("知道了", (d, x) -> d.dismiss());
        }
        mSimpleTipsDialog.setMessage(s).show();
    }
}
