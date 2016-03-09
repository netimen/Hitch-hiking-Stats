package ru.netimen.hitch_hikingstats

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.jakewharton.rxbinding.view.clicks
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI
import ru.netimen.hitch_hikingstats.lib.MvpFragment
import ru.netimen.hitch_hikingstats.lib.MvpView
import ru.netimen.hitch_hikingstats.lib.Presenter
import rx.Observable

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   03.03.16
 */

//interface HasLength {
//    val aaa by lazy { "aaa" }
////    val lengthMinutes = System.currentTimeMillis()
////        get() = (System.currentTimeMillis() - field) / 60 / 1000
//}
sealed class GoState { // CUR store state in case crash, reboot etc
    //    abstract class StateWithLength() : GoState() {
    val lengthMinutes = System.currentTimeMillis()
        get() = (System.currentTimeMillis() - field) / 60 / 1000
    //    }

    class Idle : GoState()
    class Waiting : GoState()
    class Riding(val car: Car, val waitMinutes: Int) : GoState()

}

interface GoView : MvpView {
    fun rideClicked(): Observable<Unit>
    fun stopClicked(): Observable<Unit>
    fun waitClicked(): Observable<Unit>
    fun carSelected(): Observable<String>

    fun showState(state: GoState)
}

// cur independent vm layer
class GoPresenter(view: GoView) : Presenter<GoView>(view) {
    var state: GoState = GoState.Idle()

    init {
        view.showState(state)
        view.waitClicked().subscribe {
            state = GoState.Waiting()
            view.showState(state)
        }
        view.rideClicked().subscribe {
            state = GoState.Waiting()
        }
    }
}

class GoFragment : MvpFragment<GoPresenter, GoFragment>(), GoView {
//    override val presenter = GoPresenter(this)
    private val ui = GoFragmentUI()
    private lateinit var state: GoState

    override fun createPresenter() = GoPresenter(this)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) = ui.createView(UI {})

    override fun rideClicked() = ui.ride.clicks()

    override fun stopClicked(): Observable<Unit> = ui.waitStop.clicks().filter { state !is GoState.Idle }

    override fun waitClicked(): Observable<Unit> = ui.waitStop.clicks().filter { state is GoState.Idle }

    override fun carSelected(): Observable<String> {
        throw UnsupportedOperationException()
    }

    override fun showState(state: GoState) {
        this.state = state
        when (state) {
            is GoState.Waiting -> {
                ui.waitStop.text = ui.getSateCaption(state)
            }
        }

    }

}

class GoFragmentUI : AnkoComponent<Fragment> {
    lateinit var title: TextView
    lateinit var car: EditText
    lateinit var ride: Button
    lateinit var waitStop: Button

    lateinit var statesCaptions: Array<out String>

    fun getSateCaption(state: GoState) = when (state) {
        is GoState.Idle -> statesCaptions[0]
        is GoState.Waiting -> statesCaptions[1]
        is GoState.Riding -> statesCaptions[2]
    }

    override fun createView(ui: AnkoContext<Fragment>) = with(ui) {

        relativeLayout {
            fitsSystemWindows = true // CUR on small screens run button is invisible
            padding = dimen(R.dimen.margin_big)

            statesCaptions = stringArray(R.array.hitch_states)
            title = textView(statesCaptions[0]) { // CUR display state in toolbar instead
                id = 1
                textSize = 20f
            }.lparams {
                alignParentTop()
                centerHorizontally()
                bottomMargin = dip(40)
                topMargin = dimen(R.dimen.margin_big)
            }

            val buttonMargin = dimen(R.dimen.margin_small)
            waitStop = button(statesCaptions[1]) {
                id = 2
            }.lparams {
                margin = buttonMargin
                alignParentRight()
                below(title)
            }
            ride = button(statesCaptions[2]) {
                id = 3
            }.lparams {
                margin = buttonMargin
                alignParentRight()
                below(waitStop)
            }
            car = editText {
                hintResource = R.string.toyota
            }.lparams(width = dip(120)) {
                margin = buttonMargin
                sameBottom(ride)
                leftOf(ride)
            }
        }
    }
}

