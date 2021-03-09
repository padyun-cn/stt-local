package com.uls.stlocalservice.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.TimePicker;

import com.padyun.scripttools.biz.ui.dialogs.DgV2ScriptBase;
import com.uls.stlocalservice.R;
import com.uls.utilites.content.ToastUtils;

import androidx.annotation.NonNull;

/**
 * Created by daiepngfei on 2021-03-01
 */
public class DgScriptAlarm extends DgV2ScriptBase {
    private TimePicker timePickerStart, timePickerEnd;
    private TextView button;

    private Integer startHour = null;
    private Integer startMin = null;
    private Integer endHour = null;
    private Integer endMin = null;
    private OnTimeChangedListener onTimeChangedListener = null;
    private OnResetListener onResetListener;

    public DgScriptAlarm(@NonNull Context context) {
        super(context);
    }

    public interface OnTimeChangedListener {
        void onTimeChanged(int startHour, int startMin, int endHour, int endMin);
    }

    public interface OnResetListener {
        void onReset();
    }

    public void init(Integer startHour, Integer startMin, Integer endHour, Integer endMin, OnTimeChangedListener onTimeChangedListener, OnResetListener onResetListener) {
        this.startHour = startHour;
        this.startMin = startMin;
        this.endMin = endMin;
        this.endHour = endHour;
        this.onTimeChangedListener = onTimeChangedListener;
        this.onResetListener = onResetListener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timePickerStart = findViewById(R.id.timePickerStart);
        timePickerStart.setIs24HourView(true);
        if(startHour != null && startMin != null){
            timePickerStart.setCurrentHour(startHour);
            timePickerStart.setCurrentMinute(startMin);
        }
        timePickerEnd = findViewById(R.id.timePickerEnd);
        timePickerEnd.setIs24HourView(true);
        if(endHour != null && endMin != null){
            timePickerEnd.setCurrentHour(endHour);
            timePickerEnd.setCurrentMinute(endMin);
        }

        button = findViewById(R.id.button_finish);
        button.setOnClickListener(v -> {
            final int _endHour = timePickerEnd.getCurrentHour();
            final int _endMin = timePickerEnd.getCurrentMinute();
            final int _startHour = timePickerStart.getCurrentHour();
            final int _startMin = timePickerStart.getCurrentMinute();
            if (_startHour > _endHour || _startHour == _endHour && _startMin >= _endMin) {
                ToastUtils.showToast(getContext(), "结束时间必须大于开始时间");
            } else {
                if(startMin == null || endMin == null || startHour == null || endHour == null
                        || _startMin != startMin || _endMin != endMin ||_startHour != startHour || _endHour != endHour
                ){
                    if(onTimeChangedListener != null){
                        onTimeChangedListener.onTimeChanged(_startHour, _startMin, _endHour, _endMin);
                    }
                }
                dismiss();
            }
        });

        findViewById(R.id.button_reset).setOnClickListener(v -> {
            if (this.onResetListener != null) {
                this.onResetListener.onReset();
            }
            dismiss();
        });
    }

    @Override
    protected int getContentLayout() {
        return R.layout.dg_time_period;
    }
}
