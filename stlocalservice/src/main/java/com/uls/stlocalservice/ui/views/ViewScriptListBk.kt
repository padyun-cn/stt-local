package com.uls.stlocalservice.ui.views

import android.app.Activity
import android.app.Service
import android.content.ComponentName
import android.content.DialogInterface
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.AttributeSet
import android.view.View
import androidx.core.util.Consumer
import com.mon.ui.buildup.ViewSimpleList
import com.mon.ui.list.compat.adapter.BaseRecyclerHolder
import com.mon.ui.list.compat.adapter.IBaseRecyclerModel
import com.padyun.core.dialogs.CoreDgV2Alert
import com.padyun.manifest.ApiStatusCodes
import com.padyun.scripttools.biz.ui.content.Constants
import com.padyun.scripttools.biz.ui.data.MdCustomScriptTitle
import com.padyun.scripttools.biz.ui.data.StRespBaseObj
import com.padyun.scripttools.biz.ui.dialogs.upload.DgLiteProgress
import com.padyun.scripttools.biz.ui.fragment.FmCustomScriptHome
import com.padyun.scripttools.biz.ui.holders.HdCustomScriptTitle
import com.uls.utilites.content.CoreWorkers
import com.padyun.scripttools.content.data.CustomScriptIOManager
import com.padyun.scripttools.content.data.CustomScriptStorage
import com.padyun.scripttools.content.network.ScriptTestProxyService
import com.padyun.scripttools.module.runtime.ScriptManager
import com.padyun.scripttools.module.runtime.StContext
import com.padyun.scripttools.module.runtime.StIntentActions
import com.padyun.scripttools.module.runtime.test.TestProxy
import com.padyun.scripttools.services.biz.UploadScriptService
import com.padyun.scripttoolscore.content.network.SockResponseUtils
import com.padyun.scripttoolscore.models.HttpParam
import com.uls.utilites.common.ICCallback
import com.uls.utilites.content.ToastUtils
import com.uls.utilites.io.Files
import com.uls.utilites.un.Useless
import com.uls.utilites.un.Useless.runOnUiThread
import com.uls.utilites.utils.LogUtil
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.IOException

/**
 * Created by daiepngfei on 2021-01-18
 */
@Suppress("unused")
class ViewScriptListBk : ViewSimpleList {


    constructor(context: Activity) : super(context)
    constructor(context: Activity, attrs: AttributeSet?) : super(context, attrs)

    private var isProcessing: Boolean = false
    private var testProxy: TestProxy? = null
    var runningScriptNameChangeListener: Consumer<String>? = null
    var uploadServiceConnection: ServiceConnection? = null
    var mUploadScriptService: UploadScriptService? = null
    var mDgUploadProgress: DgLiteProgress? = null
    private var mCustomScriptHomeProxy: FmCustomScriptHome.CustomScriptHomeProxy? = null

    fun setCustomScriptHomeProxy(proxy: FmCustomScriptHome.CustomScriptHomeProxy?) {
        this.mCustomScriptHomeProxy = proxy
    }

    override fun generateVHByType(itemView: View, viewType: Int): BaseRecyclerHolder<*> = HdCustomScriptTitle(itemView)

    override fun onLoadListFirstTime() {
        // testProxy = ScriptTestProxyService.apply(this, getArgString(KEY_IP), getArgString(KEY_ASIP))
        testProxy = ScriptTestProxyService.apply(StContext.getInstance().manifest.deviceIp,
                StContext.getInstance().manifest.deviceAsIp)
        load()
    }

