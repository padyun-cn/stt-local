package com.mon.ui.list.compat.fragment;


import androidx.fragment.app.Fragment;

/**
 * Created by daiepngfei on 1/9/18
 */

public interface IFragmentGenerator {
    /**
     *
     * @return
     */
    int getFragmentContainerId(String tag);

    /**
     *
     * @param tag
     * @return
     */
    Fragment onCreateFragmentWithTag(String tag);
}
