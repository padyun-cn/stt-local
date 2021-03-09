package com.padyun.scripttools.biz.ui.holders

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.mon.ui.list.compat.adapter.BaseRecyclerHolder
import com.mon.ui.list.compat.adapter.BaseV2RecyclerAdapter
import com.padyun.scripttools.R
import com.padyun.scripttools.biz.ui.data.MdFavoredUserTask
import com.padyun.scripttools.biz.ui.fragment.FmCustomScriptTitleList
import com.uls.utilites.content.ToastUtils
import com.uls.utilites.un.Useless

/**
 * Created by daiepngfei on 9/20/19
 */
class HdFavoredUserTaskTitle(itemView: View?) : BaseRecyclerHolder<MdFavoredUserTask>(itemView) {
    private var titleText: TextView? = null
    private var subTitleText: TextView? = null
    private var buttonIcon: ImageView? = null
    private var buttonAdd: View? = null
    private var iconFlag: ImageView? = null

    override fun init(view: View) {
        titleText = view.findViewById(R.id.title)
        subTitleText = view.findViewById(R.id.subTitle)
        buttonIcon = view.findViewById(R.id.button_icon_v2_task_oprator)
        buttonAdd = view.findViewById(R.id.button_add)
        iconFlag = view.findViewById(R.id.flagIcon)
    }

    override fun set(act: Activity, adapter: BaseV2RecyclerAdapter, item: MdFavoredUserTask, position: Int) {
        buttonIcon?.setImageResource(R.drawable.ic_script_button_add_task)
        titleText?.text = Useless.nonNullStr(item.task_name)
        buttonIcon?.setImageResource(if (item.isTaskAdded) R.drawable.ic_script_button_remove_task else R.drawable.ic_script_button_add_task)
        when (item.status) {
            0 -> {
                buttonAdd?.setOnClickListener {
                    if (item.hasCensoredVersion()) {
                        sendItemMessage(if (item.isTaskAdded) FmCustomScriptTitleList.MSG_POST_DEL else FmCustomScriptTitleList.MSG_POST_ADD, item)
                    } else {
                        //ToastUtils.show(act, "当前没有可运行的版本")
                        ToastUtils.show(act, "当前任务还在审核中")
                    }
                }
            }
            1 -> {
                buttonAdd?.setOnClickListener {
                    sendItemMessage(if (item.isTaskAdded) FmCustomScriptTitleList.MSG_POST_DEL else FmCustomScriptTitleList.MSG_POST_ADD, item)
                }
            }
            2 -> {
                buttonAdd?.setOnClickListener {
                    ToastUtils.show(act, "当前任务已经被拒绝，详情请联系客服咨询。")
                }
            }
        }
        if (item.isCreator /*&& !StContext.script().hasLocalTask(item.id)*/) {
            subTitleText?.visibility = View.VISIBLE
            iconFlag?.visibility = View.VISIBLE
            iconFlag?.setImageResource(R.drawable.ic_script_home_bottom_bar_remote_checked)
            when (item.status) {
                0 -> {
                    subTitleText?.text = "审核中"
                }
                1 -> {
                    subTitleText?.visibility = View.GONE
                    subTitleText?.text = null
                }
                2 -> {
                    subTitleText?.text = "被拒绝"
                }
            }

        } else {
            iconFlag?.visibility = View.GONE
            subTitleText?.visibility = View.GONE
            subTitleText?.text = null
        }
        // subTitleText?.visibility = if(item.subTitle == null) View.GONE else View.VISIBLE

    }
}