    private fun load() {

        val userIdOrMobile: String = StContext.getInstance().manifest.userId
        val gameId: String = StContext.getInstance().manifest.gameId
        val scriptFiles = CustomScriptIOManager.getCustomScriptFilesWithGameId(userIdOrMobile, gameId)
        // Toast.makeText(context!!, "" + userIdOrMobile + " | " + gameId + " | " + FPathScript.scriptDataDir(), Toast.LENGTH_LONG).show()
        if (scriptFiles != null && scriptFiles.isNotEmpty()) {
            showLoading()
            CoreWorkers<List<IBaseRecyclerModel>>(CoreWorkers.Work {
                val list: MutableList<IBaseRecyclerModel> = ArrayList()
                Useless.foreach(scriptFiles) {
                    var f = it
                    /*compat code*/
                    var dPath = f.absolutePath
                    if (".sce" == Files.ext(dPath)) {
                        // dPath = Files_.replaceExt(dPath, "ai")
                        dPath = dPath.replace(".sce", ".ai")
                        if (Files.exists(f.absolutePath)) {
                            val script = CustomScriptIOManager.parseToCoScript(Files.readWholeFileString(f.absoluteFile))
                            if (CustomScriptStorage.directSave(dPath, script)) {
                                Files.delete(f.absoluteFile)
                                f = File(dPath)
                            }
                        }
                    }
                    /*compat code*/
                    val coScript = CustomScriptIOManager.getCoScriptFromFileSync(f.absolutePath)
                    if (coScript != null && coScript.id != null) {
                        if (!Useless.isEmpty(coScript.publishedID)) {
                            StContext.script().registerOrUpdateScriptDesc(coScript.publishedID, ScriptManager.SFileDesc(dPath, coScript.publishingCode))
                        }
                        list.add(MdCustomScriptTitle(f, coScript))
                    }
                }
                list
            }, CoreWorkers.IResult {
                dismissLoading()
                listRefresh(it)
                checkUploadingScript()
                checkRunningScript()
            }).start()
        }
    }

    private fun checkUploadingScript() {
        withBindedUploadService(Runnable { foreachData(MdCustomScriptTitle::class.java) { mUploadScriptService?.queryState(it.getPath(), mOnUploadListener) } })
    }

    private fun checkRunningScript() {

        StContext.network().applyTest().getCurrentRunningScript(object : ICCallback<String> {

            override fun onSuccess(t: String?) {
                LogUtil.d(this@ViewScriptListBk.javaClass.simpleName, "current running script is : $t")
                try {
                    val json = JSONObject(t)
                    val id = json.getString("id")
                    val state = json.getInt("state")
                    runOnUiThread(Runnable {
                        foreachData(MdCustomScriptTitle::class.java) {
                            if (it.setStateWithId(id, state)) {
                                runningScriptNameChangeListener?.accept(it.getName())
                            }
                        }
                        refreshList()
                    })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onSendFail(errno: Int, msg: String?, e: Exception?) {
                LogUtil.w(this@ViewScriptListBk.javaClass.simpleName, "error : $msg", e)
            }
        })
    }

    private fun removeTask(title: MdCustomScriptTitle) {
        isProcessing = true
        showLoading()
        testProxy?.sendCmdStop(
                object : SockResponseUtils.SimpleOkResponse() {

                    override fun onFail(errno: Int, msg: String?, e: Exception?) {
                        isProcessing = false
                        dismissLoading()
                        ToastUtils.show(context!!, "网络错误，请稍候重试")
                    }

                    override fun onResponseOk() {
                        dismissLoading()
                        isProcessing = false
                        runningScriptNameChangeListener?.accept("")
                        foreachData(MdCustomScriptTitle::class.java) { it.setStateWithId(title.script.id, MdCustomScriptTitle.STATE_IDLE) }
                        refreshList()
                    }

                })
    }

    private fun withRemoteTasksClear(title: MdCustomScriptTitle, then: Runnable) {
        showLoading()
        StContext.network().http().post(Constants.ApiPath.Phrase.POST_TASK_STATUS,
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        onCallbackError("网络错误，请稍候重试")
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val obj = StRespBaseObj.parse(response)
                        if(obj == null){
                            onCallbackError("网络错误，请稍候重试")
                            return
                        }
                        if (obj.code == 0) {
                            dismissLoading()
                            then.run()
                        } else {
                            runOnUiThread(Runnable {
                                dismissLoading()
                                if (obj.code == ApiStatusCodes.TASK_RUNNING_TYPE_USER || obj.code == ApiStatusCodes.TASK_RUNNING_TYPE_SYSTEM) {
                                    showJZDialog(obj)
                                } else {
                                    ToastUtils.show(context!!, obj.msg)
                                }
                                isProcessing = false
                            })
                        }
                    }

                    private fun showJZDialog(obj: StRespBaseObj) {
                        CoreDgV2Alert.toCreate(context!!)
                                .setTitle("温馨提示")
                                .setMessage(obj.msg)
                                .setNegativeButton("取消", DialogInterface.OnClickListener { d, _ ->
                                    run {
                                        isProcessing = false
                                        dismissLoading()
                                        d.dismiss()
                                    }
                                })
                                .setPositiveButton("确定", DialogInterface.OnClickListener { d, _ ->
                                    run {
                                        dismissLoading()
                                        d.dismiss()
                                        doClearTasks()
                                    }
                                })
                                .show()
                    }

                    private fun doClearTasks() {
                        showLoading()
                        StContext.network().http().post(Constants.ApiPath.Phrase.POST_CLEAR_TASK,
                                object : Callback {
                                    override fun onFailure(call: Call, e: IOException) {
                                        onCallbackError("网络错误，请稍候重试")
                                    }

                                    override fun onResponse(call: Call, response: Response) {
                                        val obj = StRespBaseObj.parse(response)
                                        if(obj == null){
                                            onCallbackError("网络错误，请稍候重试")
                                            return
                                        }
                                        if (obj.code == 0) {
                                            runOnUiThread(Runnable {
                                                dismissLoading()
                                                mCustomScriptHomeProxy?.markTitleListUpdated()
                                                val intent = Intent()
                                                intent.putExtra("flag", FmCustomScriptHome::class.java.simpleName)
                                                StContext.getInstance().sendStBroadcast(StIntentActions.CLEAR_REMOTE_TASKS, intent)
                                                then.run()
                                            })
                                        } else {
                                            onCallbackError(obj.msg)
                                        }
                                    }
                                },
                                HttpParam("device_id", StContext.manifest().deviceId),
                                HttpParam("game_id", StContext.manifest().gameId),
                                HttpParam("task_id", title.script.publishedID)
                        )
                    }

                    private fun onCallbackError(msg: String) {
                        runOnUiThread(Runnable {
                            dismissLoading()
                            isProcessing = false
                            ToastUtils.show(context!!, msg)
                        })
                    }
                }
                ,
                HttpParam("device_id", StContext.manifest().deviceId),
                HttpParam("game_id", StContext.manifest().gameId),
                HttpParam("task_id", title.script.publishedID)
        )
    }

