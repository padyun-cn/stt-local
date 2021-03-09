package com.padyun.scripttools.biz.ui.fragment

import android.app.Service
import android.content.ComponentName
import android.content.DialogInterface
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Consumer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mon.ui.list.compat.adapter.BaseRecyclerHolder
import com.mon.ui.list.compat.adapter.BaseV2RecyclerAdapter
import com.mon.ui.list.compat.adapter.IBaseRecyclerModel
import com.padyun.core.dialogs.CoreDgV2Alert
import com.padyun.manifest.ApiStatusCodes
import com.padyun.scripttools.R
import com.padyun.scripttools.biz.ui.activity.AcScriptEditor
import com.padyun.scripttools.biz.ui.content.Constants
import com.padyun.scripttools.biz.ui.data.MdCustomScriptTitle
import com.padyun.scripttools.biz.ui.data.StRespBaseObj
import com.padyun.scripttools.biz.ui.dialogs.DgV2NewScript
import com.padyun.scripttools.biz.ui.dialogs.upload.DgLiteProgress
import com.padyun.scripttools.biz.ui.holders.HdCustomScriptTitle
import com.padyun.scripttools.compat.data.CoScript
import com.uls.utilites.content.CoreWorkers
import com.padyun.scripttools.content.data.CustomScriptIOManager
import com.padyun.scripttools.content.data.CustomScriptStorage
import com.padyun.scripttools.content.data.FPathScript
import com.padyun.scripttools.content.network.ScriptTestProxyService
import com.padyun.scripttools.module.runtime.ScriptManager
import com.padyun.scripttools.module.runtime.StContext
import com.padyun.scripttools.module.runtime.StIntentActions
import com.padyun.scripttools.module.runtime.test.TestProxy
import com.padyun.scripttools.services.biz.UploadScriptService
import com.padyun.scripttools.services.biz.UploadScriptService.*
import com.padyun.scripttoolscore.content.network.SockResponseUtils
import com.padyun.scripttoolscore.models.HttpParam
import com.uls.utilites.common.ICCallback
import com.uls.utilites.content.ToastUtils
import com.uls.utilites.io.Files
import com.uls.utilites.ui.DensityUtils
import com.uls.utilites.un.Useless
import com.uls.utilites.utils.LogUtil
import com.yanzhenjie.recyclerview.SwipeMenuItem
import com.yanzhenjie.recyclerview.SwipeRecyclerView
import com.yanzhenjie.recyclerview.touch.OnItemMovementListener
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.IOException

/**
 * Created by daiepngfei on 9/20/19
 */
class FmCustomScriptTitleList(val editable: Boolean = true) : FmStSimpleList() {
    private var mCustomScriptHomeProxy: FmCustomScriptHome.CustomScriptHomeProxy? = null

    fun setCustomScriptHomeProxy(proxy: FmCustomScriptHome.CustomScriptHomeProxy?) {
        this.mCustomScriptHomeProxy = proxy
    }
    private var isProcessing: Boolean = false
    private var testProxy: TestProxy? = null
    var runningScriptNameChangeListener: Consumer<String>? = null

    companion object {

        const val KEY_GAME_ID = "GAME_ID"
        const val KEY_USER_ID = "USER_ID"
        const val KEY_IP = "IP"
        const val KEY_ASIP = "ASIP"
        const val KEY_VERIFY = "VERIFY"
        const val TAG = "FmCustomScriptTitleList#"

        const val MSG_POST_ADD = 10010
        const val MSG_POST_DEL = 10011

        @JvmStatic
        fun newInstance(ip: String?, asip: String?, verify: String?, userId: String?, gameId: String?): FmCustomScriptTitleList {
            val fm = FmCustomScriptTitleList()
            val bundle = Bundle()
            bundle.putString(KEY_IP, ip)
            bundle.putString(KEY_ASIP, asip)
            bundle.putString(KEY_GAME_ID, gameId)
            bundle.putString(KEY_USER_ID, userId)
            bundle.putString(KEY_VERIFY, verify)
            fm.arguments = bundle
            return fm
        }
    }

