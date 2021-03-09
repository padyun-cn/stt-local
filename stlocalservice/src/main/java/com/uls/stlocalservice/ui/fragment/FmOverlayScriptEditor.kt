package com.uls.stlocalservice.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.*
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.util.Consumer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mon.ui.buildup.IntegerNumberInput
import com.mon.ui.list.compat.adapter.BaseRecyclerHolder
import com.mon.ui.list.compat.adapter.BaseV2RecyclerAdapter
import com.mon.ui.list.compat.adapter.IBaseRecyclerModel
import com.mon.ui.list.compat2.fragment.FmSimpleList
import com.padyun.core.dialogs.CoreDgV2Alert
import com.padyun.manifest.AppAddressBook
import com.padyun.scripttools.R
import com.padyun.scripttools.biz.ui.content.GuideManager
import com.padyun.scripttools.biz.ui.content.SECons
import com.padyun.scripttools.biz.ui.dialogs.DgV2SimpleWheel
import com.padyun.scripttools.biz.ui.dialogs.edit.DgImport
import com.padyun.scripttools.biz.ui.dialogs.edit.DgImport.ImportFactory
import com.padyun.scripttools.biz.ui.holders.*
import com.padyun.scripttools.biz.ui.views.CvScriptToolCommonNaviBar
import com.padyun.scripttools.compat.data.AbsCoConditon
import com.padyun.scripttools.compat.data.CoFinish
import com.padyun.scripttools.compat.data.CoScript
import com.padyun.scripttools.compat.data.CropImgGroupParceble
import com.padyun.scripttools.content.data.CustomScriptIOManager
import com.padyun.scripttools.content.la.ISEActivityAttatchable
import com.padyun.scripttools.content.la.ISEActivityAttatcher
import com.padyun.scripttools.module.runtime.StContext
import com.padyun.scripttoolscore.compatible.data.model.brain.SEBrainAlarm
import com.uls.stlocalservice.ui.dialog.DgScriptAlarm
import com.uls.stlocalservice.ui.floating.ScriptOverlays
import com.uls.utilites.content.CoreWorkers
import com.uls.utilites.io.Files
import com.uls.utilites.ui.DensityUtils
import com.uls.utilites.un.Useless
import com.yanzhenjie.recyclerview.SwipeMenuItem
import com.yanzhenjie.recyclerview.SwipeMenuLayout
import com.yanzhenjie.recyclerview.SwipeMenuView
import com.yanzhenjie.recyclerview.SwipeRecyclerView
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener
import com.yanzhenjie.recyclerview.touch.OnItemMovementListener
import java.util.*

/**
 * Created by daiepngfei on 9/24/19
 */
open class FmOverlayScriptEditor : FmSimpleList(), ISEActivityAttatchable {
    private lateinit var mGuideImage: ImageView
    private lateinit var mGuideActionImage: ImageView
    private var attachers: MutableList<ISEActivityAttatcher> = ArrayList()
    private var removingAttachers: MutableList<ISEActivityAttatcher> = ArrayList()
    private var isLoopingAttacher: Boolean = false
    private val mLock: Any = Object()
    private var mFooterView: View? = null

    private var currentScript: CoScript? = null
    private var currentScriptFile: String? = null
    private val mFooter: IBaseRecyclerModel = IBaseRecyclerModel { R.layout.item_ui_condition_footer }
    private var mInsertingFlagItem: AbsCoConditon? = null
    private var mInsertingData: List<AbsCoConditon>? = null
    private val mMailBox = StContext.postOffice().register(AppAddressBook.EDITOR_LISTS)

    companion object {
        const val FILE_PATH = "FILE_PATH"
        const val UID = "UID"
        const val GAME_ID = "GAME_ID"
        const val REQ_CODE = 11233
        const val FB_INSERT = 9090
        @JvmStatic
        fun newInstance(arg: Bundle?): FmOverlayScriptEditor {
            val ins = FmOverlayScriptEditor()
            ins.arguments = arg
            return ins
        }
    }

