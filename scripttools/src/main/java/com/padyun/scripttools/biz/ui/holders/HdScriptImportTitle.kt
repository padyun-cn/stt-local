package com.padyun.scripttools.biz.ui.holders

import android.app.Activity
import android.view.View
import android.widget.TextView
import  com.mon.ui.list.compat.adapter.BaseRecyclerHolder
import com.mon.ui.list.compat.adapter.BaseV2RecyclerAdapter
import com.uls.utilites.un.Useless
import com.padyun.scripttools.R
import com.padyun.scripttools.biz.ui.data.MdScriptImportTitle

/**
 * Created by daiepngfei on 9/20/19
 */
class HdScriptImportTitle(itemView: View?) : BaseRecyclerHolder<MdScriptImportTitle>(itemView) {
    private var titleText: TextView? = null
    override fun init(view: View) {
        titleText = view.findViewById(R.id.title)
    }

    override fun set(act: Activity, adapter: BaseV2RecyclerAdapter, item: MdScriptImportTitle, position: Int) {
        titleText?.text = Useless.nonNullStr(item.getName())
    }
}