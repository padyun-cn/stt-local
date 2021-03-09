package com.uls.stlocalservice.ui.floating;

import android.view.Gravity;
import android.view.View;
import android.widget.EditText;

import com.mon.ui.floating.OverlayContext;
import com.mon.ui.floating.OverlayDialog;
import com.mon.ui.floating.OverlayParamFactory;
import com.uls.stlocalservice.R;
import com.uls.utilites.un.Useless;

/**
 * Created by daiepngfei on 2021-01-19
 */
public class OverlayNewScriptDialog extends OverlayDialog {
    private OnCreateListener l;

    public OverlayNewScriptDialog(OverlayContext context, OnCreateListener l) {
        super(context);
        this.l = l;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        setContentView(R.layout.overdg_new_script, OverlayParamFactory.matchParent(Gravity.CENTER));
        init(l);
    }


    private void init(OnCreateListener l) {
        this.l = l;
        final EditText editNameText = (EditText) findViewById(R.id.editName);
        editNameText.setText(Useless.nonNullStr(null));
        final EditText editServerIpText = (EditText) findViewById(R.id.editServerIp);
        editServerIpText.setText(Useless.nonNullStr(null));
        final EditText editAsIpText = (EditText) findViewById(R.id.editAsIp);
        editAsIpText.setText(Useless.nonNullStr(null));
        findViewById(R.id.cancel).setOnClickListener(v -> dismiss());
        findViewById(R.id.confirm).setOnClickListener(v -> {
            final String name = editNameText.getText().toString();
            final String serverIp = editServerIpText.getText().toString();
            final String asIp = editAsIpText.getText().toString();
            if (findViewById(R.id.layout_script_name).getVisibility() == View.VISIBLE && Useless.isEmpty(name)) {
                // Toast.makeText(getContext(), "请输入" + "名称", Toast.LENGTH_SHORT).show();
            } else {
                if (this.l != null) l.onCreate(OverlayNewScriptDialog.this, name, serverIp, asIp);
            }
        });


    }

    public interface OnCreateListener {
        void onCreate(OverlayNewScriptDialog dialogInterface, String name, String sip, String asip);
    }
}
