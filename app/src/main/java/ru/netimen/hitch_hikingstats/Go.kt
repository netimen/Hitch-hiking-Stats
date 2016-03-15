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
import ru.netimen.hitch_hikingstats.lib.*
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import uy.kohesive.injekt.InjektMain
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.fullType
import uy.kohesive.injekt.injectLazy
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   03.03.16
 */

interface GoView : MvpView {
    fun rideClicked(): Observable<Unit>
    fun stopClicked(): Observable<Unit>
    fun waitClicked(): Observable<Unit>
    fun carSelected(): Observable<String>

    fun showState(state: GoState)
    fun updateTitle(state: GoState)
}

// cur independent vm layer, loading data while fragment is being created, router
// cur notification
class GoPresenter(view: GoView) : Presenter<GoView>(view) {
    private var state by Delegates.observable<GoState>(GoState.Idle()) { prop, old, new -> updateState(new) }
    private val updateTitleSubscription: Subscription? = null

    private val loadState: () -> LoadObservable<GoState, ErrorInfo> by injectLazy()
    private val saveState: (GoState) -> Unit by injectLazy()
    private val addRide: (GoState) -> Unit by injectLazy()

    init {
        loadState().onData { state = it }.subscribe() // cur lifecycle here

        view.stopClicked().bindToLifeCycle().subscribe {
            addRide(state)
            state = GoState.Idle()
        }
        view.waitClicked().bindToLifeCycle().subscribe { state = GoState.Waiting() }
        view.rideClicked().bindToLifeCycle().subscribe { state = GoState.Riding("Toyota", state.lengthMinutes) }//CUR: get car
    }

    private fun updateState(newState: GoState) {
        saveState(state)

        updateTitleSubscription?.unsubscribe()
        Observable.timer(1, TimeUnit.MINUTES).repeat().bindToLifeCycle().observeOn(AndroidSchedulers.mainThread()).subscribe { view.updateTitle(state) }

        view.showState(newState)
    }

}

class GoFragment : MvpFragment<GoPresenter, GoFragment>(), GoView {
    private val ui = GoFragmentUI()

    override fun createPresenter() = GoPresenter(this)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) = ui.createView(UI {})

    override fun rideClicked() = ui.ride.clicks()

    override fun stopClicked(): Observable<Unit> = ui.stop.clicks()

    override fun waitClicked(): Observable<Unit> = ui.wait.clicks()

    override fun carSelected(): Observable<String> {
        throw UnsupportedOperationException()
    }

    override fun showState(state: GoState) {
        updateTitle(state)
        ui.wait.visibility = if (state is GoState.Idle) View.VISIBLE else View.GONE
        ui.stop.visibility = if (ui.wait.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    override fun updateTitle(state: GoState) {
        activity.title = getString(ui.getSateCaption(state)) + if (state.lengthMinutes > 0) " ${state.lengthMinutes} " + getString(R.string.min) else "" // CUR never displays 0 min
    }

    override fun <T> bindToLifeCycle() = RxLifecycle.bindView<T>(ui.ride.parent as View)

    companion object : InjektMain() {
        override fun InjektRegistrar.registerInjectables() {
            addSingleton(fullType(), { LoadObservable(FirebaseStateRepo().get()) })
            addSingleton(fullType(), { state: GoState -> FirebaseStateRepo().set(state) })
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

