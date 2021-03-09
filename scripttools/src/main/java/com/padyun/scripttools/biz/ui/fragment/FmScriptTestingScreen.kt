package com.padyun.scripttools.biz.ui.fragment

import com.mon.ui.list.compat.fragment.FmBase

/**
 * Created by daiepngfei on 10/18/19
 */
class FmScriptTestingScreen : FmBase()/*, IGStreamPlayerEvents*//*, SurfaceHolder.Callback*/ {

    /*private var rootView: View? = null
    private var cStreamView: CStreamView? = null
    private var taskName: TextView? = null
    private var expandButton: View? = null
    private var scriptfilePath: String? = null
    private var streamOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

    companion object {
        const val REQ_FULL_SCREEN = 1124
        @JvmStatic
        fun newInstance(ip: String, asip: String) = getInstanceWithArgumentsSet(FmScriptTestingScreen(), arguments().putExtra(SECons.Ints.KEY_STREAM_IP, ip).putExtra(SECons.Ints.KEY_STREAM_AS_IP, asip).extras)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = rootView ?: inflater.inflate(R.layout.fr_script_test_screen, container, false)
        cStreamView = rootView?.findViewById(R.id.stream)
        cStreamView?.disableAutoOrientationChange()
        taskName = rootView?.findViewById(R.id.taskName)
        expandButton = rootView?.findViewById(R.id.view_expand_start)
        expandButton?.setOnClickListener { goFullScreen() }
        cStreamView?.setShowDebugInfo(false)
        cStreamView?.setStreamEventListener { event, data ->
            runOnUiThread(Runnable {
                if (event == IGEvents.STREAM_REMOTE_STATE_RESOLUTION) {
                    val width = data!!.getInt("width")
                    val height = data.getInt("height")
                    if (height > width) {
                        val params = (cStreamView?.layoutParams as ConstraintLayout.LayoutParams)
                        params.dimensionRatio = "v,9:16"
                        cStreamView?.layoutParams = params
                    }
                }
            })
            false

        }
        println("doBindingNewSurface# oncreatting ~! ")
        return rootView
    }

    private fun goFullScreen() {
        expandButton?.isEnabled = false
        val intent = Intent(activity, AcStreamPlay::class.java)
        intent.putExtra(SECons.Ints.KEY_SCRIPT_FILE_PATH, scriptfilePath)
        intent.putExtra(SECons.Ints.REQUEST_ORIENTATION, streamOrientation)
        startActivityForResult(intent, REQ_FULL_SCREEN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_FULL_SCREEN) {
            runOnNextResume(Runnable {
                //streamHelper?.switchSurfaceCallbackWrapper(surface, false, SpOverlayProgress(rootView?.findViewById<View>(R.id.loadingView)))
                // streamHelperLALocked = false
            })
        }
    }

    fun setScriptFilePath(path: String) {
        this.scriptfilePath = path
    }

    fun setTaskName(taskName: String?) {
        this.taskName?.text = taskName
    }

    fun startStream() {
    }

    override fun onStart() {
        super.onStart()
        cStreamView?.open(
                GSConfig.Builder.defaultBuilder()
                    .ip(ScriptTestConfig.getServerIp())
                    .asip(ScriptTestConfig.getAsIp())
                    .verify(ScriptTestConfig.getDefaultVerify())
                    .configureModule(ScriptTestConfig.genDefaultConfigModule())
                    .audioConfigIndex(ScriptTestConfig.getAudionConfigType())
                .build()
        )
    }

    override fun onResume() {
        super.onResume()
        expandButton?.isEnabled = true
    }

    override fun onDestroy() {
        cStreamView?.close()
        super.onDestroy()
    }*/

}