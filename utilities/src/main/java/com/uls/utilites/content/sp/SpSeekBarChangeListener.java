package com.uls.utilites.content.sp;

import android.widget.SeekBar;

/**
 * Created by daiepngfei on 7/22/19
 */
public class SpSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
    private final TC tc;

    public interface TC {
        void onProgressChanged(int progress);
    }
    public SpSeekBarChangeListener(TC tc){
        this.tc = tc;
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(tc != null) tc.onProgressChanged(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
