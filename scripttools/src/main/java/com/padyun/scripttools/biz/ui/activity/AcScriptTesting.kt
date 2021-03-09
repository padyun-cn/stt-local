package com.padyun.scripttools.biz.ui.activity

import android.view.LayoutInflater
import android.widget.FrameLayout
import com.mon.ui.activities.AcBaseCompat
import com.padyun.scripttools.R
import com.padyun.scripttools.biz.ui.fragment.FmScriptTestingLog
import com.padyun.scripttools.biz.ui.fragment.FmScriptTestingScreen
import com.padyun.scripttools.biz.ui.views.CvScriptToolCommonNaviBar

/**
 * Created by daiepngfei on 10/18/19
 */
class AcScriptTesting : AcBaseCompat() {
    private var testScreenFragment: FmScriptTestingScreen? = null
    private var testLogFragment: FmScriptTestingLog? = null
    private var firstTime = true
    override fun onCreateContent(viewById: FrameLayout, content_container: Int) {
        LayoutInflater.from(this)?.inflate(R.layout.ac_script_test, viewById, true)
        testScreenFragment = supportFragmentManager.findFragmentById(R.id.fr_test_screen) as FmScriptTestingScreen
        testLogFragment = supportFragmentManager.findFragmentById(R.id.fr_test_log) as FmScriptTestingLog
        testLogFragment?.arguments = intent.extras
    }

    override fun onStart() {
        super.onStart()
        if(firstTime){
            firstTime = false
            /*testScreenFragment?.startStream()
            testScreenFragment?.setScriptFilePath(Useless.nonNullStr(intent.getStringExtra(SECons.Ints.KEY_SCRIPT_FILE_PATH)))
            testScreenFragment?.setTaskName(Files.nameSfx(Useless.nonNullStr(intent.getStringExtra(SECons.Ints.KEY_SCRIPT_FILE_PATH))))*/
        }
    }

    override fun onCreateTopView(frameLayout: FrameLayout, resId: Int) {
        val v = CvScriptToolCommonNaviBar(this)
        v.init(this, findViewById(R.id.top_bar), "脚本日志")
    }

    override fun finish() {
        super.finish()
//        testLogFragment?.stopScript();
    }
}