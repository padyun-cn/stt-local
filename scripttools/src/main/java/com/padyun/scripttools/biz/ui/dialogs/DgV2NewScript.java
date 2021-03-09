package com.padyun.scripttools.biz.ui.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.padyun.scripttools.R;
import com.uls.utilites.ui.Viewor;
import com.uls.utilites.un.Useless;

/**
 * Created by daiepngfei on 1/29/19
 */
public class DgV2NewScript extends DgSimpleBase {
    private OnCreateListener l;

    @SuppressLint("SetTextI18n")
    public DgV2NewScript(Context context) {
        super(context);
    }

    public void init(OnCreateListener l) {
        init(false, null, null, null, null, l);
    }

    public void init(boolean noName, String title, String gname, String sip, String asip, OnCreateListener l) {
        this.l = l;
        if (title != null) ((TextView) findViewById(R.id.title)).setText(title);
        if (noName) Viewor.gone(findViewById(R.id.layout_script_name), findViewById(R.id.l1));
        final EditText editNameText = findViewById(R.id.editName);
        editNameText.setText(Useless.nonNullStr(gname));
        final EditText editServerIpText = findViewById(R.id.editServerIp);
        editServerIpText.setText(Useless.nonNullStr(sip));
        final EditText editAsIpText = findViewById(R.id.editAsIp);
        editAsIpText.setText(Useless.nonNullStr(asip));
        findViewById(R.id.cancel).setOnClickListener(v -> dismiss());
        findViewById(R.id.confirm).setOnClickListener(v -> {
            final String name = editNameText.getText().toString();
            final String serverIp = editServerIpText.getText().toString();
            final String asIp = editAsIpText.getText().toString();
            if (findViewById(R.id.layout_script_name).getVisibility() == View.VISIBLE && Useless.isEmpty(name)) {
                Toast.makeText(getContext(), "请输入" + "名称", Toast.LENGTH_SHORT).show();
            } /*else if (CUtils.isEmpty(serverIp)) {
                Toast.makeText(getContext(), "请输入" + "Server-Ip", Toast.LENGTH_SHORT).show();
            } else if (CUtils.isEmpty(asIp)) {
                Toast.makeText(getContext(), "请输入" + "名称", Toast.LENGTH_SHORT).show();
            }*/ else {
                if (this.l != null) l.onCreate(DgV2NewScript.this, name, serverIp, asIp);
            }
        });

        /*final Spinner spinner = findViewById(R.id.spinner);
        List<AsipHistory.HistItem> items = AsipHistory.getHistoryList(this.getContext());
        if (items == null || items.size() == 0) {
            findViewById(R.id.spinnerLayout).setVisibility(View.GONE);
            return;
        }

        findViewById(R.id.buttonSpinner).setOnClickListener(v -> spinner.performClick());

        findViewById(R.id.spinnerLayout).setVisibility(View.VISIBLE);
        final String[] data = new String[items.size() + 1];
        data[0] = "点击选择一条历史记录";
        for (int i = 0; i < items.size(); i++) {
            AsipHistory.HistItem item = items.httpGet(i);
            if (item == null) {
                data[i + 1] = "";
                continue;
            }
            data[i + 1] = item.serverIp + "   " + item.asIp;
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_text, data);
        spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position > 0){
                    AsipHistory.HistItem item = items.httpGet(position - 1);
                    editServerIpText.setText(item.serverIp);
                    editAsIpText.setText(item.asIp);
                } else {
                    editServerIpText.setText("");
                    editAsIpText.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner.setSelection(0);*/
    }

    @Override
    public void dismiss() {
        super.dismiss();
        dismissLoading();
    }

    public void showLoading(){
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
    }

    public void dismissLoading(){
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.dg_v2_new_script;
    }

    public interface OnCreateListener {
        void onCreate(DgV2NewScript dialogInterface, String name, String sip, String asip);
    }
}
