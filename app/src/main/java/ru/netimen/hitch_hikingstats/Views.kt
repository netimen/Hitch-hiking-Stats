package ru.netimen.hitch_hikingstats

import android.content.Context
import android.widget.RelativeLayout
import android.widget.TextView
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
        carView = textView { }.lparams {
            alignParentLeft()
        }
        dateView = textView { }.lparams {
            alignParentRight()
        }
    }
}