    override fun attacherStartActivityForResult(intent: Intent?, requestCode: Int) = startActivityForResult(intent, requestCode)

    override fun attach(attacher: ISEActivityAttatcher?) {
        synchronized(mLock) {
            if (attacher == null) return
            attachers.add(attacher)
        }
    }

    override fun dettach(attacher: ISEActivityAttatcher) {
        synchronized(mLock) {
            if (!Useless.isEmpty(attachers)) {
                if (isLoopingAttacher)
                    removingAttachers.add(attacher)
                else
                    attachers.remove(attacher)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onInitSwipeReclcerView(recyclerView: SwipeRecyclerView, adapter: BaseV2RecyclerAdapter) {
        mMailBox.setAddressAgent { bundles ->
            for (b in bundles) {
                onReceiveNewData(b)
            }
        }
        adapter.addDataFooter(mFooter)
        recyclerView.isLongPressDragEnabled = true // 拖拽排序，默认关闭。
        recyclerView.setSwipeMenuCreator { _, rightMenu, position ->
            val m = adapter.get(position)
            if (position < dataSize) {
                val width = DensityUtils.dip2px(activity, 50f)
                val height = ViewGroup.LayoutParams.MATCH_PARENT

                val disableItem = SwipeMenuItem(activity)
                disableItem.setBackgroundColor(Color.parseColor("#C7C7C7")).setWidth(width).setHeight(height).setTextSize(10).setTextColor(Color.WHITE).text = if (m is AbsCoConditon && m.isDisabled) "启用" else "禁用"
                val timerItem = SwipeMenuItem(activity)
                timerItem.setBackgroundColor(Color.parseColor("#22CE68")).setWidth(width).setHeight(height).setTextSize(10).setTextColor(Color.WHITE).text = "设置定时"
                val loopItem = SwipeMenuItem(activity)
                loopItem.setBackgroundColor(Color.parseColor("#7ECEF4")).setWidth(width).setHeight(height).setTextSize(10).setTextColor(Color.WHITE).text = "设置循环"
                val timeoutItem = SwipeMenuItem(activity)
                timeoutItem.setBackgroundColor(Color.parseColor("#FF9E02")).setWidth(width).setHeight(height).setTextSize(10).setTextColor(Color.WHITE).text = "设置延时"
                val deleteItem = SwipeMenuItem(activity)
                deleteItem.setBackgroundColor(Color.parseColor("#FF3B32")).setWidth(width).setHeight(height).setTextSize(10).setTextColor(Color.WHITE).text = "删除"
//                deleteItem.setBackgroundColor(Color.parseColor("#FF3B32")).setWidth(width).setHeight(height).setImage(R.drawable.ic_delete_menu_project_item)
                rightMenu.addMenuItem(disableItem)
                rightMenu.addMenuItem(timerItem)

                if (m !is CoFinish) {
                    rightMenu.addMenuItem(loopItem)
                    rightMenu.addMenuItem(timeoutItem)
                }
                rightMenu.addMenuItem(deleteItem)
            }
        }
        recyclerView.setOnItemMenuClickListener { menuBridge, adapterPosition ->
            menuBridge.closeMenu()
            val pos = menuBridge.position
            val holder = recyclerView.findViewHolderForAdapterPosition(adapterPosition) as AbsCoHolder<*>
            val m = adapter.get(adapterPosition)
            if (m is AbsCoConditon) {
                when (pos) {
                    0 -> {
                        try {
                            val menuLayout: SwipeMenuLayout? = holder.itemView as SwipeMenuLayout
                            val rightMenu: SwipeMenuView? = menuLayout?.getChildAt(2) as SwipeMenuView
                            val container = rightMenu?.getChildAt(0) as LinearLayout
                            m.isDisabled = !m.isDisabled
                            (container.getChildAt(0) as TextView).text = if (m.isDisabled) "启用" else "禁用"
                            //adapter.notifyDataSetChanged()
                            adapter.notifyItemChanged(adapterPosition)
                            saveScript()
                        } catch (e: Exception) {
                            // nothing
                            e.printStackTrace()
                        }
                    }
                    1 -> {
                        val dgV2Alarm = DgScriptAlarm(activity!!)
                        var startHour: Int? = null
                        var endHour: Int? = null
                        var startMin: Int? = null
                        var endMin: Int? = null
                        val brainAlam = m.co_brainAlarm
                        if (brainAlam != null) {
                            startHour = brainAlam.startHour
                            endHour = brainAlam.endHour
                            startMin = brainAlam.starMin
                            endMin = brainAlam.endMin
                        }
                        dgV2Alarm.init(
                                startHour, startMin, endHour, endMin,
                                { sh, sm, eh, em ->
                                    val ba = brainAlam ?: SEBrainAlarm()
                                    ba.starMin = sm
                                    ba.startHour = sh
                                    ba.endMin = em
                                    ba.endHour = eh
                                    m.co_brainAlarm = ba
                                    adapter.notifyDataSetChanged()
                                    dgV2Alarm.dismiss()
                                },
                                {
                                    m.co_brainAlarm = null
                                    adapter.notifyDataSetChanged()
                                }
                        )
                        dgV2Alarm.show()
                    }
                    2 -> {
                        if (m is CoFinish) {
                            onMenuRemove(adapter, adapterPosition, m)
                        } else {
                            val brainCounter = m.co_brain_count
                            val dgV2SimpleWheel = DgV2SimpleWheel(activity!!)
                            val counts = arrayListOf("无限循环", "1", "2", "3", "4",
                                    "5", "6", "7", "8", "9", "10", "20", "30", "40", "50", "60", "70", "80", "90", "99")
                            dgV2SimpleWheel.init(counts, brainCounter) { _, position ->
                                val count = if (position == 0) 0 else counts[position].toInt()
                                if (m.co_brain_count != count) {
                                    m.co_brain_count = count
                                    adapter.notifyDataSetChanged()
                                    saveScript()
                                }
                            }
                            dgV2SimpleWheel.show()
                        }
                    }
                    3 -> {
                        holder.onHolderMenuClick(adapter, pos)
                        runOnUiThread(Runnable {
                            showIntegerEditor(m.co_timeout, IntegerNumberInput.OnNumberChangedListener { number ->
                                if (m.co_timeout != number) {
                                    m.co_timeout = number
                                    adapter.notifyDataSetChanged()
                                    saveScript()
                                }
                            })
                        })
                    }
                    4 -> {
                        onMenuRemove(adapter, adapterPosition, m)
                    }
                }
            }
        }
        recyclerView.setOnItemMovementListener(
                object : OnItemMovementListener {
                    override fun onDragFlags(recyclerView: RecyclerView, targetViewHolder: RecyclerView.ViewHolder): Int {
                        if (targetViewHolder is CoHolderVoid) {
                            return 0
                        }
                        val position = targetViewHolder.adapterPosition
                        var flag = 0
                        if (position > 0) flag = flag or ItemTouchHelper.UP
                        if (position < dataSize - 1) flag = flag or ItemTouchHelper.DOWN
                        return flag
                    }

                    override fun onSwipeFlags(recyclerView: RecyclerView, targetViewHolder: RecyclerView.ViewHolder): Int {
                        return ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                    }
                })

        recyclerView.setOnItemMoveListener(
                object : OnItemMoveListener {

                    override fun onItemMove(srcHolder: RecyclerView.ViewHolder, targetHolder: RecyclerView.ViewHolder): Boolean {
                        // 交换数据，并更新adapter。
                        val fromPosition = srcHolder.adapterPosition
                        val toPosition = targetHolder.adapterPosition
                        if (fromPosition >= dataSize || toPosition >= dataSize) return false
                        swapData(adapter, fromPosition, toPosition)
                        return true
                    }

                    override fun onItemDismiss(srcHolder: RecyclerView.ViewHolder) {
                        // do nothing
                    }
                })

        recyclerView.setOnItemStateChangedListener { viewHolder, actionState ->
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                val vibrator = activity!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (vibrator.hasVibrator()) {
                    vibrator.cancel()
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(longArrayOf(0, 20), -1)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    viewHolder?.itemView?.elevation = 20f
                    viewHolder?.itemView?.translationZ = 20f
                }
                viewHolder?.itemView?.setBackgroundColor(Color.WHITE)
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    viewHolder?.itemView?.elevation = 0f
                    viewHolder?.itemView?.translationZ = 0f
                }
                viewHolder?.itemView?.setBackgroundColor(Color.WHITE)
            }
        }
    }

    private fun onMenuRemove(adapter: BaseV2RecyclerAdapter, adapterPosition: Int, m: AbsCoConditon) {
        CoreDgV2Alert.toCreate(activity!!)
                .setTitle("提示")
                .setMessage("确定删除该条目么？ ")
                .setPositiveButton("确定", DialogInterface.OnClickListener { dialog, _ ->
                    dialog.dismiss()
                    removeData(adapter, adapterPosition, m)
                    if (adapter.isEmpty) {
                        listRefresh(ArrayList())
                    }
                    adapter.notifyItemRemoved(adapterPosition)
                })
                .setNegativeButton("取消", DialogInterface.OnClickListener { dialog, _ ->
                    dialog.dismiss()
                })
                .show()

    }

    override fun handleMessage(msg: Message): Boolean {

        if (msg.what == SECons.Ints.SCRIPT_NEED_SAVE) {
            refreshList()
            saveScript()
        } else if (msg.what == FB_INSERT) {
            if (mInsertingData != null) {
                val index = msg.arg1
                importData(Runnable {
                    listInsert(index, mInsertingData)
                    backInNormalMode()
                })
            } else {
                backInNormalMode()
            }
        }

        return super.handleMessage(msg)
    }

    private fun importData(run: Runnable) {
        showLoading()
        CoreWorkers
                .on { ImportFactory.newCopiedConditions(mInsertingData) }
                .then {
                    run.run()
                    saveScript()
                    dismissLoading()
                }
        /*CoreWorkers<List<AbsCoConditon?>>(
                Work<List<AbsCoConditon?>?> { ImportFactory.newCopiedConditions(mInsertingData) },
                IResult<List<AbsCoConditon?>?> {
                    run.run()
                    saveScript()
                    dismissLoading()
                }
        ).go()*/
    }

    /**
     *
     */
    private fun addData(list: ArrayList<AbsCoConditon>, coScript: CoScript) {
        listAppend(list)
        coScript.addContions(list)
        saveScript()
    }

    /**
     *
     */
    private fun removeData(adapter: BaseV2RecyclerAdapter, adapterPosition: Int, m: AbsCoConditon) {
        adapter.removeItemAtPosition(adapterPosition)
        currentScript?.removeCondition(m)
        saveScript()
    }

    /**
     *
     */
    private fun swapData(adapter: BaseV2RecyclerAdapter, fromPosition: Int, toPosition: Int) {
        adapter.swapData(fromPosition, toPosition)
        currentScript?.swapConditions(fromPosition, toPosition)
        saveScript()
    }

    private fun showIntegerEditor(text: Int, l: IntegerNumberInput.OnNumberChangedListener?) {
        //Viewor.visible(overlayView?.findViewById(R.id.overlayView))
        val integerNumberInput = overlayView?.findViewById<IntegerNumberInput>(R.id.editScriptEditor)
        /*integerNumberInput?.setOnMightSoftKeyboardShowListener { show ->
            if (!show) {
                integerNumberInput.clearFocus()
                overlayView?.findViewById<ScrollView>(R.id.editLayout)?.visibility = View.INVISIBLE
            } else {
                overlayView?.findViewById<ScrollView>(R.id.editLayout)?.visibility = View.VISIBLE
            }
        }*/
        integerNumberInput?.number = text
        val buttonDone = overlayView?.findViewById<View>(R.id.button_done_delay)
        buttonDone?.setOnClickListener {
            // SystemUtils.InputMethod.hideKeyboard(activity)
            val number = integerNumberInput?.number
            if (l != null && text != number) l.onNumnberChanged(number ?: 0)
            overlayView?.findViewById<ScrollView>(R.id.editLayout)?.visibility = View.INVISIBLE
        }
        //integerNumberInput?.visibility = View.VISIBLE
        overlayView?.findViewById<ScrollView>(R.id.editLayout)?.visibility = View.VISIBLE
//        if (integerNumberInput?.requestFocus()!!) {
//            SystemUtils.InputMethod.show(activity, integerNumberInput, true, Re())
//        }

    }

    inner class Re(handler: Handler? = null) : ResultReceiver(handler) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
            // super.onReceiveResult(resultCode, resultData)
            println(message = "bbahnln: $resultCode")
        }
    }

    override fun generateVHByType(root: View, type: Int): BaseRecyclerHolder<*>? {
        val holder: BaseRecyclerHolder<out IBaseRecyclerModel>
        /*val root = LayoutInflater.from(itemView.codec_context).inflate(if (type == 0) R.layout.item_empty else type, itemView, false)*/
        when (type) {
            R.layout.item_ui_condition_click -> holder = CoHolderClick(root, this)
            R.layout.item_ui_condition_click_m -> holder = CoHolderClickM(root, this)
            R.layout.item_ui_condition_offset_click -> holder = CoHolderOffset(root, this)
            R.layout.item_ui_condition_offset_click_m -> holder = CoHolderOffsetM(root, this)
            R.layout.item_ui_condition_position_click -> holder = CoHolderTap(root, this)
            R.layout.item_ui_condition_slide -> holder = CoHolderSlide(root, this)
            R.layout.item_ui_condition_slide_m -> holder = CoHolderSlideM(root, this)
            R.layout.item_ui_condition_color_click -> holder = CoHolderColor(root, this)
            R.layout.item_ui_condition_finish -> holder = CoHolderFinish(root, this)
            R.layout.item_ui_condition_footer -> {
                mFooterView = root
                holder = CoHolderVoid(root, this)
            }
            else -> holder = CoHolderVoid(root, this)
        }
        return holder
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onLifeAttachNotify(Consumer { it.onCreate(savedInstanceState) })
    }

    override fun onStart() {
        super.onStart()
        onLifeAttachNotify(Consumer { it.onStart() })
    }

    override fun onResume() {
        super.onResume()
        onLifeAttachNotify(Consumer { it.onResume() })
        println("xxedito: EditorListActivity.onResumed 2")
        checkMailBoxOnResume()
    }

    override fun onPause() {
        super.onPause()
        onLifeAttachNotify(Consumer { it.onPause() })
    }

    override fun onStop() {
        super.onStop()
        onLifeAttachNotify(Consumer { it.onStop() })
    }

    override fun onDestroy() {
        super.onDestroy()
        onLifeAttachNotify(Consumer { it.onDestroy() })
    }


    private fun checkMailBoxOnResume() {
        val bundles = mMailBox.fetch()
        for (b in bundles) {
            onReceiveNewData(b)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onLifeAttachNotify(Consumer { it.onActivityResult(requestCode, resultCode, data) })
        if (requestCode == REQ_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                onReceiveNewData(data?.extras)
            }
            GuideManager.simplelyShowImageGuides(activity, mGuideImage, mGuideActionImage,
                    GuideManager.ImageGuideType.EDIT_BACK_FROM_STREAM_1,
                    GuideManager.ImageGuideType.EDIT_BACK_FROM_STREAM_2
            )
        }

    }

    private fun onReceiveNewData(data: Bundle?) {
        val parcelableArray: ArrayList<Parcelable> = data?.getParcelableArrayList("items") ?: return
        val coScript: CoScript = currentScript ?: CoScript.newScript()
        val list = ArrayList<AbsCoConditon>()

        for (pg in parcelableArray) {
            if (pg !is CropImgGroupParceble) continue
            val condition = CropImgGroupParceble.conAbsCondition(pg)
            if (condition != null) {
                list.add(condition)
            }
        }

        if (list.size > 0) {
            addData(list, coScript)
        }
    }

    private fun saveScript(run: Runnable? = null) {
        if (currentScript != null && currentScriptFile != null) {
            val conditions = ArrayList<AbsCoConditon>()
            Useless.foreach(listAdapter.data) {
                if (it is AbsCoConditon) {
                    conditions.add(it)
                }
            }
            currentScript?.conditions = conditions
            CustomScriptIOManager.saveWithFilePath(
                    currentScriptFile,
                    currentScript,
                    CoreWorkers.IResult {
                        run?.run()
                        print("saveSync success!")
                    }
            )
        }
    }

    private fun onLifeAttachNotify(consumer: Consumer<ISEActivityAttatcher>?) {
        if (consumer == null) return
        setLoopingAttacher(true)
        Useless.foreach(attachers, consumer)
        setLoopingAttacher(false)
        Useless.foreach(removingAttachers) { t -> attachers.remove(t) }
    }

    private fun setLoopingAttacher(isLoopingAttacher: Boolean) {
        synchronized(mLock) {
            this.isLoopingAttacher = isLoopingAttacher
        }
    }

    private var commonBar: CvScriptToolCommonNaviBar? = null
    private var insertTipsBar: View? = null
    override fun onInitTopView(inflater: LayoutInflater?, topLayout: FrameLayout?) {
        val topView = inflater?.inflate(R.layout.view_top_script_editor, topLayout, true)
        commonBar = topView?.findViewById(R.id.commonNaviBar)
        commonBar?.init(activity, null, "")
        val buttonText = activity!!.getString(com.uls.stlocalservice.R.string.string_button_script_editor_top_right)
        commonBar?.setRightTextButton(buttonText) { this@FmOverlayScriptEditor.onImportClick() }

        insertTipsBar = topView?.findViewById(R.id.tipsBar)

        topView?.findViewById<View>(R.id.buttonCloseTips)?.setOnClickListener {
            val sp = activity!!.getSharedPreferences("script_tips", Context.MODE_PRIVATE)
            sp.edit().putBoolean("script_insert", false).apply()
            insertTipsBar?.visibility = View.GONE
        }
    }

    private fun onImportClick() {
        DgImport(activity!!).init(StContext.manifest().userId, StContext.manifest().gameId, getArgString(FILE_PATH), Consumer {
            if (Useless.isEmpty(it)) {
                return@Consumer
            }
            if (isEmpty) {
                importData(Runnable { listAppend(it) })
            } else {
                performOnItemEditingMode(it)
            }
        }).show()
    }

    private fun backInNormalMode() {
        mInsertingData = null
        foreachData(AbsCoConditon::class.java) {
            it.uiFlags.setInsertStateOFF()
        }
        mInsertingFlagItem = null
        insertTipsBar?.visibility = View.GONE
        notifyDataSetChanged()
        commonBar?.setTitle(Files.nameSfx(getArgString(FILE_PATH)))
        commonBar?.setRightTextButton("导入") { this@FmOverlayScriptEditor.onImportClick() }
    }

    private fun performOnItemEditingMode(it: MutableList<out AbsCoConditon>) {
        mInsertingData = it
        foreachData(AbsCoConditon::class.java) {
            it.uiFlags.setInsertStateStandby()
        }
        val sp = activity!!.getSharedPreferences("script_tips", Context.MODE_PRIVATE)
        insertTipsBar?.visibility = if (sp.getBoolean("script_insert", true)) View.VISIBLE else View.GONE
        notifyDataSetChanged()
        commonBar?.setTitle("选择插入位置")
        commonBar?.setRightTextButton("取消") { backInNormalMode() }
    }

    private var overlayView: View? = null

    override fun onInitOverlayView(inflater: LayoutInflater?, overlayLayout: FrameLayout?) {
        overlayView = inflater?.inflate(R.layout.overlay_script_editor, overlayLayout, true)
        overlayLayout?.visibility = View.VISIBLE
        val expand = overlayView?.findViewById<ImageView>(R.id.buttonScriptToolsExpand)
        val go = overlayView?.findViewById<ImageView>(R.id.buttonScriptToolsEditGo)
        val test = overlayView?.findViewById<ImageView>(R.id.buttonScriptToolsTest)
        go?.setOnClickListener { goCropping() }
        expand?.setOnClickListener {
            val notVisible = go?.visibility == View.GONE
            test?.visibility = if (notVisible) View.VISIBLE else View.GONE
            go?.visibility = if (notVisible) View.VISIBLE else View.GONE
            expand.setImageResource(if (!notVisible) R.drawable.ic_v2_expand_button else R.drawable.ic_v2_collapse_button)
        }
        expand?.visibility = View.GONE
        test?.visibility = View.GONE

        // test?.setOnClickListener { goTesting() }

        // guide
        val guideImage = ImageView(activity)
        guideImage.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        guideImage.setBackgroundColor(Color.BLACK)
        guideImage.scaleType = ImageView.ScaleType.FIT_XY
        mGuideImage = guideImage
        val guideActionImage = ImageView(activity)
        val actionButtonWidth = DensityUtils.dip2px(activity, 110f)
        val actionButtonHeight = DensityUtils.dip2px(activity, 60f)
        val layoutParams = FrameLayout.LayoutParams(actionButtonWidth, actionButtonHeight)
        layoutParams.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        layoutParams.bottomMargin = DensityUtils.dip2px(activity, 65.toFloat())
        guideActionImage.layoutParams = layoutParams
        guideActionImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
        mGuideActionImage = guideActionImage
        mGuideImage.visibility = View.GONE
        mGuideActionImage.visibility = View.GONE
        overlayLayout?.addView(guideImage)
        overlayLayout?.addView(guideActionImage)

    }


    private fun goCropping() {
        if (activity == null) {
            return
        }
        startActivity(activity!!.packageManager.getLaunchIntentForPackage(StContext.manifest().gameId))
        ScriptOverlays.Agent.onGameCapture(activity!!.applicationContext)
    }

    override fun onCreateEmptyView(inflater: LayoutInflater?, container: ViewGroup?): View? {
        val emptyView: View? = if (activity!!.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            inflater?.inflate(R.layout.view_empty_script_editor_overlay, container, false)
        } else {
            inflater?.inflate(R.layout.view_empty_script_editor, container, false)
        }
        emptyView?.findViewById<View>(R.id.buttonEmptyGoEdit)?.setOnClickListener {
            goCropping()
        }
        return emptyView
    }

    override fun onLoadListFirstTime() {
        currentScriptFile = getArgString(FILE_PATH)
        if (!TextUtils.isEmpty(currentScriptFile)) {
            CustomScriptIOManager.getCoScriptFromFileAsync(getArgString(FILE_PATH), Consumer {
                if (it != null) {
                    commonBar?.setTitle(Files.nameSfx(getArgString(FILE_PATH)))
                    currentScript = it
                    if (it.conditions != null && it.conditions.size > 0) {
                        listRefresh(it.conditions)
                    }
                }
            })
        }
        // show guide
        GuideManager.simplelyShowImageGuides(activity, mGuideImage, mGuideActionImage, GuideManager.ImageGuideType.EDIT_ENTER)
    }

    override fun onBackPressed(): Boolean {
        saveScript()
        return super.onBackPressed()
    }

}