package com.padyun.scripttools.biz.ui.dialogs;

import android.content.Context;
import android.content.SharedPreferences;

import com.padyun.scripttools.R;


/**
 * Created by daiepngfei on 7/10/19
 */
public class DgV2AlertSlideSetting extends DgSimpleBase {
    private static final String KEY = "Alert_Slide_Setting_Confirmed";
    public DgV2AlertSlideSetting(Context context) {
        super(context);
        final SharedPreferences sp = getContext().getSharedPreferences("Settings", Context.MODE_PRIVATE);

        findViewById(R.id.button_confirm).setOnClickListener(v -> dismiss());
        findViewById(R.id.checkbox_layout).setOnClickListener(v -> {
            final boolean confirmed = sp.getBoolean(KEY, false);
            sp.edit().putBoolean(KEY, !confirmed).apply();
            findViewById(R.id.checkbox).setBackgroundResource(!confirmed ? R.drawable.ic_check_yes : R.drawable.ic_check_false);
        });
    }

    public static void show(Context context){
        final SharedPreferences sp = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        final boolean confirmed = sp.getBoolean(KEY, false);
        if(!confirmed) new DgV2AlertSlideSetting(context).show();
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.dg_alert_slide_setting;
    }
}
