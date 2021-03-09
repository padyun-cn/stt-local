package com.mon.ui.list.compat.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import java.util.*
import kotlin.math.min

/**
 * Created by daiepngfei on 9/20/17
 */

open class FmBase : Fragment(), IKeyAttacher {
    private var curShowingChildFragment: Fragment? = null
    private var mFragmentGenerator: IFragmentGenerator? = null

    private var mResumeCount = 0

    private val mFirstResumeRunnables: Stack<Runnable> = Stack()
    private val mNextResumeRunnables: Stack<Runnable> = Stack()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            val isSupportHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN)
            val ft = fragmentManager!!.beginTransaction()
            if (isSupportHidden) {
                ft.hide(this)
            } else {
                ft.show(this)
            }
            ft.commit()
        }
        if (IFragmentGenerator::class.java.isInstance(this)) {
            configiFragmentGenerator(this as IFragmentGenerator)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden)
    }

    /**
     *
     * @param fragmentGenerator
     */
    private fun configiFragmentGenerator(fragmentGenerator: IFragmentGenerator) {
        this.mFragmentGenerator = fragmentGenerator
    }

    protected fun getArgInt(key: String): Int {
        return arguments?.getInt(key) ?: 0
    }

    protected fun getArgString(key: String): String {
        return arguments?.getString(key) ?: ""
    }

    protected fun getArgBoolean(key: String, defaultVal: Boolean): Boolean {
        return arguments?.getBoolean(key, defaultVal) ?: true
    }

    protected fun finishActivity() {
        if (activity != null) {
            onActivityFinished()
            activity!!.finish()
        }
    }

    open fun onActivityFinished() {}

    /**
     *
     */
    protected fun runOnUiThread(runnable: Runnable?) {
        if (runnable != null) {
            activity?.runOnUiThread(runnable)
        }
    }

    /**
     *
     * @param tag
     */
    protected fun doSwitchFragmentWithTag(tag: String) {
        val fragmentGenerator = mFragmentGenerator
        if (fragmentGenerator != null) {
            val fragmentManager = childFragmentManager
            if (curShowingChildFragment != null) {
                fragmentManager.beginTransaction().hide(curShowingChildFragment!!).commitAllowingStateLoss()
            }
            curShowingChildFragment = fragmentManager.findFragmentByTag(tag)
            if (curShowingChildFragment == null) {
                curShowingChildFragment = fragmentGenerator.onCreateFragmentWithTag(tag)
                if (curShowingChildFragment != null) fragmentManager.beginTransaction().add(fragmentGenerator.getFragmentContainerId(tag), curShowingChildFragment!!, tag).commitAllowingStateLoss()
            } else {
                fragmentManager.beginTransaction().show(curShowingChildFragment!!).commitAllowingStateLoss()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        resumeOnFristTime()
        resumRunsLaterTime()
        mResumeCount = min(mResumeCount + 1, Integer.MAX_VALUE - 1)
    }

    fun runOrDelayOnFirstRusume(runnable: Runnable?) {
        if (runnable != null) {
            if (isFirstResumed()) mFirstResumeRunnables.push(runnable)
            else runnable.run()
        }
    }

    fun runOnNextResume(runnable: Runnable?) {
        if (runnable != null) {
            mNextResumeRunnables.push(runnable)
        }
    }

    private fun resumRunsLaterTime() {
        if (!isFirstResumed()) {
            onResumeLaterTime()
            while (!mNextResumeRunnables.empty()) {
                val run: Runnable? = mNextResumeRunnables.pop()
                run?.run()
            }
        }
    }

    open fun onResumeLaterTime() {

    }

    private fun resumeOnFristTime() {
        if (!isFirstResumed() || mFirstResumeRunnables.size == 0) {
            if (mFirstResumeRunnables.size > 0) mFirstResumeRunnables.clear()
            return
        }
        while (!mFirstResumeRunnables.empty()) {
            val run: Runnable? = mFirstResumeRunnables.pop()
            run?.run()
        }

    }

    fun isFirstResumed(): Boolean {
        return mResumeCount <= 0
    }


    companion object {
        private const val STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN"

        /**
         * @param t
         * @param bundle
         * @param <T>
         *
         * @return
        </T> */
        @JvmStatic
        fun <T : Fragment> getInstanceWithArgumentsSet(t: T, bundle: Bundle?): T {
            t.arguments = bundle
            return t
        }

        /**
         * @return
         */
        @JvmStatic
        fun arguments(): Intent {
            return Intent()
        }
    }

    override fun onBackPressed(): Boolean = false

}
