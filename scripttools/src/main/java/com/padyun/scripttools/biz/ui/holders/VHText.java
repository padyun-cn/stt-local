package com.padyun.scripttools.biz.ui.holders;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import  com.mon.ui.list.compat.adapter.BaseRecyclerHolder;
import com.mon.ui.list.compat.adapter.BaseV2RecyclerAdapter;
import com.padyun.scripttools.R;
import com.padyun.scripttools.biz.ui.data.VHMText;


/**
 * Created by daiepngfei on 6/24/19
 */
public class VHText extends BaseRecyclerHolder<VHMText> {

    private TextView content;
    private View line;
    private boolean isNoHeader, isNoFooter;

    public VHText(@NonNull View itemView) {
        this(itemView, false, false);
    }

    public VHText(@NonNull View itemView, boolean hasNoHeader, boolean hasNoFooter) {
        super(itemView);
        this.isNoHeader = hasNoHeader;
        this.isNoFooter = hasNoFooter;
    }

    @Override
    public void init(View itemView) {
        content = itemView.findViewById(R.id.text);
        line = itemView.findViewById(R.id.line);
    }

    @Override
    protected void set(@NonNull Activity act, @NonNull BaseV2RecyclerAdapter adapter, @NonNull VHMText data, int position) {
        if (!isNoHeader && position == 0) {
            content.setBackgroundResource(R.drawable.selector_item_round_half_t);
        } else if (!isNoFooter && position == adapter.getItemCount() - 1) {
            content.setBackgroundResource(R.drawable.selector_item_round_half_b);
        } else content.setBackgroundResource(R.drawable.selector_item_round_half_mid);
        content.setText(data.getContent());
        line.setVisibility(position == adapter.getItemCount() - 1 ? View.INVISIBLE : View.VISIBLE);
        if (data.getTextColor() != null) content.setTextColor(data.getTextColor());
        content.setEnabled(!data.isDisable());
    }
}
