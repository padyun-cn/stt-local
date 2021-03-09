@file:Suppress("DEPRECATION")

package com.padyun.scripttools.biz.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams
import android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mon.ui.list.compat.adapter.BaseRecyclerHolder
import com.mon.ui.list.compat.adapter.BaseV2RecyclerAdapter
import com.mon.ui.list.compat.adapter.IBaseRecyclerModel
import com.mon.ui.list.compat2.fragment.FmSimpleList
import com.padyun.scripttools.R
import com.padyun.scripttools.biz.ui.content.SECons
import com.padyun.scripttools.biz.ui.content.UScriptPlayer
import com.padyun.scripttools.biz.ui.dialogs.DgStreamSetting
import com.padyun.scripttools.biz.ui.holders.logs.CoHolderLogClick
import com.padyun.scripttools.biz.ui.holders.logs.CoHolderLogFinish
import com.padyun.scripttools.biz.ui.holders.logs.CoHolderLogOffset
import com.padyun.scripttools.biz.ui.holders.logs.CoHolderLogSlide
import com.padyun.scripttools.compat.data.AbsCoConditon
import com.padyun.scripttools.compat.data.logs.CoLogItem
import com.padyun.scripttools.compat.test.ScriptTestConfig
import com.padyun.scripttoolscore.compatible.plugin.RuntimeInfo
import com.padyun.scripttoolscore.compatible.plugin.ScriptServiceManager
import com.uls.utilites.content.ToastUtils
import com.uls.utilites.ui.Viewor
import com.yanzhenjie.recyclerview.SwipeRecyclerView

/**
 * Created by daiepngfei on 10/18/19
 */
@SuppressLint("UseSparseArrays")
class FmScriptTestingLog : FmSimpleList(), UScriptPlayer.ILogCatcher, UScriptPlayer.OnStateChangedListener {

    private var mLogFilterType: Int = 0
    private var mProgressDialog: ProgressDialog? = null
    private var playButton: View? = null

