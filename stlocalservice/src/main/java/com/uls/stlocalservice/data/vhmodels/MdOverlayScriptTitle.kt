package com.uls.stlocalservice.data.vhmodels

import com.mon.ui.list.compat.adapterForViews.View_IBaseModel
import com.padyun.scripttools.R
import com.padyun.scripttools.compat.data.CoScript
import com.uls.utilites.io.Files
import com.uls.utilites.un.Useless
import java.io.File


/**
 * Created by daiepngfei on 9/20/19
 */
open class MdOverlayScriptTitle(var file: File, val script: CoScript) : View_IBaseModel {
    companion object {
        const val STATE_RUNNING_PRE = -100
        const val STATE_IDLE = 0
        const val STATE_RUNNING = 1
        const val STATE_PAUSED = 2

    }
    private var state: Int = 0
    var enableEditing: Boolean = false
    var subTitle: String? = null


    override fun getTypeItemLayoutId(): Int = R.layout.item_custom_script_item
    fun getPath() = file.absolutePath!!
    fun getName() = Files.nameSfx(file)!!
    fun setStateWithId(id: String, state: Int): Boolean{
        if(Useless.equals(id, script.id)){
            this.state = state
            return true
        } else {
            this.state = STATE_IDLE
        }
        return false
    }
    fun isStateRunning() = state == STATE_RUNNING
    fun isStateRunningPre() = state == STATE_RUNNING_PRE
}