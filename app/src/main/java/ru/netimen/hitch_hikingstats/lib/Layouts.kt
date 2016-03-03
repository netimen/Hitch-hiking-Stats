package ru.netimen.hitch_hikingstats.lib

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.FrameLayout
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.forEachChild
import ru.netimen.hitch_hikingstats._FrameLayout

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   26.02.16
 */

open class OneVisibleChildLayout(context: Context) : FrameLayout(context) {
    fun showChild(child: View, hiddenVisibility: Int = View.GONE) {
        if (child.parent != this) throw IllegalArgumentException("$child is not a child of $this")

        forEachChild { if (child == it) it.visibility = VISIBLE else it.visibility = hiddenVisibility }
    }

    override fun addView(child: View?, index: Int) {
        super.addView(child, index)
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        super.addView(child, index, params)
    }
}

//open class DataLayout(context: Context) : OneVisibleChildLayout(context) {
//
//}


class _OneVisibleChildLayout(ctx: Context) : OneVisibleChildLayout(ctx), _FrameLayout

fun ViewManager.oneVisibleChildLayout(init: _OneVisibleChildLayout.() -> Unit = {}) = ankoView({ _OneVisibleChildLayout(it) }, init) // https://gist.github.com/cnevinc/539d6659a4afbf6a0b08

