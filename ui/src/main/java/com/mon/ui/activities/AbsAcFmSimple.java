package com.mon.ui.activities;

import android.widget.FrameLayout;

import com.mon.ui.list.compat.fragment.IFragmentGenerator;
import com.mon.ui.list.compat.fragment.IKeyAttacher;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * Created by daiepngfei on 9/24/19
 */
public abstract class AbsAcFmSimple extends AcBaseCompat {
    private Fragment mCurFragment;
    private IFragmentGenerator mFragmentGenerator;

    @Override
    protected void onCreateContent(@NonNull FrameLayout viewById, int content_container) {
        tryAutoConfigFragmentGenerator();
        Fragment fragmentByTag = getSupportFragmentManager().findFragmentByTag(getClass().getSimpleName());
        if(fragmentByTag == null){
            fragmentByTag = onCreateFragment();
        }
        if(fragmentByTag != null) {
            getSupportFragmentManager()
                    .beginTransaction().replace(content_container, fragmentByTag, getClass().getSimpleName()).commit();
            mCurFragment = fragmentByTag;
        }
    }

    /**
     *
     */
    private void tryAutoConfigFragmentGenerator() {
        if (IFragmentGenerator.class.isInstance(this) && isAutoConfigFragmentgenerator()) {
            configiFragmentGenerator((IFragmentGenerator) this);
        }
    }

    /**
     * @return
     */
    protected boolean isAutoConfigFragmentgenerator() {
        return true;
    }

    /**
     * @return
     */
    protected Fragment getCurrentShowingFragment() {
        return mCurFragment;
    }

    /**
     * @param fragmentGenerator
     */
    protected void configiFragmentGenerator(IFragmentGenerator fragmentGenerator) {
        this.mFragmentGenerator = fragmentGenerator;
    }

    @Override
    public void onBackPressed() {
        Fragment fr = getCurrentShowingFragment();
        boolean handle = false;
        if (IKeyAttacher.class.isInstance(fr)) {
            ((IKeyAttacher) fr).onBackPressed();
        }
        super.onBackPressed();
    }

    /**
     * @param tag
     */
    protected void doSwitchFragmentWithTag(String tag) {
        final IFragmentGenerator fragmentGenerator = mFragmentGenerator;
        if (fragmentGenerator != null) {
            final FragmentManager fragmentManager = getSupportFragmentManager();
            final Fragment fr = fragmentManager.findFragmentByTag(tag);
            if (mCurFragment != null) {
                fragmentManager.beginTransaction().hide(mCurFragment).commitAllowingStateLoss();
            }
            mCurFragment = fr;
            if (mCurFragment == null) {
                mCurFragment = fragmentGenerator.onCreateFragmentWithTag(tag);
                fragmentManager.beginTransaction().add(fragmentGenerator.getFragmentContainerId(tag), mCurFragment, tag).commitAllowingStateLoss();
            } else {
                fragmentManager.beginTransaction().show(mCurFragment).commitAllowingStateLoss();
            }
        }
    }

    protected abstract Fragment onCreateFragment();

}
