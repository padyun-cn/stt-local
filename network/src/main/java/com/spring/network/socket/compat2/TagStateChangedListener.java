package com.spring.network.socket.compat2;


import com.uls.utilites.utils.LogUtil;

/**
 * Created by daiepngfei on 11/1/19
 */
public class TagStateChangedListener implements OnStateChangedListener {

    private String tag;
    private OnStateChangedListener l;

    public TagStateChangedListener(String tag, OnStateChangedListener l) {
        this.tag = tag;
        this.l = l;
    }

    @Override
    public final void onConnStateChanged(SockConnState state) {
        if (tag != null) {
            final String msg = "[Thread: " + Thread.currentThread().getName() + "] [stateTag: " + tag + "] Current state is " + state.name();
            final String intervalTag = state == SockConnState.START_READING || state == SockConnState.STOP_READING ? msg : null;
            LogUtil.d("TagStateChangedListener", msg, intervalTag, 5000);
        }
        if (l != null) {
            l.onConnStateChanged(state);
        }
    }

}
