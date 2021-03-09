package com.padyun.scripttools.biz.ui.data

import com.padyun.scripttools.R
import com.padyun.scripttools.compat.data.CoScript
import java.io.File


/**
 * Created by daiepngfei on 9/20/19
 */
class MdScriptImportTitle(file: File, script: CoScript) : MdCustomScriptTitle(file, script) {
    override fun getTypeItemLayoutId(): Int = R.layout.item_script_import_item
}