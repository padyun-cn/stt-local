package com.padyun.scripttools.biz.ui.holders

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.mon.ui.list.compat.adapter.BaseRecyclerHolder
import com.mon.ui.list.compat.adapter.BaseV2RecyclerAdapter
import com.padyun.scripttools.R
import com.padyun.scripttools.biz.ui.data.MdCustomScriptTitle
import com.padyun.scripttools.biz.ui.fragment.FmCustomScriptTitleList
import com.uls.utilites.un.Useless

/**
 * Created by daiepngfei on 9/20/19
 */
class HdCustomScriptTitle(itemView: View?) : BaseRecyclerHolder<MdCustomScriptTitle>(itemView) {
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

    override fun set(act: Activity, adapter: BaseV2RecyclerAdapter, item: MdCustomScriptTitle, position: Int) {
        titleText?.text = Useless.nonNullStr(item.getName())
        subTitleText?.text = item.subTitle
        subTitleText?.visibility = if(item.subTitle == null) View.GONE else View.VISIBLE
        buttonIcon?.setImageResource(if (item.isStateRunning()) R.drawable.ic_script_button_remove_task else R.drawable.ic_script_button_add_task)
        buttonAdd?.setOnClickListener {
            sendItemMessage(if(item.isStateRunning()) FmCustomScriptTitleList.MSG_POST_DEL else FmCustomScriptTitleList.MSG_POST_ADD, item)
        }
    }
}