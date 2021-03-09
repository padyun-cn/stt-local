package com.uls.stlocal;

import android.os.Bundle;

import com.mon.ui.activities.AbsAcFmSimple;
import com.uls.stlocalservice.ui.fragment.FmGameList;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MainActivity extends AbsAcFmSimple {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment onCreateFragment() {
        return new FmGameList();
    }
}
