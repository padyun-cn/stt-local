package com.padyun.scripttools.biz.ui.activity

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import androidx.fragment.app.Fragment
import com.mon.ui.activities.AbsAcFmSimple
import com.padyun.scripttools.biz.ui.fragment.FmScriptEditor
import com.padyun.scripttools.compat.test.ScriptTestConfig
import com.uls.utilites.content.ToastUtils
import com.uls.utilites.un.Useless

/**
 * Created by daiepngfei on 9/24/19
 */
class AcScriptEditorBk : AbsAcFmSimple() {

    companion object {
//        init {
//            System.loadLibrary("opencv_java3")
//        }
        @JvmStatic
        fun launch(act: Context, scriptPath: String, ip: String, asip: String, verify: String, userIdOrMobile: String?, gameId: String?) {
            if(Useless.hasEmptyIn(ip, scriptPath)){
                ToastUtils.show(act, "网络错误，请稍后重试！")
                return
            }
            ScriptTestConfig.setServerIp(ip)
            ScriptTestConfig.setAsIp(asip)
            ScriptTestConfig.setVerify(verify)
            val intent = Intent(act, AcScriptEditorBk::class.java)
            intent.putExtra(FmScriptEditor.FILE_PATH, scriptPath)
            intent.putExtra(FmScriptEditor.UID, userIdOrMobile)
            intent.putExtra(FmScriptEditor.GAME_ID, gameId)
            act.startActivity(intent)
        }
    }

    override fun onCreateFragment(): Fragment = FmScriptEditor.newInstance(intent.extras)

    override fun finish() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        }
        super.finish()
    }
}