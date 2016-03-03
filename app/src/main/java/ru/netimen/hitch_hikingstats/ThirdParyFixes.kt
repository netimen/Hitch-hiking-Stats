package ru.netimen.hitch_hikingstats

import android.content.Context
import android.view.View

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   03.03.16
 */

fun <T, R> T?.onNull(blockIfNull: () -> R) = this?.let {} ?: blockIfNull

fun String?.notEmpty() = if (isNullOrEmpty()) null else this

inline fun View.stringArray(resource: Int): Array<out String> = context.stringArray(resource)
fun Context.stringArray(resource: Int): Array<out String> = resources.getStringArray(resource)

interface _FrameLayout {
    fun <T : View> T.lparams(
            c: android.content.Context?,
            attrs: android.util.AttributeSet?,
            init: android.widget.FrameLayout.LayoutParams.() -> Unit = {}
    ): T {
        val layoutParams = android.widget.FrameLayout.LayoutParams(c!!, attrs!!)
        layoutParams.init()
        this@lparams.layoutParams = layoutParams
        return this
    }

    fun <T : View> T.lparams(
            width: Int = android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
            height: Int = android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
            init: android.widget.FrameLayout.LayoutParams.() -> Unit = {}
    ): T {
        val layoutParams = android.widget.FrameLayout.LayoutParams(width, height)
        layoutParams.init()
        this@lparams.layoutParams = layoutParams
        return this
    }

    fun <T : View> T.lparams(
            width: Int = android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
            height: Int = android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
            gravity: Int,
            init: android.widget.FrameLayout.LayoutParams.() -> Unit = {}
    ): T {
        val layoutParams = android.widget.FrameLayout.LayoutParams(width, height, gravity)
        layoutParams.init()
        this@lparams.layoutParams = layoutParams
        return this
    }

    fun <T : View> T.lparams(
            source: android.view.ViewGroup.LayoutParams?,
            init: android.widget.FrameLayout.LayoutParams.() -> Unit = {}
    ): T {
        val layoutParams = android.widget.FrameLayout.LayoutParams(source!!)
        layoutParams.init()
        this@lparams.layoutParams = layoutParams
        return this
    }

    fun <T : View> T.lparams(
            source: android.view.ViewGroup.MarginLayoutParams?,
            init: android.widget.FrameLayout.LayoutParams.() -> Unit = {}
    ): T {
        val layoutParams = android.widget.FrameLayout.LayoutParams(source!!)
        layoutParams.init()
        this@lparams.layoutParams = layoutParams
        return this
    }

    fun <T : View> T.lparams(
            source: android.widget.FrameLayout.LayoutParams?,
            init: android.widget.FrameLayout.LayoutParams.() -> Unit = {}
    ): T {
        val layoutParams = android.widget.FrameLayout.LayoutParams(source!!)
        layoutParams.init()
        this@lparams.layoutParams = layoutParams
        return this
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
