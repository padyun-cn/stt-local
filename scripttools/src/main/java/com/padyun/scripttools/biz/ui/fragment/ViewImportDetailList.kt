package com.padyun.scripttools.biz.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.util.Consumer
import com.mon.ui.buildup.ViewSimpleList
import com.mon.ui.list.compat.adapter.BaseRecyclerHolder
import com.mon.ui.list.compat.adapter.IBaseRecyclerModel
import com.padyun.scripttools.R
import com.padyun.scripttools.biz.ui.data.*
import com.padyun.scripttools.biz.ui.holders.imports.*
import com.padyun.scripttools.compat.data.*
import com.padyun.scripttools.content.data.CustomScriptIOManager
import com.uls.utilites.un.Useless

/**
 * Created by daiepngfei on 9/20/19
 */
@SuppressLint("ViewConstructor")
class ViewImportDetailList(context: Activity) : ViewSimpleList(context) {
    companion object {
        const val ON_CHECKED_CHANGED = 10023
    }

    private var callback: Handler.Callback? = null
    private var userIdOrMobile: String? = null
    private var gameId: String? = null
    private var currentScript: CoScript? = null
    private var currentScriptFile: String? = null
    private var onSelectedFinishListener: OnSelectedFinishListener? = null

    interface OnSelectedFinishListener {
        fun onSeletedFinish(items: List<AbsCoConditon>)
    }

    constructor(context: Activity, userIdOrMobile: String, gameId: String) : this(context) {
        this.userIdOrMobile = userIdOrMobile
        this.gameId = gameId
    }


    override fun getCustomConfigOption(): ConfigOption {
        val config = genDefaultConfigOption()
        config.setRootBackgroundColor(Color.WHITE)
        return config
    }

    override fun onInitBottomView(inflater: LayoutInflater?, topLayout: FrameLayout?) {
        val view = inflater?.inflate(R.layout.include_script_button_done, topLayout, true)
        view?.findViewById<View>(R.id.button_finish)?.setOnClickListener {
            if (onSelectedFinishListener != null) {
                onSelectedFinishListener!!.onSeletedFinish(getAllImportsConditions())
            }
        }
    }

    fun setCheckedAll(isChecked: Boolean) {
        foreachData(AbsCoImportCondition::class.java) {
            it.setChecked(isChecked)
        }
        listAdapter?.notifyDataSetChanged()
    }

    fun setOnItemSelectedListener(l: OnSelectedFinishListener?) {
        this.onSelectedFinishListener = l
    }

    override fun onCreateEmptyView(inflater: LayoutInflater?, container: ViewGroup?): View? {
        val view = inflater?.inflate(R.layout.view_empty_script_editor, container, false)
        view?.findViewById<View>(R.id.buttonEmptyGoEdit)?.visibility = View.INVISIBLE
        return view
    }

    override fun generateVHByType(root: View, type: Int): BaseRecyclerHolder<*>? {
        /*val root = LayoutInflater.from(itemView.codec_context).inflate(if (type == 0) R.layout.item_empty else type, itemView, false)*/
        return when (type) {
            R.layout.item_script_import_detail_click -> CoHolderImportClick(root)
            R.layout.item_script_import_detail_click_m -> CoHolderImportClickM(root)
            R.layout.item_script_import_detail_offset -> CoHolderImportOffset(root)
            R.layout.item_script_import_detail_offset_m -> CoHolderImportOffsetM(root)
            R.layout.item_script_import_detail_slide -> CoHolderImportSlide(root)
            R.layout.item_script_import_detail_slide_m -> CoHolderImportSlideM(root)
            R.layout.item_script_import_detail_finish -> CoHolderImportFinish(root)
            else -> null
        }
    }

    override fun handleMessage(msg: Message): Boolean {
        callback?.handleMessage(msg)
        return super.handleMessage(msg)
    }

    fun setCallback(callback: Handler.Callback) {
        this.callback = callback
    }

    fun getCheckAllState(): Boolean {
        for(item in data){
            if(item is AbsCoImportCondition<*> && !item.isChecked){
                return false
            }
        }
        return true
    }

    override fun onLoadListFirstTime() {

    }

    fun load(file: String) {
        currentScriptFile = file
        if (!TextUtils.isEmpty(currentScriptFile)) {
            CustomScriptIOManager.getCoScriptFromFileAsync(currentScriptFile!!, Consumer { sce ->
                if (sce != null) {
                    currentScript = sce
                    if (sce.conditions != null && sce.conditions.size > 0) {
                        val list: ArrayList<IBaseRecyclerModel> = ArrayList()
                        Useless.foreach(sce.conditions) { cond ->
                            when (cond) {
                                is CoOffset -> list.add(CoImportOffset(cond, true))
                                is CoClick -> list.add(CoImportClick(cond, true))
                                is CoSlide -> list.add(CoImportSlide(cond, true))
                                is CoFinish -> list.add(CoImportFinish(cond, true))
                            }
                        }
                        listRefresh(list)
                    }
                }
            })
        }
    }

    private fun getAllImportsConditions(): List<AbsCoConditon> {
        val list = ArrayList<AbsCoConditon>()
        foreachData(AbsCoImportCondition::class.java) {
            if (it.isChecked) {
                list.add(it.condition)
            }
        }
        return list
    }


}