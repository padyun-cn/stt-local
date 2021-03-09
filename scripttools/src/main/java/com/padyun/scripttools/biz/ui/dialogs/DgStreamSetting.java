package com.padyun.scripttools.biz.ui.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.uls.utilites.un.Useless;
import com.padyun.scripttools.R;


/**
 * Created by daiepngfei on 1/29/19
 */
public class DgStreamSetting extends DgSimpleBase {
    private OnCreateListener l;

    @SuppressLint("SetTextI18n")
    public DgStreamSetting(Context context) {
        super(context);
    }

    public DgStreamSetting(Context context, OnCreateListener l) {
        super(context);
        init(l);
    }

    public void init(OnCreateListener l) {
        final Entity entity = new Entity();
        this.l = l;
        ((EditText) findViewById(R.id.edit)).setInputType(InputType.TYPE_CLASS_NUMBER);
        ((EditText) findViewById(R.id.edit)).setHint("单位秒");
        ((TextView) findViewById(R.id.title)).setText("设置");
        findViewById(R.id.button).setOnClickListener(v -> {
            final String name = ((EditText) findViewById(R.id.edit)).getText().toString();
//            if (CUtils.isEmpty(name)) {
//                //Toast.makeText(getContext(), "请输入时间", Toast.LENGTH_SHORT).show();
//                dismiss();
//            } else {
                entity.time = Useless.stringToInt(name);
                if (this.l != null) l.onCreate(entity);
                dismiss();
//            }
        });

        final SharedPreferences sp = getContext().getSharedPreferences("StreamSetting", Context.MODE_PRIVATE);
        final int logFilterType = sp.getInt("log_filter_type", 0);
        final String[] data = new String[]{"全部", "通过", "未通过"};
        Spinner spinner = findViewById(R.id.spin_log_filter);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, data);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                entity.fileType = position;
                sp.edit().putInt("log_filter_type", position).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner.setSelection(logFilterType);

    }


    @Override
    public void show() {
        super.show();
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.dg_stream_setting;
    }

    public static class Entity {
        public int time;
        public int fileType;
    }

    public interface OnCreateListener {
        void onCreate(Entity name);
    }
}
