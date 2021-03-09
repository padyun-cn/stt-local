package com.mon.ui.list.compat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.mon.ui.R;
import com.mon.ui.buildup.CvV2HomeRecyclerView;
import com.mon.ui.buildup.CvV2HomeSwipeRefreshLayout;
import com.mon.ui.dialog.CoreProgressDialog;
import com.mon.ui.list.compat.adapter.BaseRecyclerHolder;
import com.mon.ui.list.compat.adapter.BaseV2RecyclerAdapter;
import com.mon.ui.list.compat.adapter.RecyclerV2LoadFooterAdapter;
import com.spring.network.http.callback.HCDataList;
import com.mon.ui.list.compat.adapter.IBaseRecyclerModel;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Created by daiepngfei on 9/14/17
 */
@SuppressWarnings("ALL")
public abstract class AbsFmV2RecyclerBase extends FmBase implements Handler.Callback, BaseV2RecyclerAdapter.VHComponent, SwipeRefreshLayout.OnRefreshListener {

    private CvV2HomeSwipeRefreshLayout mSwipeLayout;
    private RelativeLayout mContentWrapper;
    private View mEmptyView;
    private View mErrorView;
    private CvV2HomeRecyclerView mListView;
    private RecyclerV2LoadFooterAdapter mAdapter;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int PAGE_START = 1;
    private boolean mLazyLoading = true;
    private boolean mIsFristRefreshEnding;
    private boolean mIsFristRefreshStart;
    private long mLastInitDataTime = 0l;
    private static final int TIME_SLOT = 200;
    private LinearLayoutManager mLayoutManager;
    private boolean mIsLoadingLocked;
    private CoreProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mContentWrapper == null) {
            // content wrapper
            mContentWrapper = (RelativeLayout) inflater.inflate(R.layout.fr_v2_base, container, false);
            // empty view
            mEmptyView = onCreateEmptyView(inflater);
            if (mEmptyView != null) {
                mEmptyView.setVisibility(View.GONE);
//                mContentWrapper.addView(mEmptyView, 0,
//                        LayoutParamGener.getRelaParams_M());
            }
            // error view
            mErrorView = onCreateErrorView(inflater);
            if (mErrorView != null) {
                mErrorView.setVisibility(View.GONE);
//                mContentWrapper.addView(mErrorView, 0, LayoutParamGener.getRelaParams_M());
            }

            // refreshable
            mSwipeLayout = mContentWrapper.findViewById(R.id.listWrapperLayout);
            mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_blue_dark);
            mSwipeLayout.setOnRefreshListener(this);
            mSwipeLayout.setEnabled(isEnablePullToRefresh());

            // initialize adapter
            mAdapter = new RecyclerV2LoadFooterAdapter(getActivity(), this, new Handler(this));
            mAdapter.setEnable(isEnableLoadingMore());
            mAdapter.setOnItemClickListener(onCreateItemClickListener());
            mAdapter.onCreate();

            // list view
            mListView = mSwipeLayout.findViewById(R.id.listView);
            ((DefaultItemAnimator) mListView.getItemAnimator()).setSupportsChangeAnimations(false);
            mListView.getItemAnimator().setChangeDuration(0);
            final RecyclerView.LayoutManager layoutManager = getLayoutManager();
            mListView.setLayoutManager(layoutManager);
            if (isEnableLoadingMore()) {
                addLoadingMoreEvent();
            }
            // list view
            mListView.setAdapter(mAdapter);

            // on initialize views
            onCustomInit(mSwipeLayout, mListView, layoutManager, mAdapter);
            // dismiss progress loading
            dismissLoading();
        }
        return mContentWrapper;
    }

    @Override
    public final boolean handleMessage(Message msg) {
        return onItemMessage(msg);
    }

    /**
     * @param msg
     *
     * @return
     */
    protected boolean onItemMessage(Message msg) {
        switch (msg.what) {
            case BaseRecyclerHolder.LOADING_SHOW:
                showLoadingDialog();
                break;
            case BaseRecyclerHolder.LOADING_DISSMISS:
                dismissLoadingDialog();
                break;
            default:
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isEnableLazyLoading() && mLazyLoading) {
            mLazyLoading = false;
            lazyRefresh();
        }
        mAdapter.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isLastLoadingFinishedWithEmptyList() && shouldLazyRetryLoadingOnResume()) {
            lazyRefresh();
        }
        mAdapter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mAdapter.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.onStop();
    }

    @Override
    public void onDestroy() {
        dismissLoading();
        if (mAdapter != null) {
            mAdapter.onDestroy();
        }
        super.onDestroy();
    }

    public void onIntent(Intent intent) {
        mAdapter.onIntent(intent);
    }

    /**
     *
     */
    protected void lazyRefresh() {
        mSwipeLayout.post(new Runnable() {
            @Override
            public void run() {
                if (isEnablePullToRefresh() && isLazyRefreshAnim()) {
                    mSwipeLayout.setRefreshing(true);
                } else {
//                    showLoading();
                }
                doRefresh();
            }
        });
    }

    /**
     *
     */
    protected void doRefresh() {
        onRefresh();
    }

    /**
     *
     */
    public void reload() {
        if (isFirstloadingStart()) {
            forceReload();
        }
    }

    public void forceReload() {
        doRefresh();
    }

    /**
     *
     */
    private void addLoadingMoreEvent() {
        mListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!isAbortLoading() && getLayoutManager().findLastCompletelyVisibleItemPosition() == getData().size() - 1) {
                        mAdapter.lockFooterWithLoading();
                        onLoadingMore();
                    }
                }

                AbsFmV2RecyclerBase.this.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }
        });
    }

    /**
     * @param recyclerView
     * @param newState
     */
    protected void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        // do nothing
    }


    protected boolean update(@Nullable List<? extends IBaseRecyclerModel> models, boolean isRefresh) {
        return update(models, isRefresh, false);
    }

    private boolean update(@Nullable List<? extends IBaseRecyclerModel> models, boolean isRefresh, boolean forceClear) {
        boolean update = false;
        if (mAdapter != null) {
            // refresh ending
            refreshEnding();
            // remove footerview first
            mAdapter.removeLoadFooter();
            // restrictGet size
            final int size = models == null ? 0 : models.size();
            // do upgrade
            if (!forceClear && size != 0) {
                mAdapter.update(models, isRefresh);
                if (isRefresh) mListView.scrollToPosition(0);
                update = true;
            }

            // inShow footer with state
            if (!getAdapter().isEmpty()) {
                if (size < getSettledPageSize()) {
                    if (isShowLoadMoreTxt()) {
                        mAdapter.lockFooterWithFinishing();
                    } else {
                        mAdapter.lockFooterWithFinishing(false);
                    }
                } else {
                    mSwipeLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.showFooterWithLoading();
                        }
                    }, 200);
                }
            }
            // empty view
            emptyEnding();
        }
        releaseLoadingLock();
        return update;
    }


    protected boolean updateForSearch(@Nullable List<? extends IBaseRecyclerModel> models, boolean isRefresh) {
        boolean update = false;
        if (mAdapter != null) {
            // refresh ending
            refreshEnding();
            // remove footerview first
            mAdapter.removeLoadFooter();
            // restrictGet size
            final int size = models == null ? 0 : models.size();
            // do upgrade
            if (size != 0) {
                mAdapter.update(models, isRefresh);
                if (isRefresh) mListView.scrollToPosition(0);
                update = true;
            } else {
                clearList();
            }

            // inShow footer with state
            if (!getAdapter().isEmpty()) {
                if (size < getSettledPageSize()) {
                    if (isShowLoadMoreTxt()) {
                        mAdapter.lockFooterWithFinishing();
                    } else {
                        mAdapter.lockFooterWithFinishing(false);
                    }
                } else {
                    mSwipeLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.showFooterWithLoading();
                        }
                    }, 200);
                }
            }
            // empty view
            emptyEnding();
        }
        releaseLoadingLock();
        return update;
    }


    public void clearList() {
        getData().clear();
        notifyDataSetChanged();
    }


    /**
     * @return
     */
    protected boolean isAbortLoading() {
        return mAdapter.isLocked();
    }

    /**
     *
     */
    protected void abortRefershing() {
        if (mSwipeLayout.isRefreshing()) {
            mSwipeLayout.setRefreshing(false);
        }
        dismissLoading();
    }

    /**
     *
     */
    protected void emptyEnding() {
        mSwipeLayout.setEnabled(isEnablePullToRefresh() && !mAdapter.isEmpty());
        if (mErrorView != null) mErrorView.setVisibility(View.GONE);
        if (mEmptyView != null)
            mEmptyView.setVisibility(mAdapter.isEmpty() ? View.VISIBLE : View.GONE);
        mSwipeLayout.setVisibility(mAdapter.isEmpty() ? View.INVISIBLE : View.VISIBLE);
    }

    /**
     *
     */
    protected void errorEnding() {
        mSwipeLayout.setEnabled(isEnablePullToRefresh() && !mAdapter.isEmpty());
        if (mEmptyView != null) mEmptyView.setVisibility(View.GONE);
        if (mErrorView != null)
            mErrorView.setVisibility(mAdapter.isEmpty() ? View.VISIBLE : View.GONE);
        mSwipeLayout.setVisibility(mAdapter.isEmpty() ? View.INVISIBLE : View.VISIBLE);
    }


    public View getmEmptyView() {
        if (mEmptyView != null) return mEmptyView;
        return null;
    }

    /**
     *
     */
    protected void refreshEnding() {
        if (!mIsFristRefreshEnding) {
            mIsFristRefreshEnding = true;
        }
        abortRefershing();
    }

    /**
     * @return
     */
    protected ViewGroup getRootView() {
        return mContentWrapper;
    }

    /**
     * @return
     */
    public RecyclerView getRecyclerView() {
        return mListView;
    }

    /**
     * @param inflater
     *
     * @return
     */
    protected View onCreateEmptyView(@NonNull LayoutInflater inflater) {
        return null;
    }

    /**
     * @param inflater
     *
     * @return
     */
    protected View onCreateErrorView(@NonNull LayoutInflater inflater) {
        return onCreateEmptyView(inflater);
    }

    /**
     * @return
     */
    protected BaseV2RecyclerAdapter.OnItemClickListener onCreateItemClickListener() {
        return BaseV2RecyclerAdapter.OnItemClickListener.class.isInstance(this) ? (BaseV2RecyclerAdapter.OnItemClickListener) this : null;
    }

    /**
     * @return
     */
    protected RecyclerV2LoadFooterAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * @return
     */
    protected List<? extends IBaseRecyclerModel> getData() {
        return mAdapter.getData();
    }

    /**
     * @return
     */
    protected int getNextPageIndex() {
        return mAdapter.getItemCount() / getSettledPageSize() + 1;
    }


    @Override
    public void onRefresh() {
        initDataLimited(PAGE_START, getSettledPageSize(), true);
    }

    /**
     *
     */
    protected void onLoadingMore() {
        if (System.currentTimeMillis() - mLastInitDataTime > TIME_SLOT) {
            initDataLimited(getNextPageIndex(), getSettledPageSize(), false);
        }

    }

    private void initDataLimited(int page, int size, boolean refresh) {
        if (mIsLoadingLocked) return;
        if (!mIsFristRefreshStart) {
            mIsFristRefreshStart = true;
        }
        if (System.currentTimeMillis() - mLastInitDataTime > TIME_SLOT) {
            mLastInitDataTime = System.currentTimeMillis();
            mIsLoadingLocked = true;
            initData(page, size, refresh);
        }
    }

    protected boolean isFirstloadingStart() {
        return mIsFristRefreshStart;
    }

    protected void setFirstloadingStart() {
        mIsFristRefreshStart = false;
    }

    protected boolean isFirstloadingEnd() {
        return mIsFristRefreshEnding;
    }

    /**
     * @return
     */
    protected boolean isEnablePullToRefresh() {
        return true;
    }

    /**
     * @return
     */
    protected boolean isEnableLoadingMore() {
        return true;
    }

    protected boolean isShowLoadMoreTxt() {
        return true;
    }

    /**
     * @return
     */
    protected LinearLayoutManager getLayoutManager() {
        if (mLayoutManager == null) {
            LinearLayoutManager manager = onGenerateLayoutManager();
            mLayoutManager = (manager == null ? generateDefaultLayoutManager() : manager);
        }
        return mLayoutManager;
    }

    /**
     * @return
     */
    private LinearLayoutManager generateDefaultLayoutManager() {
        return new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    }

    /**
     * @return
     */
    protected LinearLayoutManager onGenerateLayoutManager() {
        return null;
    }


    /**
     *
     */
    protected void disableFooterLoading() {
        mAdapter.setEnable(false);
    }

    /**
     * @param swp
     * @param rv
     * @param mag
     * @param adp
     */
    protected void onCustomInit(View swp, RecyclerView rv, RecyclerView.LayoutManager mag, BaseV2RecyclerAdapter adp) {
    }

    /**
     * @param nextPageIndex
     * @param pageSize
     */
    protected abstract void initData(final int nextPageIndex, final int pageSize, final boolean isRefresh);


    /**
     * @return
     */
    protected int getSettledPageSize() {
        return DEFAULT_PAGE_SIZE;
    }


    /**
     *
     */
    public void notifyDataSetChanged() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
