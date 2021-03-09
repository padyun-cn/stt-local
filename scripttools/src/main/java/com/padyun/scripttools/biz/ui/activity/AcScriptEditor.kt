package com.padyun.scripttools.biz.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import androidx.fragment.app.Fragment
import com.mon.ui.activities.AbsAcFmSimple
import com.padyun.scripttools.biz.ui.fragment.FmScriptEditor

/**
 * Created by daiepngfei on 9/24/19
 */
open class AcScriptEditor : AbsAcFmSimple() {

    companion object {
//        init {
//            System.loadLibrary("opencv_java3")
//        }
        @JvmStatic
        fun launch(act: Context, scriptPath: String) {
            val intent = Intent(act, AcScriptEditor::class.java)
            if(act !is Activity){
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            intent.putExtra(FmScriptEditor.FILE_PATH, scriptPath)
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