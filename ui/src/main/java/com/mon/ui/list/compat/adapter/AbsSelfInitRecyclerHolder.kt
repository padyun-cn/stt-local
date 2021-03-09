package com.mon.ui.list.compat.adapter

import android.view.View

/**
 * Created by daiepngfei on 5/18/18
 */
@Suppress("LeakingThis")
abstract class AbsSelfInitRecyclerHolder<B : IBaseRecyclerModel>(itemView: View) : BaseRecyclerHolder<B>(itemView) {
    init {
        initialize()
    }
}
