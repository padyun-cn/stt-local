package com.padyun.scripttools.biz.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.mon.ui.buildup.ViewSimpleList
import com.mon.ui.list.compat.adapter.BaseRecyclerHolder
import com.mon.ui.list.compat.adapter.BaseV2RecyclerAdapter
import com.mon.ui.list.compat.adapter.IBaseRecyclerModel
import com.padyun.scripttools.biz.ui.activity.AcScriptEditor
import com.padyun.scripttools.biz.ui.data.MdScriptImportTitle
import com.padyun.scripttools.biz.ui.holders.HdScriptImportTitle
import com.uls.utilites.content.CoreWorkers
import com.padyun.scripttools.content.data.CustomScriptIOManager
import com.padyun.scripttools.content.data.CustomScriptStorage
import com.uls.utilites.io.Files
import com.uls.utilites.un.Useless
import java.io.File

/**
 * Created by daiepngfei on 9/20/19
 */
@SuppressLint("ViewConstructor")
class ViewImportMenuList(context: Activity) : ViewSimpleList(context), BaseV2RecyclerAdapter.OnItemClickListener<MdScriptImportTitle> {
    private var userIdOrMobile: String? = null
    private var gameId: String? = null
    private var currentPath: String? = null


    constructor(context: Activity, userIdOrMobile: String, gameId: String, currentPath: String) : this(context) {
        this.userIdOrMobile = userIdOrMobile
        this.gameId = gameId
        this.currentPath = currentPath
        scriptWithFile()
    }

    override fun onInitTopView(inflater: LayoutInflater?, topLayout: FrameLayout?) {
        super.onInitTopView(inflater, topLayout)
    }

    override fun onCreate() {
        super.onCreate()
        setOnItemClickListener(this@ViewImportMenuList)
    }

    override fun getCustomConfigOption(): ConfigOption {
        val config = genDefaultConfigOption()
        config.setRootBackgroundColor(Color.WHITE)
        return config
    }

    override fun generateVHByType(itemView: View, viewType: Int): BaseRecyclerHolder<*> = HdScriptImportTitle(itemView)
    override fun onLoadListFirstTime() {

    }

    private fun scriptWithFile() {
        val scriptFiles = CustomScriptIOManager.getCustomScriptFilesWithGameId(userIdOrMobile, gameId)
        if (scriptFiles != null && scriptFiles.isNotEmpty()) {
            CoreWorkers<List<IBaseRecyclerModel>>(CoreWorkers.Work {
                val list: MutableList<IBaseRecyclerModel> = ArrayList()
                Useless.foreach(scriptFiles) {
                    var f = it
                    var dPath = f.absolutePath
                    if (!Useless.equals(dPath, currentPath)) {
                        /*compat code*/
                        if (".sce" == Files.ext(dPath)) {
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
                            list.add(MdScriptImportTitle(f, coScript))
                        }
                    }
                }
                list
            }, CoreWorkers.IResult {
                listRefresh(it)
            }).start()
        }
    }

    private fun loadScriptWithFile(f: String) {
        AcScriptEditor.launch(context as Activity, f)
    }

    override fun onItemClick(adapter: BaseV2RecyclerAdapter, item: MdScriptImportTitle, position: Int) {
        listener?.onItemClick(adapter, item, position)
    }

    private var listener: BaseV2RecyclerAdapter.OnItemClickListener<MdScriptImportTitle>? = null
    fun setOnScriptItemClickListener(listener: BaseV2RecyclerAdapter.OnItemClickListener<MdScriptImportTitle>?) {
        this.listener = listener
    }

}