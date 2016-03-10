package ru.netimen.hitch_hikingstats

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.jakewharton.rxbinding.view.clicks
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI
import ru.netimen.hitch_hikingstats.lib.BranchableSubject
import ru.netimen.hitch_hikingstats.lib.MvpFragment
import ru.netimen.hitch_hikingstats.lib.MvpView
import ru.netimen.hitch_hikingstats.lib.Presenter
import rx.Observable
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

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
class LengthMinutesDelegate<T>() {
    private val creationMillis = System.currentTimeMillis()
    operator fun getValue(t: T, property: KProperty<*>): Int = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - creationMillis).toInt()
}

sealed class GoState { // CUR store state in case crash, reboot etc
    //    abstract class StateWithLength() : GoState() {
    //    open val lengthMinutes: Int = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) as Int
    //        get() = ((System.currentTimeMillis() - field) / 60 / 1000) as Int

    open val lengthMinutes: Int by LengthMinutesDelegate()
    //    open val lengthMinutes: Int by lazy { }
    //        get() = ((System.currentTimeMillis() - field) / 60 / 1000) as Int
    //    }

    class Idle : GoState() {
        override val lengthMinutes = 0
    }

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
    var state by Delegates.observable<GoState>(GoState.Idle()) { prop, old, new -> updateState(new) }

    init {
        view.showState(state)//CUR load state here instead
        view.stopClicked().subscribe { state = GoState.Idle() }
        view.waitClicked().subscribe { state = GoState.Waiting() }
        view.rideClicked().subscribe { state = GoState.Riding(Car("aaa", 1), state.lengthMinutes) }
    }

    private fun updateState(newState: GoState) {
        view.showState(newState)
    }
}

class GoFragment : MvpFragment<GoPresenter, GoFragment>(), GoView {
    private val ui = GoFragmentUI()
    private val waitStopClicks by lazy { BranchableSubject(ui.waitStop.clicks()) }

    override fun createPresenter() = GoPresenter(this)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) = ui.createView(UI {})

    override fun rideClicked() = ui.ride.clicks()

    override fun stopClicked(): Observable<Unit> = waitStopClicks.branch { presenter.state !is GoState.Idle }

    override fun waitClicked(): Observable<Unit> = waitStopClicks.branch { presenter.state is GoState.Idle }

    override fun carSelected(): Observable<String> {
        throw UnsupportedOperationException()
    }

    override fun showState(state: GoState) {
        activity.title = ui.getSateCaption(state)
        ui.waitStop.text = ui.getWaitStopButtonCaption(state)
    }

}

class GoFragmentUI : AnkoComponent<Fragment> {
    //    lateinit var title: TextView
    lateinit var car: EditText
    lateinit var ride: Button
    lateinit var waitStop: Button

    lateinit private var statesCaptions: Array<out String>
    lateinit private var buttonsCaptions: Array<out String>

    fun getSateCaption(state: GoState) = when (state) {
        is GoState.Idle -> statesCaptions[0]
        is GoState.Waiting -> statesCaptions[1]
        is GoState.Riding -> statesCaptions[2]
    }

    fun getWaitStopButtonCaption(state: GoState) = when (state) {
        is GoState.Idle -> buttonsCaptions[1]
        else -> buttonsCaptions[0]
    }

    override fun createView(ui: AnkoContext<Fragment>) = with(ui) {

        relativeLayout {
            fitsSystemWindows = true // CUR on small screens run button is invisible
            padding = dimen(R.dimen.margin_big)

            statesCaptions = stringArray(R.array.go_states)
            buttonsCaptions = stringArray(R.array.go_buttons)
            //            title = textView(statesCaptions[0]) { // CUR display state in toolbar instead
            //                id = 1
            //                textSize = 20f
            //            }.lparams {
            //                alignParentTop()
            //                centerHorizontally()
            //                bottomMargin = dip(40)
            //                topMargin = dimen(R.dimen.margin_big)
            //            }

            val buttonMargin = dimen(R.dimen.margin_small)
            ride = button(buttonsCaptions[2]) {
                id = 3
            }.lparams {
                margin = buttonMargin
                alignParentRight()
                centerVertically()
                //                below(waitStop)
            }
            car = editText {
                hintResource = R.string.toyota
            }.lparams(width = dip(120)) {
                margin = buttonMargin
                sameBottom(ride)
                leftOf(ride)
            }
            waitStop = button(buttonsCaptions[1]) {
            }.lparams {
                margin = buttonMargin
                alignParentRight()
                above(ride)
            }
        }
    }

}

