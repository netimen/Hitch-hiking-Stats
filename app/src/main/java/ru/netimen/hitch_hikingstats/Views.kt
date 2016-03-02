package ru.netimen.hitch_hikingstats

import android.app.Fragment
import android.content.Context
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.RelativeLayout
import android.widget.TextView
import org.jetbrains.anko.*

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   02.03.16
 */

fun ViewGroup.UI(init: AnkoContext<Context>.() -> Unit) = AnkoContext.createDelegate(this)

interface RideView {
    var carName: TextView
    var date: TextView
}

class RideViewImpl(context: Context) : RelativeLayout(context){
    //
        init {
//            UI {
                textView()
                textView()
//            }
        }
    //}

}

class RideUI : AnkoComponent<Fragment> {
    override fun createView(ui: AnkoContext<Fragment>) = with(ui) {
        relativeLayout {
            textView {
                text = "aaa"
            }.lparams {
                alignParentLeft()
            }
            textView {
                text = "bbb"
            }.lparams {
                alignParentRight()
            }
        }
    }
}
