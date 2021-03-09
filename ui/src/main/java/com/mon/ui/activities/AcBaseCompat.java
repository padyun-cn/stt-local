package com.mon.ui.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.mon.ui.R;
import com.mon.ui.list.compat.fragment.IFragmentGenerator;

import java.util.HashSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * Created by daiepngfei on 9/24/19
 */
public class AcBaseCompat extends AppCompatActivity {

    private Fragment mCurFragment;
    private IFragmentGenerator mFragmentGenerator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        onCreateRestoreInstance(savedInstanceState);
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        if (isSetFullScreen()) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        super.setContentView(R.layout.ac_core_container);
        tryAutoConfigFragmentGenerator();
        init();
    }

    /**
     *
     */
    protected void init() {
        final View bottomView = onCreateBottomView();
        if (bottomView != null) {
            final FrameLayout bottomLayout = findViewById(R.id.bottom_bar);
            bottomLayout.setVisibility(View.VISIBLE);
            bottomLayout.addView(bottomView);
        }
        onCreateTopView(findViewById(R.id.top_bar), R.id.top_bar);
        onCreateContent(findViewById(R.id.content_container), R.id.content_container);
    }

    protected void onCreateRestoreInstance(Bundle restoreInstanceState) {

    }

    protected void onCreateContent(@NonNull FrameLayout viewById, int content_container) {
    }

    protected View onCreateBottomView() {
        return null;
    }

    protected void onCreateTopView(@NonNull FrameLayout frameLayout, int resId) {
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

    private HashSet<String> mFragmentAddingTags = new HashSet<>();

    /**
     * @param tag
     */
    protected void doSwitchFragmentWithTag(String tag) {
        final IFragmentGenerator fragmentGenerator = mFragmentGenerator;
        if (fragmentGenerator != null) {
            final FragmentManager fragmentManager = getSupportFragmentManager();
            final Fragment fr = fragmentManager.findFragmentByTag(tag);
            if (fr == null) {
                if (mFragmentAddingTags.contains(tag)) {
                    // the fragment with this tag might in adding process
                    return;
                }
            }
            if (mCurFragment != null) {
                fragmentManager.beginTransaction().hide(mCurFragment).commitAllowingStateLoss();
            }
            mCurFragment = fr;
            if (mCurFragment == null) {
                mCurFragment = fragmentGenerator.onCreateFragmentWithTag(tag);
                if (mCurFragment != null) {
                    mFragmentAddingTags.add(tag);
                    fragmentManager.beginTransaction().add(fragmentGenerator.getFragmentContainerId(tag), mCurFragment, tag).commitAllowingStateLoss();
                }
            } else {
                fragmentManager.beginTransaction().show(mCurFragment).commitAllowingStateLoss();
            }
        }
    }

    protected void hideFragment(String tag) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final Fragment fr = fragmentManager.findFragmentByTag(tag);
        if (fr != null) {
            fragmentManager.beginTransaction().hide(fr).commitAllowingStateLoss();
        }
    }


    protected boolean isSetFullScreen() {
        return false;
    }

    protected void switchPortrait() {
        screenSwitch(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    protected void switchLandScape() {
        System.out.println("AcScriptTestingFullScreen::switchLandScape--Screen");
        screenSwitch(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private boolean screenSwitch(int screen) {
        if (getRequestedOrientation() != screen) {
            setRequestedOrientation(screen);
            return true;
        }
        return false;
    }

    @Override
    public final void setContentView(View view) {
        // super.setContentView(view);
    }

    @Override
    public final void setContentView(int layoutResID) {
        // super.setContentView(layoutResID);
    }

    @Override
    public final void setContentView(View view, ViewGroup.LayoutParams params) {
        // super.setContentView(view, params);
    }
}
