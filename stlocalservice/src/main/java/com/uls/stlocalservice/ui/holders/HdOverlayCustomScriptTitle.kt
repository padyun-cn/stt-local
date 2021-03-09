package com.uls.stlocalservice.ui.holders

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.mon.ui.list.compat.adapterForViews.View_BaseAdapter
import com.mon.ui.list.compat.adapterForViews.View_BaseHolder
import com.padyun.scripttools.R
import com.uls.stlocalservice.data.vhmodels.MdOverlayScriptTitle
import com.uls.stlocalservice.ui.floating.OverlayScriptList_View
import com.uls.utilites.un.Useless

/**
 * Created by daiepngfei on 9/20/19
 */
class HdOverlayCustomScriptTitle(itemView: View?) : View_BaseHolder<MdOverlayScriptTitle>(itemView) {
    private var titleText: TextView? = null
    private var subTitleText: TextView? = null
    private var buttonIcon: ImageView? = null
    private var buttonAdd: View? = null

    override fun init(view: View) {
        titleText = view.findViewById(R.id.title)
        subTitleText = view.findViewById(R.id.subTitle)
        buttonIcon = view.findViewById(R.id.button_icon_v2_task_oprator)
        buttonAdd = view.findViewById(R.id.button_add)
    }

    override fun set(act: Context, adapter: View_BaseAdapter, item: MdOverlayScriptTitle, position: Int) {
        titleText?.text = Useless.nonNullStr(item.getName())
        subTitleText?.text = item.subTitle
        subTitleText?.visibility = if(item.subTitle == null) View.GONE else View.VISIBLE
        buttonIcon?.setImageResource(if (item.isStateRunning() || item.isStateRunningPre())
            R.drawable.ic_script_button_remove_task else R.drawable.ic_script_button_add_task)
        buttonAdd?.setOnClickListener {
            sendItemMessage(if(item.isStateRunning() || item.isStateRunningPre())
                OverlayScriptList_View.MSG_POST_DEL else OverlayScriptList_View.MSG_POST_ADD, item)
        }
    }
}