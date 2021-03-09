package com.uls.stlocalservice.ui.holders;

import android.app.Activity;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mon.ui.list.compat.adapter.BaseRecyclerHolder;
import com.mon.ui.list.compat.adapter.BaseV2RecyclerAdapter;
import com.uls.stlocalservice.R;
import com.uls.stlocalservice.data.vhmodels.MdGame;
import com.uls.stlocalservice.ui.fragment.FmGameList;

import androidx.annotation.NonNull;

/**
 * Created by daiepngfei on 2020-11-11 这代码写的稀烂。。
 */
public class HdGame extends BaseRecyclerHolder<MdGame> {

    public HdGame(View itemView) {
        super(itemView);
    }

    private ImageView mGameIcon;
    private TextView mGameName, mChannelName;
    private View actionButton;

    @Override
    protected void init(@NonNull View view) {
        mGameIcon = view.findViewById(R.id.icon);
        mGameName = view.findViewById(R.id.gameName);
        mChannelName = view.findViewById(R.id.channelName);
        actionButton = view.findViewById(R.id.upload_button_text_layout);
    }

    @Override
    protected void set(@NonNull Activity act, @NonNull BaseV2RecyclerAdapter adapter, @NonNull MdGame item, int position) {
        mGameIcon.setImageDrawable(item.getDrawable());
        mGameName.setText(item.getName());
        actionButton.setOnClickListener(v -> {
            Message message = Message.obtain();
            message.what = FmGameList.MSG_START_GAME;
            message.obj = item;
            sendItemMessage(message);
        });
    }

}