//            mAdapter.notifyItemRangeChanged(0,mAdapter.dataSize()-1);

        }
    }

    public boolean isLazyRefreshAnim() {
        return false;
    }

    public boolean isEnableLazyLoading() {
        return true;
    }


    /**
     * @param <T>
     */
    protected class V2ReqHandler<T extends IBaseRecyclerModel> extends HCDataList<T> {

        private boolean isRefresh;
        private boolean clearFirst;

        public V2ReqHandler(Class<T> cls, boolean isRefresh) {
            this(cls, true, isRefresh);
        }


        public V2ReqHandler(Class<T> cls, boolean checkable, boolean isRefresh) {
            super(cls, checkable);
            this.isRefresh = isRefresh;
        }

        public V2ReqHandler(Class<T> cls, boolean checkable, boolean clearfirstRefresh, boolean isRefresh) {
            super(cls, checkable);
            this.isRefresh = isRefresh;
            this.clearFirst = clearfirstRefresh;
        }

        @Override
        public void onHandledSuccess(List<T> t) {
            handleLoadingSuccess(fixData(t), isRefresh);
            onSuccess();
        }


        protected List<? extends IBaseRecyclerModel> fixData(List<T> t) {
            return t;
        }

        @Override
        public void onFailure(Exception e, int code, String msg) {
            handleLoadingFailure(isRefresh, code);
            if (isRefresh && clearFirst) clearList();
        }

        protected void onSuccess() {

        }

    }

    protected <T extends IBaseRecyclerModel> boolean handleLoadingSuccess(List<T> t, boolean isRefresh) {
        boolean update = update(t, isRefresh);
        releaseLoadingLock();
        return update;
    }

    protected void releaseLoadingLock() {
        mIsLoadingLocked = false;
    }

    protected void handleLoadingFailure(boolean isRefresh, int code) {
        refreshEnding();
        emptyEnding();
        if (!isRefresh) {
            if (code == BaseV2RecyclerAdapter.LIST_NO_MORE_DATA) getAdapter().lockFooterWithFinishing();
            else getAdapter().showLoadFooterWithError();
        }
        releaseLoadingLock();
    }

    public void setLayoutFreezingTouchEvent(boolean freezing) {
        mSwipeLayout.setIsFreezingTouchEvent(freezing);
        mListView.setIsFreezingTouchEvent(freezing);
    }

    protected void setBackgroundColor(int color) {
        getRootView().setBackgroundColor(color);
    }

    protected boolean isLastLoadingFinishedWithEmptyList() {
        return mIsFristRefreshEnding && getData().size() == 0;
    }

    protected boolean isLazyLoadingFinished() {
        return mIsFristRefreshEnding;
    }

    protected boolean shouldLazyLoadingOnStart() {
        return true;
    }

    protected boolean shouldLazyRetryLoadingOnResume() {
        return false;
    }

    protected void showLoadingDialog() {
        System.out.println("xiao xiao");
        if (mProgressDialog == null) {
            // Integer color = getActivity() instanceof AcV2UiBase ? ((AcV2UiBase) getActivity()).getCustomStatusBarColor() : null;
            mProgressDialog = new CoreProgressDialog(getContext(), null);
            mProgressDialog.setCancelable(false);
        }
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    protected void dismissLoadingDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    protected void showLoading() {
        System.out.println("xiao xiao2");

        if (mContentWrapper != null) {
            AVLoadingIndicatorView v = mContentWrapper.findViewById(R.id.avi);
            final View progross = mContentWrapper.findViewById(R.id.frcenterProgressBar);
            if (v != null && progross.getVisibility() != View.VISIBLE) {
                mContentWrapper.setClickable(true);
                progross.setVisibility(View.VISIBLE);
                v.setVisibility(View.VISIBLE);
                v.show();
            }
        }
    }

    protected boolean dismissLoading() {
        if (mContentWrapper != null) {
            AVLoadingIndicatorView v = mContentWrapper.findViewById(R.id.avi);
            final View progross = mContentWrapper.findViewById(R.id.frcenterProgressBar);
            if (v != null && progross.getVisibility() == View.VISIBLE) {
                mContentWrapper.setClickable(false);
                v.setVisibility(View.INVISIBLE);
                v.hide();
                progross.setVisibility(View.GONE);
                return true;
            }
        }
        return false;
    }


    /**
     * @param position
     */
    protected void notifyItemChanged(int position) {
        getAdapter().resetHolderWithPosition(position);
    }

    /**
     * @param itemDecoration
     */
    protected void addDecorator(RecyclerView.ItemDecoration itemDecoration) {
        if (mListView != null && itemDecoration != null) {
            mListView.addItemDecoration(itemDecoration);
        }
    }

    @Override
    public boolean onBackPressed() {
        super.onBackPressed();
        return dismissLoading();
    }
}
