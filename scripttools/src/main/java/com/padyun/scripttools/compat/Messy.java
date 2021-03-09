package com.padyun.scripttools.compat;

import android.text.SpannableStringBuilder;

import com.uls.utilites.un.Useless;
import com.padyun.scripttoolscore.compatible.data.model.coord.SECoordFixed;
import com.padyun.scripttoolscore.compatible.data.model.range.SERangeSize;

/**
 * Created by daiepngfei on 9/23/19
 */
public class Messy {
    public static void getFixedRegionSpan(SpannableStringBuilder builder, SECoordFixed fixed, SERangeSize size) {
        if(Useless.nulls(builder, fixed, size)) return;
        final int x = fixed.getX();
        final int y = fixed.getY();
        final int r = size.getW() + x;
        final int b = size.getH() + y;
        builder.append("<");
        builder.append(String.valueOf(x)).append(",");
        builder.append(String.valueOf(y)).append(",");
        builder.append(String.valueOf(r)).append(",");
        builder.append(String.valueOf(b));
        builder.append(">");
    }
}