    override fun getCustomConfigOption(): ConfigOption {
        val config = genDefaultConfigOption()
        config.setRootBackgroundColor(Color.WHITE)
        return config
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
        // Toast.makeText(activity!!, "" + userIdOrMobile + " | " + gameId + " | " + FPathScript.scriptDataDir(), Toast.LENGTH_LONG).show()
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
                            if (CustomScriptStorage.internalSave(dPath, script)) {
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

    override fun onResumeLaterTime() {
        super.onResumeLaterTime()
        checkUploadingScript()
        checkRunningScript()
    }

    private fun checkUploadingScript() {
        withBindedUploadService(Runnable { foreachData(MdCustomScriptTitle::class.java) { mUploadScriptService?.queryState(it.getPath(), mOnUploadListener) } })
    }

    private fun checkRunningScript() {

        StContext.network().applyTest().getCurrentRunningScript(object : ICCallback<String> {

            override fun onSuccess(t: String?) {
                LogUtil.d(this@FmCustomScriptTitleList.javaClass.simpleName, "current running script is : $t")
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
                LogUtil.w(this@FmCustomScriptTitleList.javaClass.simpleName, "error : $msg", e)
            }
        })
    }

    override fun handleMessage(msg: Message): Boolean {
        if (isProcessing) {
            // ToastUtils.show(activity, "正在执行请求，请稍候")
            return false
        }
        if (msg != null) {
            when (msg.what) {
                MSG_POST_ADD -> {
                    isProcessing = true
                    val title = (msg.obj as MdCustomScriptTitle)
                    withRemoteTasksClear(title, Runnable { addSocketTask(title) })
                }
                MSG_POST_DEL -> {
                    val title = (msg.obj as MdCustomScriptTitle)
                    removeTask(title)
/*
                    testProxy?.sendCmdStop(object : ISockSendResponse {

                        override fun onSendFail(errno: Int, msg: String?, e: Exception?) {
                            toastNeterror()
                        }

                        override fun onBaseResponse(reader: CoReader?): Boolean {
                            isAdding = false
                            return true
                        }

                        override fun onSendingCompelete() {
                            runOnUiThread(Runnable {
                                isAdding = false
                                runningScriptNameChangeListener?.accept("")
                                foreachData(MdCustomScriptTitle::class.java) { it.setStateWithId(title.script.id, MdCustomScriptTitle.STATE_IDLE) }
                                refreshList()
                            })
                        }

                    })
*/
                }
            }
        }
        return super.handleMessage(msg)
    }

    private fun removeTask(title: MdCustomScriptTitle) {
        isProcessing = true
        showLoading()
        testProxy?.sendCmdStop(
                object : SockResponseUtils.SimpleOkResponse() {

                    override fun onFail(errno: Int, msg: String?, e: Exception?) {
                        isProcessing = false
                        dismissLoading()
                        ToastUtils.show(activity!!, "网络错误，请稍候重试")
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
                                    ToastUtils.show(activity!!, obj.msg)
                                }
                                isProcessing = false
                            })
                        }
                    }

                    private fun showJZDialog(obj: StRespBaseObj) {
                        CoreDgV2Alert.toCreate(activity!!)
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
                            ToastUtils.show(activity!!, msg)
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
                            ToastUtils.show(activity!!, "网络错误，请稍候重试")
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

    override fun onCreateEmptyView(inflater: LayoutInflater, container: ViewGroup?): View {
        val buttonLayout = inflater.inflate(R.layout.view_empty_new_custom_tasks, container, false)
        val button = buttonLayout.findViewById<View>(R.id.buttonNewCustomTask)
        button.setOnClickListener { createNewScriptWithNewName() }
        return buttonLayout
    }

    fun createNewScriptWithNewName() {
        val dg = DgV2NewScript(activity)
        dg.init { dg_, name, _, _ ->
            run {
                dg.dismiss()
                createNewScript(name, Runnable { dg_.dismiss() })
            }
        }
        dg.show()
    }

    private fun createNewScript(name: String?, runnable: Runnable?) {
        val f = FPathScript.scriptDataFile(name)
        var result = false
        try {
            result = f.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (result) {
            val script = CoScript.newScript()
            CustomScriptStorage.saveAsync(f.absolutePath, script, CoreWorkers.IResult {
                if (it) {
                    loadScriptWithFile(f.absolutePath)
                    listAppend(MdCustomScriptTitle(f, script))
                } else {
                    ToastUtils.show(activity!!, "创建新脚本失败")
                }
                runnable?.run()
            })
        } else {
            runnable?.run()
        }
    }

    private fun loadScriptWithFile(f: String) {
        AcScriptEditor.launch(activity!!, f)
    }

    override fun onInitSwipeReclcerView(recyclerView: SwipeRecyclerView?, adapter: BaseV2RecyclerAdapter?) {
        adapter?.setOnItemClickListener { _, item, _ ->
            run {
                if (editable) {
                    val m = item as MdCustomScriptTitle
                    if (m.enableEditing) {
                        withRemoteTasksClear(m, Runnable { loadScriptWithFile(m.getPath()) })
                    } else {
                        withBindedUploadService(Runnable {
                            mUploadScriptService?.queryState(m.getPath()) {
                                handleUIWithUploadState(m, it, true)
                            }
                        })
                    }
                }
            }
        }

        recyclerView?.setSwipeMenuCreator { _, rightMenu, position ->
            if (editable && position < dataSize) {
                val width = DensityUtils.dip2px(activity, 60f)
                val height = ViewGroup.LayoutParams.MATCH_PARENT
                val uploadItem = SwipeMenuItem(activity)
                uploadItem.setBackgroundColor(Color.parseColor("#389CFF")).setWidth(width).setHeight(height).setTextSize(10).setTextColor(Color.WHITE).text = "上传云端"
                val deleteItem = SwipeMenuItem(activity)
                deleteItem.setBackgroundColor(Color.parseColor("#FF3B32")).setWidth(width).setHeight(height).setTextSize(10).setTextColor(Color.WHITE).text = "删除"
                val renameItem = SwipeMenuItem(activity)
                renameItem.setBackgroundColor(Color.parseColor("#7baB32")).setWidth(width).setHeight(height).setTextSize(10).setTextColor(Color.WHITE).text = "重命名"
                rightMenu.addMenuItem(uploadItem)
                rightMenu.addMenuItem(renameItem)
                rightMenu.addMenuItem(deleteItem)
            }
        }

        recyclerView?.setOnItemMovementListener(object : OnItemMovementListener {
            override fun onDragFlags(p0: RecyclerView?, p1: RecyclerView.ViewHolder?): Int {
                return 0
            }

            override fun onSwipeFlags(p0: RecyclerView?, p1: RecyclerView.ViewHolder?): Int {
                LogUtil.d("linshi", "linshi")
                return ItemTouchHelper.LEFT
            }
        })
        recyclerView?.setOnItemMenuClickListener { menuBridge, adapterPosition ->
            menuBridge.closeMenu()
            val pos = menuBridge.position
            val m = adapter?.get(adapterPosition)
            if (editable && m is MdCustomScriptTitle) {
                when (pos) {
                    0 -> {
                        val taskId = m.script.publishedID
                        if (taskId != null && taskId.isNotEmpty()) {
                            showLoading()
                            StContext.network().http().get(
                                    Constants.ApiPath.Phrase.GET_CHECK_TASK_STATUS,
                                    object : Callback {
                                        override fun onFailure(call: Call, e: IOException) {
                                            runOnUiThread(Runnable {
                                                dismissLoading()
                                                ToastUtils.show(activity!!, "网络错误，请稍候重试")
                                            })
                                        }

                                        override fun onResponse(call: Call, response: Response) {
                                            val obj = StRespBaseObj.parse(response)
                                            runOnUiThread(Runnable {
                                                dismissLoading()
                                                if(obj == null){
                                                    ToastUtils.show(activity!!, "网络错误，请稍候重试")
                                                } else {
                                                    if (obj.code != 0) {
                                                        showSimpleTipsDialog(obj.msg)
                                                    } else {
                                                        showUploadTips(m)
                                                    }
                                                }
                                            })
                                        }
                                    },
                                    HttpParam("taskid", taskId)
                            )
                        } else {
                            showUploadTips(m)
                        }
                    }
                    1 -> {
                        val dg = DgV2NewScript(activity)
                        dg.init { _, name, _, _ ->
                            run {
                                dg.dismiss()
                                val f = File(m.getPath())
                                if (f.exists()) {
                                    val newF = FPathScript.newScriptFile(Files.dir(m.getPath()).absolutePath, name)
                                    if (f.renameTo(newF)) {
                                        m.file = newF
                                        adapter.notifyDataSetChanged()
                                    }
                                }
                            }
                        }
                        dg.show()
                    }
                    2 -> {
                        CoreDgV2Alert.toCreate(activity!!)
                                .setTitle("提示")
                                .setMessage("确定删除 '${m.getName()}' 任务么？ ")
                                .setPositiveButton("确定", DialogInterface.OnClickListener { dialog, _ ->
                                    dialog.dismiss()
                                    adapter.removeItemAtPosition(adapterPosition)
                                    Files.delete(m.getPath())
                                    if (adapter.isEmpty) {
                                        listRefresh(ArrayList())
                                    }
                                    adapter.notifyDataSetChanged()
                                })
                                .setNegativeButton("取消", DialogInterface.OnClickListener { dialog, _ ->
                                    dialog.dismiss()
                                })
                                .show()
                    }
                }
            }
        }
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
        when (data.getIntExtra(KEY_STATE, STATE_NONE)) {
            STATE_NONE -> {
                item.enableEditing = true
                item.subTitle = null
            }
            STATE_WAITTING -> {
                disableItemSwipe(index)
                item.subTitle = "等待中"
                item.enableEditing = false
            }
            STATE_RUNNING -> {
                item.enableEditing = false
                disableItemSwipe(index)
                val actionState = data.getIntExtra(KEY_UP_ACTION_STATE, ACTION_READY)
                item.subTitle = "正在上传中，请稍后"
                when (data.getIntExtra(KEY_UP_STATE, UP_STATE_READY)) {
                    UP_STATE_ZIPPING -> {
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
                            dg.setProgress(data.getFloatExtra(KEY_UP_PROGRESS, 0F))
                            val zippingInfo = Useless.nonNullStr(data.getStringExtra(KEY_ZIP_INFO))
                            dg.setProgressSubTitle(zippingInfo + if (data.getIntExtra(KEY_UP_TYPE, TYPE_PROCCESSED) == TYPE_PROCCESSED) "(1/2)" else "(2/2)")
                        }
                        /*}*/

                    }
                    UP_STATE_UPLOADING -> {
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
                            dg.setProgressTitle("正在上传" + if (data.getIntExtra(KEY_UP_TYPE, TYPE_PROCCESSED) == TYPE_PROCCESSED) "脚本，请勿关闭客户端！"
                            else "可编辑的本地数据\n截图较大，可能耗时过长，请耐心等待" + "")
                            dg.setProgress(data.getFloatExtra(KEY_UP_PROGRESS, 0F))
                            dg.setProgressSubTitle(if (data.getIntExtra(KEY_UP_TYPE, TYPE_PROCCESSED) == TYPE_PROCCESSED) "(1/2)" else "(2/2)")
                        }
                        /*}*/
                    }
                    UP_STATE_PUBLISHING -> {
                        item.subTitle = "正在发布中，请稍后"
                    }
                    UP_STATE_COMPLETE -> {
                        item.subTitle = null
                        enableItemSwipe(index)
                        if (actionState == ACTION_DONE) {
                            getUploadProgressBar().dismiss()
                            showUploadingStateTips("上传成功，审核通过后将发送通知至【消息中心】，并开通分享任务给好友权限！")
                            if (data.getStringArrayExtra(KEY_PUBLISHED_ID) != null) {
                                item.script.publishedID = data.getStringExtra(KEY_PUBLISHED_ID)
                            }
                            if (mCustomScriptHomeProxy != null) {
                                mCustomScriptHomeProxy?.markTitleListUpdated()
                            }
                        }
                    }
                    UP_STATE_ERROR -> {
                        enableItemSwipe(index)
                        item.subTitle = "上传出现错误"
                    }

                }
            }
            STATE_REFUSED -> {
                val msg = data.getStringExtra(KEY_MSG)
                showUploadingStateTips(msg)
            }
            STATE_CANCELED -> {
                showUploadingStateTips("本次上传已经取消")
                enableItemSwipe(index)
            }
            STATE_ERROR -> {
                // todo: when ERROR_PUBLISHING etc.
                //item.subTitle = "发布失败，请稍后重试"
                getUploadProgressBar().dismiss()
                val msg = data.getStringExtra(KEY_MSG)
                showUploadingStateTips(if (Useless.isEmpty(msg)) "发布失败，请稍候重试。" else msg)
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
        CoreDgV2Alert.toCreate(activity!!).setTitle("上传说明")
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

    var uploadServiceConnection: ServiceConnection? = null
    var mUploadScriptService: UploadScriptService? = null
    var mDgUploadProgress: DgLiteProgress? = null
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
            mDgUploadProgress = DgLiteProgress(activity!!)
        }
        return mDgUploadProgress!!
    }

    /**
     * do
     */
    private fun withBindedUploadService(withing: Runnable?) {
        if (uploadServiceConnection == null || mUploadScriptService == null) {
            val uploadServiceIntent = Intent(activity, UploadScriptService::class.java)
            activity!!.startService(uploadServiceIntent)
            uploadServiceConnection = object : ServiceConnection {

                override fun onServiceDisconnected(name: ComponentName?) {
                    uploadServiceConnection = null
                }

                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    mUploadScriptService = (service as ScriptUploadBinder).uploaderService
                    mUploadScriptService?.addUploadingStateChangeListener(mOnUploadListener)
                    withing?.run()
                }
            }
            activity!!.bindService(uploadServiceIntent, uploadServiceConnection!!, Service.BIND_AUTO_CREATE)
        } else {
            withing?.run()
        }
    }

    /**
     *
     */
    private val mOnUploadListener = OnUploadingStateChangedListener { data ->
        run {
            // httpGet & check the uploading-key
            val key = data.getStringExtra(KEY_KEY)
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
            mUploadingStateTipsDialog = CoreDgV2Alert.toCreate(activity!!)
            mUploadingStateTipsDialog!!.setTitle("温馨提示")
                    .setExclusiveNaturalButton("知道了", DialogInterface.OnClickListener { d, _ -> run { d.dismiss() } })
        }
        mUploadingStateTipsDialog!!.setMessage(s).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (uploadServiceConnection != null) {
            mUploadScriptService?.removeUploadingStateChangeListener(mOnUploadListener)
            activity!!.unbindService(uploadServiceConnection!!)
        }
    }

    fun reload() {
        load()
    }

    fun config(editable: Boolean) {
//        this.editable = editable
//        if (testProxy != null) {
//            // ScriptTestProxyService.apply()
//        }
//        testProxy = ScriptTestProxyService.apply(StContext.getInstance().manifest.deviceIp,
//                StContext.getInstance().manifest.deviceAsIp)
//
//
//        if (isAdded) {
//            load()
//        } else {
//            runOrDelayOnFirstRusume(Runnable { load() })
//        }
    }

}