    private val logQueueHandler = Handler()
    private var mUScriptController: UScriptPlayer? = null


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (mUScriptController == null) {
            mUScriptController = UScriptPlayer(getArgString(SECons.Ints.KEY_SCRIPT_FILE_PATH), this, this)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mUScriptController?.onViewStateRestored(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        mUScriptController?.onViewStateRestored(savedInstanceState)
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
        when (newState) {
            RecyclerView.SCROLL_STATE_DRAGGING -> setEnableAutoScrollToBottom(false)
            RecyclerView.SCROLL_STATE_IDLE -> setEnableAutoScrollToBottom((recyclerView?.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() == itemCount - 1)
        }
    }

    @Synchronized
    private fun setEnableAutoScrollToBottom(enable: Boolean) {
        isAutoScrollToBottomEnable = enable
        logQueueHandler.removeCallbacksAndMessages(null)
    }

    override fun onInitSwipeReclcerView(swipeRecyclerView: SwipeRecyclerView?, mListAdapter: BaseV2RecyclerAdapter?) {
        mListAdapter?.setDataLimit(500)
        setEnableAutoScrollToBottom(true)
        mProgressDialog = ProgressDialog(activity!!)
        mLogFilterType = activity!!.getSharedPreferences("StreamSetting", Context.MODE_PRIVATE).getInt("log_filter_type", 0)
    }

    @SuppressLint("InflateParams")
    override fun onInitOverlayView(inflater: LayoutInflater?, overlayLayout: FrameLayout?) {
        val overlay = inflater?.inflate(R.layout.overlay_script_testing_tools, null, false)
        val layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.gravity = Gravity.END.or(Gravity.BOTTOM)
        overlay?.layoutParams = layoutParams
        overlay?.findViewById<View>(R.id.buttonScrollToBottom)?.setOnClickListener { setEnableAutoScrollToBottom(true) }
        playButton = overlay?.findViewById(R.id.buttonPauseOrResume)
        playButton?.isEnabled = false
        playButton?.setOnClickListener {
            mUScriptController?.start()
        }
        overlay?.findViewById<View>(R.id.buttonSetting)?.setOnClickListener {
            DgStreamSetting(activity) { time ->
                showProgressLoading()
                mLogFilterType = time.fileType
                ScriptServiceManager.setRuntimeInterval(ScriptTestConfig.getServerIp(), ScriptTestConfig.getAsIp(), time.time) { dismissProgressLoading() }
            }.show()
        }
        overlay?.findViewById<View>(R.id.buttonToggleTools)?.setOnClickListener {
            if (overlay.findViewById<View>(R.id.buttonSetting).visibility == View.VISIBLE) {
                overlay.findViewById<ImageView>(R.id.buttonToggleTools).setImageResource(R.drawable.ic_v2_expand_button)
                Viewor.invisible(overlay.findViewById<View>(R.id.buttonScrollToBottom), overlay.findViewById<View>(R.id.buttonPauseOrResume), overlay.findViewById<View>(R.id.buttonSetting))
            } else {
                overlay.findViewById<ImageView>(R.id.buttonToggleTools).setImageResource(R.drawable.ic_v2_collapse_button)
                Viewor.visible(overlay.findViewById<View>(R.id.buttonScrollToBottom), overlay.findViewById<View>(R.id.buttonPauseOrResume), overlay.findViewById<View>(R.id.buttonSetting))
            }
        }
        overlayLayout?.addView(overlay)
    }

    override fun generateVHByType(itemView: View, viewType: Int): BaseRecyclerHolder<*>? = when (viewType) {
        R.layout.item_log_click -> CoHolderLogClick(itemView)
        R.layout.item_log_offset -> CoHolderLogOffset(itemView)
        R.layout.item_log_finish -> CoHolderLogFinish(itemView)
        R.layout.item_log_slide -> CoHolderLogSlide(itemView)
        R.layout.item_log_statement -> LogHolderStatement(itemView)
        else -> null
    }

    private fun setPlayButtonDrawable(res: Int) {
        activity?.findViewById<ImageView>(R.id.buttonPauseOrResume)?.setImageResource(res)
    }


    /**
     *
     */
    private fun dismissProgressLoading() {
        mProgressDialog?.dismiss()
    }

    /**
     *
     */
    private fun showProgressLoading() {
        mProgressDialog?.show()
    }

    /**
     *
     */
    private fun printCoLogs(info: RuntimeInfo, condition: AbsCoConditon?): CoLogItem<*>? {
        if (info.entity == null || condition == null) {
            return null
        }
        val entity = info.entity
        if (condition.action_list != null && condition.item_list != null) {
            /*return when(condition){
                is CoClick -> CoLogItem<CoClick>(condition as CoClick)
                is CoOffset -> CoLogItem<CoOffset>(condition)
                is CoClick -> CoLogItem<CoClick>(condition)
                is CoClick -> CoLogItem<CoClick>(condition)
                else -> null
            }*/
            val coLogItem: CoLogItem<*> = CoLogItem(condition)
            coLogItem.textColor = if (entity.isIs_trigger) resources.getColor(R.color.color_log_green) else resources.getColor(R.color.color_log_red)
            coLogItem.label = if (entity.isIs_trigger) "执行: " else "未执行: "

            val type = mLogFilterType
            val append = type == 0 || type == 1 && info.entity.isIs_trigger || type == 2 && !info.entity.isIs_trigger
            if (append) {
                appendCoLog(coLogItem)
            }
            return coLogItem
        }
        return null
    }

    var logCount = 0

    @Synchronized
    private fun appendCoLog(log: CoLogItem<*>) {
        if (isAutoScrollToBottomEnable) {
            logQueueHandler.post {
                log.number = ++logCount
                listAppend(log)
            }
        }
    }


    inner class LogStatement(val text: String) : IBaseRecyclerModel {
        override fun getTypeItemLayoutId(): Int = R.layout.item_log_statement
    }

    inner class LogHolderStatement(itemView: View) : BaseRecyclerHolder<LogStatement>(itemView) {

        private var text: TextView? = null

        override fun init(view: View) {
            text = view.findViewById(R.id.text)
        }

        override fun set(act: Activity, adapter: BaseV2RecyclerAdapter, item: LogStatement, position: Int) {
            text?.text = item.text
        }
    }


    override fun onStart() {
        super.onStart()
        runOrDelayOnFirstRusume(Runnable {
            mUScriptController!!.start()
        })
    }

    override fun onResume() {
        super.onResume()
        mUScriptController?.resume()
    }

    override fun onStop() {
        super.onStop()
        mUScriptController?.pause(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        mUScriptController?.stop()
    }

    override fun onLoadListFirstTime() {
    }

    override fun onContinueCachingLogs(log: RuntimeInfo?, conditon: AbsCoConditon?, touched: Boolean) {
        if (log != null) {
            //printLogs(log)
            printCoLogs(log, conditon)
        }
    }

    override fun onScriptControlStateChanged(tstate: UScriptPlayer.State?) {
        when (tstate) {
            UScriptPlayer.State.IDLE -> {
                setPlayButtonDrawable(R.drawable.ic_script_testing_resume)
                playButton?.setOnClickListener {
                    mUScriptController?.start()
                }
            }
            UScriptPlayer.State.PAUSED -> {
                setPlayButtonDrawable(R.drawable.ic_script_testing_resume)
                playButton?.setOnClickListener {
                    mUScriptController?.resume()
                }
            }
            UScriptPlayer.State.RESUMED, UScriptPlayer.State.STARTED -> {
                setPlayButtonDrawable(R.drawable.ic_script_testing_pause)
                playButton?.setOnClickListener {
                    mUScriptController?.pause(false)
                }
            }
            UScriptPlayer.State.UNPAUSED,
            UScriptPlayer.State.UNRESUMED,
            UScriptPlayer.State.UNSTOPPED,
            UScriptPlayer.State.UNSTARTED ->
                ToastUtils.show(activity, "网络错误，请稍候再试。")
            else -> {
            }
        }

        when (tstate) {
            UScriptPlayer.State.PAUSING,
            UScriptPlayer.State.STARTING,
            UScriptPlayer.State.RESUMING -> {
                playButton?.isEnabled = false
            }
            else -> {
                playButton?.isEnabled = true
            }
        }
    }

}