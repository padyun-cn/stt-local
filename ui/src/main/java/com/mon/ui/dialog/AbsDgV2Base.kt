package com.mon.ui.dialog

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.mon.ui.R

/**
 * Created by daiepngfei on 7/20/18
 */
abstract class AbsDgV2Base(context: Context) : AbsDgFullScreen(context) {
    override fun getLayoutId(): Int = R.layout.dg_core_common_base
    protected abstract fun onInflatingContentView(inflater: LayoutInflater): View?
    enum class WhichButton {
        POSITIVE, NEGATIVE, NATURAL, EXCLUSIVE_NATURAL
    }
    override fun onCustomInit() {
        val content = onInflatingContentView(LayoutInflater.from(context))
        if (content != null) {
            findViewById<FrameLayout>(R.id.content).addView(content)
        }
    }

    @JvmOverloads
    fun setTitle(title: String, divider: Boolean = true): AbsDgV2Base {
        findViewById<View>(R.id.divider_line).visibility = if (divider) View.VISIBLE else View.GONE
        return setTextView(R.id.title, title)
    }

    @JvmOverloads
    fun setTitle(title: Int, divider: Boolean = true): AbsDgV2Base {
        findViewById<View>(R.id.divider_line).visibility = if (divider) View.VISIBLE else View.GONE
        return setTextView(R.id.title, title)
    }

    fun setMessage(message: String): AbsDgV2Base = setTextView(R.id.msg, message)
    fun setMessage(message: Int): AbsDgV2Base = setTextView(R.id.msg, message)
    fun setPositiveButton(label: String, l: DialogInterface.OnClickListener): AbsDgV2Base = setButton(R.id.positive, true, WhichButton.POSITIVE, label, l)
    fun setPositiveButton(label: Int, l: DialogInterface.OnClickListener): AbsDgV2Base = setButton(R.id.positive, true, WhichButton.POSITIVE, label, l)
    fun setNegativeButton(label: Int, l: DialogInterface.OnClickListener): AbsDgV2Base = setButton(R.id.negative, true, WhichButton.NEGATIVE, label, l)
    fun setNegativeButton(label: String, l: DialogInterface.OnClickListener): AbsDgV2Base = setButton(R.id.negative, true, WhichButton.NEGATIVE, label, l)
    fun setExclusiveNaturalButton(label: String, l: DialogInterface.OnClickListener): AbsDgV2Base = setButton(R.id.natural, false, WhichButton.EXCLUSIVE_NATURAL, label, l)
    fun setExclusiveNaturalButton(label: Int, l: DialogInterface.OnClickListener): AbsDgV2Base = setButton(R.id.natural, false, WhichButton.EXCLUSIVE_NATURAL, label, l)

    fun setNaturalButton(label: String, l: DialogInterface.OnClickListener): AbsDgV2Base {
        findViewById<TextView>(R.id.inner_natural).visibility = View.VISIBLE
        findViewById<View>(R.id.divider_line_inner_natural).visibility = View.VISIBLE
        return setButton(R.id.inner_natural, true, WhichButton.NATURAL, label, l)
    }

    fun setNaturalButton(label: Int, l: DialogInterface.OnClickListener): AbsDgV2Base {
        findViewById<TextView>(R.id.inner_natural).visibility = View.VISIBLE
        findViewById<View>(R.id.divider_line_inner_natural).visibility = View.VISIBLE
        return setButton(R.id.inner_natural, true, WhichButton.NATURAL, label, l)
    }

    private fun setButton(id: Int, showGroup: Boolean, which: WhichButton, label: String, l: DialogInterface.OnClickListener): AbsDgV2Base {
        if (showGroup) showButtonGroup()
        else showExcluesiveNaturalButton()
        val button = findViewById<TextView>(id)
        button.text = label
        button.setOnClickListener {
            l.onClick(this, which.ordinal)
            onButtonClicked(which)
        }
        return this
    }

    private fun setButton(id: Int, showGroup: Boolean, which: WhichButton, label: Int, l: DialogInterface.OnClickListener): AbsDgV2Base {
        if (showGroup) showButtonGroup()
        else showExcluesiveNaturalButton()
        val button = findViewById<TextView>(id)
        button.setText(label)
        button.setOnClickListener {
            l.onClick(this, which.ordinal)
            onButtonClicked(which)
        }
        return this
    }

    protected open fun onButtonClicked(which: WhichButton){}


    private fun setTextView(id: Int, text: String): AbsDgV2Base {
        val button = findViewById<TextView>(id)
        button.text = text
        button.visibility = View.VISIBLE
        return this
    }

    private fun setTextView(id: Int, text: Int): AbsDgV2Base {
        val button = findViewById<TextView>(id)
        button.setText(text)
        button.visibility = View.VISIBLE
        return this
    }

    private fun showButtonGroup() {
        findViewById<View>(R.id.button_group).visibility = View.VISIBLE
        findViewById<TextView>(R.id.natural).visibility = View.GONE
    }

    private fun showExcluesiveNaturalButton() {
        findViewById<TextView>(R.id.natural).visibility = View.VISIBLE
        findViewById<View>(R.id.button_group).visibility = View.GONE
    }
}