    private fun addSocketTask(title: MdCustomScriptTitle) {
        showLoading()
        CustomScriptIOManager.getCoScriptFromFileAsync(title.getPath(), Consumer {
            testProxy?.sendStartCmd(it.buildToSEScript().buildToJson(), it.allSEImageDeDuplicated,
                    object : SockResponseUtils.SimpleOkResponse() {

                        override fun onFail(errno: Int, msg: String?, e: Exception?) {
                            isProcessing = false
                            dismissLoading()
                            ToastUtils.show(context!!, "网络错误，请稍候重试")
                        }

                        override fun onResponseOk() {
                            isProcessing = false
                            dismissLoading()
                            runningScriptNameChangeListener?.accept(title.getName())
                            foreachData(MdCustomScriptTitle::class.java) { it.setStateWithId(title.script.id, MdCustomScriptTitle.STATE_RUNNING) }
                            refreshList()
                        }

                    })
        })
    }

    /**
     * do
     */
    private fun withBindedUploadService(withing: Runnable?) {
        if (uploadServiceConnection == null || mUploadScriptService == null) {
            val uploadServiceIntent = Intent(context, UploadScriptService::class.java)
            context!!.startService(uploadServiceIntent)
            uploadServiceConnection = object : ServiceConnection {

                override fun onServiceDisconnected(name: ComponentName?) {
                    uploadServiceConnection = null
                }

                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    mUploadScriptService = (service as UploadScriptService.ScriptUploadBinder).uploaderService
                    mUploadScriptService?.addUploadingStateChangeListener(mOnUploadListener)
                    withing?.run()
                }
            }
            context!!.bindService(uploadServiceIntent, uploadServiceConnection!!, Service.BIND_AUTO_CREATE)
        } else {
            withing?.run()
        }
    }

    /**
     *
     */
    private val mOnUploadListener = UploadScriptService.OnUploadingStateChangedListener { data ->
        run {
            // httpGet & check the uploading-key
            val key = data.getStringExtra(UploadScriptService.KEY_KEY)
            if (key == null || key.isEmpty()) {
                return@OnUploadingStateChangedListener
            }

            // query the item with same key(file path)
            var item: MdCustomScriptTitle? = null
            for (i in 0..dataSize) {
                val obj = getData()[i]
                if (obj is MdCustomScriptTitle) {
                    if (key == obj.getPath()) {
                        item = obj
                        break
                    }
                }
            }
            if (item == null) {
                return@OnUploadingStateChangedListener
            }

            runOnUiThread(Runnable { handleUIWithUploadState(item, data, false) })
        }

    }
    private var mUploadingStateTipsDialog: CoreDgV2Alert? = null
    private fun showUploadingStateTips(s: String) {
        if (mUploadingStateTipsDialog == null) {
            mUploadingStateTipsDialog = CoreDgV2Alert.toCreate(context!!)
            mUploadingStateTipsDialog!!.setTitle("温馨提示")
                    .setExclusiveNaturalButton("知道了", DialogInterface.OnClickListener { d, _ -> run { d.dismiss() } })
        }
        mUploadingStateTipsDialog!!.setMessage(s).show()
    }

    /**
     *
     */
    private fun handleUIWithUploadState(item: MdCustomScriptTitle, data: Intent, forceShowDialog: Boolean) {
        item.enableEditing = true
        item.subTitle = null
        // httpGet running state
        val index = getData().indexOf(item)
        enableItemSwipe(index)
        when (data.getIntExtra(UploadScriptService.KEY_STATE, UploadScriptService.STATE_NONE)) {
            UploadScriptService.STATE_NONE -> {
                item.enableEditing = true
                item.subTitle = null
            }
            UploadScriptService.STATE_WAITTING -> {
                disableItemSwipe(index)
                item.subTitle = "等待中"
                item.enableEditing = false
            }
            UploadScriptService.STATE_RUNNING -> {
                item.enableEditing = false
                disableItemSwipe(index)
                val actionState = data.getIntExtra(UploadScriptService.KEY_UP_ACTION_STATE, UploadScriptService.ACTION_READY)
                item.subTitle = "正在上传中，请稍后"
                when (data.getIntExtra(UploadScriptService.KEY_UP_STATE, UploadScriptService.UP_STATE_READY)) {
                    UploadScriptService.UP_STATE_ZIPPING -> {
                        item.subTitle = "正在压缩，请稍后"
                        val dg = getUploadProgressBar()
                        /*if (actionState == ACTION_FAILED) {
                            dg.dismiss()
                            item.subTitle = "压缩失败，请稍后"
                            showUploadingStateTips("压缩文件失败，请稍候重试。")
                        } else {*/
                        if (forceShowDialog) {
                            dg.show()
                        }
                        if (dg.isShowing) {
                            //dg.setProgress(0.5f)
                            dg.setProgress(data.getFloatExtra(UploadScriptService.KEY_UP_PROGRESS, 0F))
                            val zippingInfo = Useless.nonNullStr(data.getStringExtra(UploadScriptService.KEY_ZIP_INFO))
                            dg.setProgressSubTitle(zippingInfo + if (data.getIntExtra(UploadScriptService.KEY_UP_TYPE, UploadScriptService.TYPE_PROCCESSED) == UploadScriptService.TYPE_PROCCESSED) "(1/2)" else "(2/2)")
                        }
                        /*}*/

                    }
                    UploadScriptService.UP_STATE_UPLOADING -> {
                        item.subTitle = "正在上传，请稍后"
                        val dg = getUploadProgressBar()
                        /*if (actionState == ACTION_FAILED) {
                            dg.dismiss()
                            item.subTitle = "上传失败，请稍后"
                            showUploadingStateTips("上传失败，请稍候重试。")
                        } else {*/
                        if (forceShowDialog) {
                            dg.show()
                        }
                        if (dg.isShowing) {
                            //dg.setProgress(0.5f)
                            dg.setProgressTitle("正在上传" + if (data.getIntExtra(UploadScriptService.KEY_UP_TYPE, UploadScriptService.TYPE_PROCCESSED) == UploadScriptService.TYPE_PROCCESSED) "脚本，请勿关闭客户端！"
                            else "可编辑的本地数据\n截图较大，可能耗时过长，请耐心等待" + "")
                            dg.setProgress(data.getFloatExtra(UploadScriptService.KEY_UP_PROGRESS, 0F))
                            dg.setProgressSubTitle(if (data.getIntExtra(UploadScriptService.KEY_UP_TYPE, UploadScriptService.TYPE_PROCCESSED) == UploadScriptService.TYPE_PROCCESSED) "(1/2)" else "(2/2)")
                        }
                        /*}*/
                    }
                    UploadScriptService.UP_STATE_PUBLISHING -> {
                        item.subTitle = "正在发布中，请稍后"
                    }
                    UploadScriptService.UP_STATE_COMPLETE -> {
                        item.subTitle = null
                        enableItemSwipe(index)
                        if (actionState == UploadScriptService.ACTION_DONE) {
                            getUploadProgressBar().dismiss()
                            showUploadingStateTips("上传成功，审核通过后将发送通知至【消息中心】，并开通分享任务给好友权限！")
                            if (data.getStringArrayExtra(UploadScriptService.KEY_PUBLISHED_ID) != null) {
                                item.script.publishedID = data.getStringExtra(UploadScriptService.KEY_PUBLISHED_ID)
                            }
                            if (mCustomScriptHomeProxy != null) {
                                mCustomScriptHomeProxy?.markTitleListUpdated()
                            }
                        }
                    }
                    UploadScriptService.UP_STATE_ERROR -> {
                        enableItemSwipe(index)
                        item.subTitle = "上传出现错误"
                    }

                }
            }
            UploadScriptService.STATE_REFUSED -> {
                val msg = data.getStringExtra(UploadScriptService.KEY_MSG)
                showUploadingStateTips(msg!!)
            }
            UploadScriptService.STATE_CANCELED -> {
                showUploadingStateTips("本次上传已经取消")
                enableItemSwipe(index)
            }
            UploadScriptService.STATE_ERROR -> {
                // todo: when ERROR_PUBLISHING etc.
                //item.subTitle = "发布失败，请稍后重试"
                getUploadProgressBar().dismiss()
                val msg = data.getStringExtra(UploadScriptService.KEY_MSG)
                showUploadingStateTips(if (Useless.isEmpty(msg)) "发布失败，请稍候重试。" else msg!!)
                enableItemSwipe(index)
            }
            else -> {
                enableItemSwipe(index)
            } // do nothing
        }
        listAdapter.notifyItemChanged(getData().indexOf(item))
    }

    private fun disableItemSwipe(index: Int) {
        if (index >= 0) {
            swipeRecyclerView.setSwipeItemMenuEnabled(index, false)
        }
    }

    private fun enableItemSwipe(index: Int) {
        if (index >= 0) {
            swipeRecyclerView.setSwipeItemMenuEnabled(index, true)
        }
    }

    private fun showUploadTips(m: MdCustomScriptTitle) {
        CoreDgV2Alert.toCreate(context!!).setTitle("上传说明")
                .setMessage("上传任务到云端分为两个部分：\n\n"
                        + "1.上传不可编辑的任务，作为审核用，上传完成后任务可添加，但不可编辑。\n\n"
                        + "2.上传所有可编辑的文件，上传完成后更换手机可以下载到新手机。\n\n"
                        + "注：可编辑文件为游戏截图，截图可能较大，建议在WIFI环境下上传。")
                .setPositiveButton("继续",
                        DialogInterface.OnClickListener { dialog, _ ->
                            run {
                                dialog.dismiss()
                                doCompress(m)
                            }
                        })
                .setNegativeButton("取消",
                        DialogInterface.OnClickListener { dialog, _ ->
                            run { dialog.dismiss() }
                        })
                .show()
    }

    private fun doCompress(m: MdCustomScriptTitle) {

        // progress dialog
        val dgUploadProgress = getUploadProgressBar()
        dgUploadProgress.setTitle("温馨提示")
        dgUploadProgress.setProgressTitle("正在压缩，请耐心等待")
        dgUploadProgress.setProgressSubTitle("(1/2)")
        dgUploadProgress.setProgress(0F)
        dgUploadProgress.setPositiveButton("最小化", DialogInterface.OnClickListener { dialog, _ -> run { dialog.dismiss() } })
        dgUploadProgress.setNegativeButton("取消操作", DialogInterface.OnClickListener { dialog, _ ->
            run {
                withBindedUploadService(Runnable {
                    mUploadScriptService?.cancel(m.getPath())
                    dialog.dismiss()
                })
            }
        })
        dgUploadProgress.show()

        // start service
        withBindedUploadService(Runnable { mUploadScriptService?.upload(m.getPath(), StContext.getInstance().manifest.gameId, StContext.getInstance().manifest.userToken) })
    }

    private fun getUploadProgressBar(): DgLiteProgress {
        if (mDgUploadProgress == null) {
            mDgUploadProgress = DgLiteProgress(context!!)
        }
        return mDgUploadProgress!!
    }


    fun onDestroy() {
        if (uploadServiceConnection != null) {
            mUploadScriptService?.removeUploadingStateChangeListener(mOnUploadListener)
            context!!.unbindService(uploadServiceConnection!!)
        }
    }

    fun reload() {
        load()
    }
}