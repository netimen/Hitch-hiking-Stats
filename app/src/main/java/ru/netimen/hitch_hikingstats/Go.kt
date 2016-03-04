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

sealed class GoState { // CUR store state in case crash, reboot etc
    abstract class StateWithLength() : GoState() {
        val lengthMinutes = System.currentTimeMillis()
            get() = (System.currentTimeMillis() - field) / 60 / 1000
    }

    class Idle : GoState()
    class Waiting : StateWithLength()
    class Riding(val car: Car, val waitMinutes: Int) : StateWithLength()
}

fun test(state: GoState) {
    when (state) {
        is GoState.Riding -> state.lengthMinutes
    }
}

interface GoView : MvpView {
    fun rideClicked(): Observable<Unit>
    fun stopClicked(): Observable<Unit>
    fun waitClicked(): Observable<Unit>
    fun carSelected(): Observable<String>
}

class GoPresenter : Presenter<GoView>() {
    var state = GoState.Idle()

    fun <T> observeView(observable: GoView.() -> Observable<T>) = view?.run { observable() } ?: Observable.never<T>()

    override fun onViewAttached() {
        unsubscribeOnDetach(observeView(GoView::rideClicked).subscribe())
    }
    //    override fun onViewAttached() = view?.let { view ->
    //        unsubscribeOnDetach(view.rideClicked().subscribe())
    //    } ?: Unit
}

class GoFragment : MvpFragment<GoPresenter, GoFragment>(), GoView {
    override val presenter = GoPresenter()
    private val ui = GoFragmentUI()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) = ui.createView(UI {})

    override fun rideClicked() = ui.ride.clicks()

    override fun stopClicked(): Observable<Unit> = throw UnsupportedOperationException()

    override fun waitClicked(): Observable<Unit> {
        throw UnsupportedOperationException()
    }

    override fun carSelected(): Observable<String> {
        throw UnsupportedOperationException()
    }

}

class GoFragmentUI : AnkoComponent<Fragment> {
    lateinit var title: TextView
    lateinit var car: EditText
    lateinit var ride: Button
    lateinit var waitStop: Button

    override fun createView(ui: AnkoContext<Fragment>) = with(ui) {

        relativeLayout {
            fitsSystemWindows = true // CUR on small screens run button is invisible
            padding = dimen(R.dimen.margin_big)

            val rideStates = stringArray(R.array.hitch_states)
            title = textView(rideStates[0]) { // CUR display state in toolbar instead
                id = 1
                textSize = 20f
            }.lparams {
                alignParentTop()
                centerHorizontally()
                bottomMargin = dip(40)
                topMargin = dimen(R.dimen.margin_big)
            }

            val buttonMargin = dimen(R.dimen.margin_small)
            waitStop = button(rideStates[1]) {
                id = 2
            }.lparams {
                margin = buttonMargin
                alignParentRight()
                below(title)
            }
            ride = button(rideStates[2]) {
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

