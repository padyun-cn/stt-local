package com.padyun.scripttools.compat.data.logs

import com.mon.ui.list.compat.adapter.IBaseRecyclerModel
import com.padyun.scripttools.R
import com.padyun.scripttools.compat.data.AbsCoImage

/**
 * Created by daiepngfei on 2019-12-11
 */
class CoLogSpItem(val itemData: AbsCoImage<*>, val triggled: Boolean = false) : IBaseRecyclerModel {
    var number = 0
    override fun getTypeItemLayoutId(): Int = R.layout.item_log_sp
}