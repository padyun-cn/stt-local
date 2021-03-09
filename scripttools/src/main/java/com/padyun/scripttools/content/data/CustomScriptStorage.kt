package com.padyun.scripttools.content.data

import com.padyun.scripttools.compat.data.CoScript
import com.padyun.scripttools.content.async.AsyncBool
import com.uls.utilites.content.CoreWorkers
import com.padyun.scripttoolscore.tools.SceCryptor
import com.uls.utilites.io.Files
import java.io.File

/**
 * Created by daiepngfei on 9/20/19
 */
object CustomScriptStorage {
    private val mLock = Object()
    private var mSyncTime = 0L

    @JvmStatic
    fun saveAsync(path: String, script: CoScript, resultHandler: CoreWorkers.IResult<Boolean>?) {
        val time = System.currentTimeMillis()
        AsyncBool(CoreWorkers.Work<Boolean> { savingSync(time, path, script) }, resultHandler).start()
    }

    @JvmStatic
    private fun savingSync(time: Long, path: String, script: CoScript): Boolean {
        return synchronized(mLock) {
            if (time < mSyncTime) {
                return false
            }
            mSyncTime = time
            internalSave(path, script)
        }
    }

    @JvmStatic
    fun directSave(path: String, script: CoScript?): Boolean {
        return internalSave(path, script)
    }

    internal fun internalSave(path: String, script: CoScript?): Boolean {
        if (script == null) {
            return false
        }
        val encPath = "$path.enc"
        if (Files.exists(encPath)) {
            Files.delete(encPath)
        }
        val encFile = File(encPath)
        val json = script.buildToJson()
        val done = Files.writeBytes(encFile, SceCryptor.encrypt(json))
        if (done) {
            val newPath = if (path.endsWith(".sce")) Files.nameSfx(path) + ".ai" else path
            encFile.renameTo(File(newPath))
        }
        return done
    }

    @JvmStatic
    fun saveSync(path: String, script: CoScript): Boolean {
        return savingSync(System.currentTimeMillis(), path, script)
    }

    @JvmStatic
    fun openAsync(path: String, resultHandler: CoreWorkers.IResult<String>) {
        CoreWorkers.exec(CoreWorkers.Work<String> { open(path) }, resultHandler)
    }

    @JvmStatic
    fun open(path: String): String? {
        return try {
            val s = Files.readBytes(File(path))
            SceCryptor.decrypt(s)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @JvmStatic
    fun getAllScripts(): Array<File> {
        val f = File(FPathScript.scriptDataDir())
        return f.listFiles { _, name -> if (name == null) false else name.endsWith(FPathScript.DEXT) || name.endsWith(FPathScript.DEXT_OLD) }
    }

}