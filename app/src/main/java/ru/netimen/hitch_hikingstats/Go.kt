package ru.netimen.hitch_hikingstats

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import com.jakewharton.rxbinding.view.clicks
import com.trello.rxlifecycle.RxLifecycle
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.onUiThread
import ru.netimen.hitch_hikingstats.domain.ErrorInfo
import ru.netimen.hitch_hikingstats.domain.GoState
import ru.netimen.hitch_hikingstats.lib.MvpFragment
import ru.netimen.hitch_hikingstats.presentation.GoLogic
import ru.netimen.hitch_hikingstats.presentation.GoPresenter
import ru.netimen.hitch_hikingstats.presentation.GoView
import ru.netimen.hitch_hikingstats.presentation.LoadObservable
import rx.Observable
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.InjektMain
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.fullType
import uy.kohesive.injekt.api.get

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   03.03.16
 */

class GoFragment : MvpFragment<GoPresenter, GoFragment>(), GoView {
    private val ui = GoFragmentUI()

    override fun createPresenter() = GoPresenter(this, Injekt.get())

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) = ui.createView(UI {})

    override fun rideClicked() = ui.ride.clicks()

    override fun stopClicked(): Observable<Unit> = ui.stop.clicks()

    override fun waitClicked(): Observable<Unit> = ui.wait.clicks()

    override fun carSelected(): Observable<String> {
        throw UnsupportedOperationException()
    }

    override fun showState(state: GoState) = onUiThread {
        updateTitle(state)
        ui.wait.visibility = if (state is GoState.Idle) View.VISIBLE else View.GONE
        ui.stop.visibility = if (ui.wait.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    override fun updateTitle(state: GoState) = onUiThread {
        activity.title = getString(ui.getSateCaption(state)) + if (state.lengthMinutes > 0) " ${state.lengthMinutes} " + getString(R.string.min) else "" // CUR never displays 0 min
    }

    override fun <T> bindToLifecycle() = RxLifecycle.bindView<T>(ui.ride.parent as View)

    companion object : InjektMain() {
        override fun InjektRegistrar.registerInjectables() {
            addSingleton(fullType<GoLogic>(), object: GoLogic {
                override fun loadState(): LoadObservable<GoState, ErrorInfo> = LoadObservable(FirebaseStateRepo().get())

                override fun saveState(state: GoState) = FirebaseStateRepo().set(state)

                override fun addRide(state: GoState) {
                    throw UnsupportedOperationException()
                }

            })
        }
    }
}

class GoFragmentUI : AnkoComponent<Fragment> {
    lateinit var car: EditText
    lateinit var ride: Button
    lateinit var wait: Button
    lateinit var stop: Button // CUR: discard button

    fun getSateCaption(state: GoState) = when (state) {
        is GoState.Idle -> R.string.idle
        is GoState.Waiting -> R.string.waiting
        is GoState.Riding -> R.string.riding
    }

    override fun createView(ui: AnkoContext<Fragment>) = with(ui) {

        relativeLayout {
            fitsSystemWindows = true
            padding = dimen(R.dimen.margin_big)

            val buttonMargin = dimen(R.dimen.margin_small)
            ride = button(R.string.ride) {
                id = 3
            }.lparams {
                margin = buttonMargin
                alignParentRight()
                centerVertically()
            }
            //            car = editText {
            //                hintResource = R.string.toyota
            //            }.lparams(width = dip(120)) {
            //                margin = buttonMargin
            //                sameBottom(ride)
            //                leftOf(ride)
            //            }
            val waitStopLparams: RelativeLayout.LayoutParams.() -> Unit = {
                margin = buttonMargin
                alignParentRight()
                above(ride)
            }
            wait = button(R.string.wait) {
            }.lparams(init = waitStopLparams)
            stop = button(R.string.stop) {
                visibility = View.GONE
            }.lparams(init = waitStopLparams)
        }
    }

}

