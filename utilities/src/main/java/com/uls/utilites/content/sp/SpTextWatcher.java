package com.uls.utilites.content.sp;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by daiepngfei on 6/26/19
 */
public class SpTextWatcher implements TextWatcher {
    private final TC tc;

    public SpTextWatcher(TC tc){ this.tc = tc;}
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(tc != null) tc.onTextChanged(charSequence.toString());
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    public interface TC {
        void onTextChanged(String s);
    }
}
