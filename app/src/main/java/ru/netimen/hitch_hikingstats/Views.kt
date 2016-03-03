package ru.netimen.hitch_hikingstats

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.alignParentLeft
import org.jetbrains.anko.alignParentRight
import org.jetbrains.anko.textView

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   02.03.16
 */

class RideView(context: Context) : RelativeLayout(context), _RelativeLayout { // cur make the class not implement this _Rel itself, so it's users don't have access to lparams
    private val carView: TextView
    private val dateView: TextView

    fun bind(ride: Ride) {
        carView.text = ride.car
        dateView.text = ride.trip
    }

    init {
        carView = textView {
            text = "aaa"
        }.lparams {
            alignParentLeft()
        }
        dateView = textView {
            text = "bbb"
        }.lparams {
            alignParentRight()
        }
    }

}

interface _RelativeLayout {
    fun <T : View> T.lparams(
            c: android.content.Context?,
            attrs: android.util.AttributeSet?,
            init: android.widget.RelativeLayout.LayoutParams.() -> Unit = {}
    ): T {
        val layoutParams = android.widget.RelativeLayout.LayoutParams(c!!, attrs!!)
        layoutParams.init()
        this@lparams.layoutParams = layoutParams
        return this
    }

    fun <T : View> T.lparams(
            width: Int = android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
            height: Int = android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
            init: android.widget.RelativeLayout.LayoutParams.() -> Unit = {}
    ): T {
        val layoutParams = android.widget.RelativeLayout.LayoutParams(width, height)
        layoutParams.init()
        this@lparams.layoutParams = layoutParams
        return this
    }

    fun <T : View> T.lparams(
            source: android.view.ViewGroup.LayoutParams?,
            init: android.widget.RelativeLayout.LayoutParams.() -> Unit = {}
    ): T {
        val layoutParams = android.widget.RelativeLayout.LayoutParams(source!!)
        layoutParams.init()
        this@lparams.layoutParams = layoutParams
        return this
    }

    fun <T : View> T.lparams(
            source: android.view.ViewGroup.MarginLayoutParams?,
            init: android.widget.RelativeLayout.LayoutParams.() -> Unit = {}
    ): T {
        val layoutParams = android.widget.RelativeLayout.LayoutParams(source!!)
        layoutParams.init()
        this@lparams.layoutParams = layoutParams
        return this
    }

    fun <T : View> T.lparams(
            source: android.widget.RelativeLayout.LayoutParams?,
            init: android.widget.RelativeLayout.LayoutParams.() -> Unit = {}
    ): T {
        val layoutParams = android.widget.RelativeLayout.LayoutParams(source!!)
        layoutParams.init()
        this@lparams.layoutParams = layoutParams
        return this
    }

}

