package com.mon.ui.list.compat.fragment;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;

import com.mon.ui.list.compat.adapter.BaseRecyclerHolder;
import com.mon.ui.list.compat.annotations.FormedRecyclerAdapter;
import com.mon.ui.list.compat.annotations.FormedRecyclerBase;
import com.mon.ui.list.compat.annotations.ViewHolder;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import androidx.annotation.NonNull;

/**
 * Created by daiepngfei on 2020-11-16
 */
public abstract class FormedVHBaseFragment extends AbsFmV2RecyclerBase {

    private SparseArray<Class<? extends BaseRecyclerHolder>> classSparseArray = new SparseArray<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FormedRecyclerAdapter adapter = getClass().getAnnotation(FormedRecyclerAdapter.class);
        if(adapter != null){
            ViewHolder[] holders = adapter.value();
            for (ViewHolder holder : holders) {
                int layoutId = holder.layoutId();
                Class<? extends BaseRecyclerHolder> holderClass = holder.holder();
                if (layoutId != 0) {
                    classSparseArray.put(layoutId, holderClass);
                }
            }
        }
    }

    @Override
    protected final void initData(int nextPageIndex, int pageSize, boolean isRefresh) {
        onLoadingData(nextPageIndex, pageSize, isRefresh);
    }

    @Override
    public BaseRecyclerHolder generateVHByType(@NonNull View itemView, int viewType) {
        Class<? extends BaseRecyclerHolder> holderClass = classSparseArray.get(viewType);
        if (holderClass != null) {
            try {
                Constructor<? extends BaseRecyclerHolder> constructor = holderClass.getConstructor(View.class);
                return constructor.newInstance(itemView);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    protected abstract void onLoadingData(int nextPageIndex, int pageSize, boolean clear);

    @Override
    protected boolean isEnableLoadingMore() {
        FormedRecyclerBase base = getClass().getAnnotation(FormedRecyclerBase.class);
        if(base != null){
            return base.loadingMore();
        }
        return false;
    }

    @Override
    protected boolean isEnablePullToRefresh() {
        FormedRecyclerBase base = getClass().getAnnotation(FormedRecyclerBase.class);
        if(base != null){
            return base.pullToRefresh();
        }
        return false;
    }
}
