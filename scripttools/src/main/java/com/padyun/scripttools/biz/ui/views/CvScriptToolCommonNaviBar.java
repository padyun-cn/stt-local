package com.padyun.scripttools.biz.ui.views;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.padyun.scripttools.R;
import com.uls.utilites.ui.DensityUtils;

import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * Created by daiepngfei on 9/29/19
 */
public class CvScriptToolCommonNaviBar extends ConstraintLayout {
    public CvScriptToolCommonNaviBar(Context context) {
        super(context);
        loadFromRes();
    }

    public CvScriptToolCommonNaviBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadFromRes();
    }

    public CvScriptToolCommonNaviBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadFromRes();
    }

    private void loadFromRes(){
        LayoutInflater.from(getContext()).inflate(R.layout.layout_script_navigation_bar, this, true);
        setBackgroundResource(R.drawable.gradient_script_tool_navi_bg);
    }

    public void init(Activity activity, FrameLayout container, String title){
        findViewById(R.id.naviButtonLeft).setOnClickListener(v -> activity.finish());
        ((TextView)findViewById(R.id.naviTextTitle)).setText(title);
        if(container != null) {
            setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, DensityUtils.dip2px(getContext(), 50)));
            container.addView(this);
        }
    }

    public void setBackEnbale(boolean enbale) {
        findViewById(R.id.naviButtonLeft).setVisibility(enbale ? View.VISIBLE : View.INVISIBLE);
    }

    public void setRightImageButton(int res, View.OnClickListener l){
        ((ImageView)findViewById(R.id.naviButtonRight)).setVisibility(View.VISIBLE);
        ((ImageView)findViewById(R.id.naviButtonRight)).setImageResource(res);
        ((ImageView)findViewById(R.id.naviButtonRight)).setOnClickListener(l);
    }

    public void setRightTextButton(String text, View.OnClickListener l){
        ((TextView)findViewById(R.id.naviTextRight)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.naviTextRight)).setText(text);
        ((TextView)findViewById(R.id.naviTextRight)).setOnClickListener(l);
    }

    public void setTitle(String title){
        ((TextView)findViewById(R.id.naviTextTitle)).setText(title);
    }
}
