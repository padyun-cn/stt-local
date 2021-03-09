package com.padyun.scripttools.biz.ui.dialogs;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mon.ui.list.compat.adapter.BaseV2RecyclerAdapter;
import com.mon.ui.list.compat.adapter.IBaseRecyclerModel;
import com.padyun.scripttools.R;
import com.padyun.scripttoolscore.compatible.data.model.SEScript;
import com.uls.utilites.ui.DensityUtils;
import com.uls.utilites.un.Useless;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by daiepngfei on 6/21/19
 */
public abstract class AbsDgV2ONBase<T> extends DgV2TipsBase implements  BaseV2RecyclerAdapter.VHComponent {
    private RecyclerView mListView;
    private BaseV2RecyclerAdapter mAdapter;
    private Callback<T> mCallback;
    private SEScript mScript;
    private int mTargetSceneId;
    private ImageView mEmptyImg;
    private FrameLayout mContentContainer;
    private Activity mActivity;

    public AbsDgV2ONBase(@NonNull Context context) {
        super(context);
        setCanceledOnTouchOutside(true);
        mEmptyImg = findViewById(R.id.img_empty);
        mEmptyImg.setVisibility(View.INVISIBLE);
    }

    public void init(Activity act, SEScript seScript, int sceId, Callback<T> callback) {

        this.mActivity = act;
        this.mCallback = callback;
        this.mScript = seScript;
        this.mTargetSceneId = sceId;
        final View.OnClickListener floatingOnClickListener = getFloatingButtonClickListener();
        findViewById(R.id.floatbutton).setOnClickListener(floatingOnClickListener == null ? view -> doCallback(Result.ACTION, null) : floatingOnClickListener);
        onInitView();

        // set adapter data
        final List<IBaseRecyclerModel> models = getItems(sceId);
        if (!Useless.isEmpty(models)) mEmptyImg.setVisibility(View.INVISIBLE);
        mAdapter = new BaseV2RecyclerAdapter(act, this, null);
        mListView.setAdapter(mAdapter);

        onDetailShowOrDismiss();
    }

    protected void showEmptyView(boolean show){
        if(mEmptyImg != null){
            mEmptyImg.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        }
        if(mListView != null){
            mListView.setVisibility(!show ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void onDetailShowOrDismiss() {
        ((TextView) findViewById(R.id.tips)).setText(isDetailMode() ? Useless.nonNullStr(getCustomDetailTips()) : Useless.nonNullStr(getCustomTips()));
    }

    public RecyclerView getListView() {
        return mListView;
    }

    protected abstract boolean isDetailMode();

    protected String getCustomTips() {
        return null;
    }

    protected String getCustomDetailTips() {
        return null;
    }

    protected void hideTitlebar(boolean hide) {
        findViewById(R.id.title_bar).setVisibility(hide ? View.GONE : View.VISIBLE);
        findViewById(R.id.layout_content_container).setBackgroundResource(hide ? R.drawable.bg_dg_shadow_card_fff8f7f7 : R.drawable.bg_dg_shadow_card_half_b_fff8f7f7);
    }

    protected Activity getActivity() {
        return mActivity;
    }

    protected View.OnClickListener getFloatingButtonClickListener() {
        return null;
    }

    protected abstract void onInitView();

    protected List<IBaseRecyclerModel> getItems(int sceId) {
        return null;
    }



    protected Callback getCallback() {
        return mCallback;
    }

    public boolean doCallback(Result r, T data) {
        return doCallback(r, "", data);
    }

    public boolean doCallback(Result r, String key, T data) {
        boolean result = false;
        if (mCallback != null) {
            result = true;
            mCallback.onCallback(this, r, key, data);
        }
        return result;
    }

    public int getTargetSceneId() {
        return mTargetSceneId;
    }

    @Override
    public final void onInitialize() {
        super.onInitialize();
        mContentContainer = findViewById(R.id.tips_sub_content_container);
        final View view = getContentView();
        if (view != null) mContentContainer.addView(view);
        mListView = findViewById(R.id.list);
        mListView.setLayoutManager(getLayoutManager());
        final RecyclerView.ItemDecoration decoration = getDecor();
        if (decoration != null) mListView.addItemDecoration(decoration);
    }

    protected void showContentView(boolean show) {
        mContentContainer.setVisibility(show ? View.VISIBLE : View.GONE);
        mListView.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        mEmptyImg.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        findViewById(R.id.floatbuttonLayout).setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        onDetailShowOrDismiss();
    }

    protected View getContentView() {
        return getContentLayoutId() == 0 ? null : LayoutInflater.from(getContext()).inflate(getContentLayoutId(), null);
    }

    protected int getContentLayoutId() {
        return 0;
    }

    protected RecyclerView.ItemDecoration getDecor() {
        return null;
    }

    @Override
    public void show() {
        super.show();
    }

    protected RecyclerView.ItemDecoration genDefaultDecor() {
        final int margin_out = DensityUtils.dip2px(getContext(), 1);
        final Paint paint = new Paint();
        paint.setColor(Color.parseColor("#D8D8D8"));
        paint.setAntiAlias(true);
        return new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                final int pos = parent.getChildAdapterPosition(view);
                if (pos > 0) {
                    outRect.top = margin_out;
                }
            }

            @Override
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int childCount = parent.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = parent.getChildAt(i);
                    c.drawLine(child.getLeft(), child.getBottom(), child.getRight(), child.getBottom(), paint);
                }
            }
        };
    }

    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getContext());
    }

    @Override
    protected int getTipsContentLayout() {
        return R.layout.include_dg_v2_tips_below_on_base;
    }

    public BaseV2RecyclerAdapter getAdapter() {
        return mAdapter;
    }

    public SEScript getScript() {
        return mScript;
    }


}
