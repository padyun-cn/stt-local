package com.padyun.core.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.mon.ui.dialog.AbsDgV2Base

/**
 * Created by daiepngfei on 7/20/18
 */
class CoreDgV2Alert(context: Context) : AbsDgV2Base(context) {
    override fun onInflatingContentView(inflater: LayoutInflater): View? = null
    companion object {
        @JvmStatic
        fun toCreate(context: Context): CoreDgV2Alert = CoreDgV2Alert(context)
    }
}




