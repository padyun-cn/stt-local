package com.padyun.scripttools.compat.data.logs

import com.mon.ui.list.compat.adapter.IBaseRecyclerModel
import com.padyun.scripttools.R
import com.padyun.scripttools.compat.data.*

/**
 * Created by daiepngfei on 2019-12-11
 */
class CoLogItem<T : AbsCoConditon>(val itemData: T) : IBaseRecyclerModel {
    var label = "未执行"
    var number = 0
    var textColor = 0
    override fun getTypeItemLayoutId(): Int = when (itemData) {
        is CoOffset -> R.layout.item_log_offset
        is CoClick -> R.layout.item_log_click
        is CoFinish -> R.layout.item_log_finish
        is CoSlide -> R.layout.item_log_slide
        else -> 0
    }
}