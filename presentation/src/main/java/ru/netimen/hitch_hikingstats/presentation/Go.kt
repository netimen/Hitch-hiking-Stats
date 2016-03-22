package ru.netimen.hitch_hikingstats.presentation

import ru.netimen.hitch_hikingstats.domain.ErrorInfo
import ru.netimen.hitch_hikingstats.domain.GoState
import ru.netimen.hitch_hikingstats.domain.StateRepo
import rx.Observable
import rx.Subscription
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   20.03.16
 */

interface GoView : MvpView {
    fun rideClicked(): Observable<Unit>
    fun stopClicked(): Observable<Unit>
    fun waitClicked(): Observable<Unit>
    fun carSelected(): Observable<String>

    fun showState(state: GoState)
    fun updateTitle(state: GoState)
}

class GoLogic(private val stateRepo: StateRepo) : Logic {
    fun loadState() = LoadObservable(stateRepo.get()) // CUR usecase?
    fun saveState(state: GoState) = stateRepo.set(state)

    fun addRide(state: GoState) = TODO()
}

// cur notification
class GoPresenter( logic: GoLogic, view: GoView) : Presenter<GoLogic, GoView>(logic, view) {
    private var state by Delegates.countedObservable<GoState>(GoState.Idle()) { prop, old, new, setCount ->
        if (setCount > 0)
            logic.saveState(new)

        onStateUpdated(new)
    }
    private var updateTitleSubscription: Subscription? = null

    init {
        logic.loadState().onData { state = it }.subscribe() // cur lifecycle here

        view.stopClicked().bindToLifecycle().subscribe {
            logic.addRide(state)
            state = GoState.Idle()
        }
        view.waitClicked().bindToLifecycle().subscribe { state = GoState.Waiting() }
        view.rideClicked().bindToLifecycle().subscribe { state = GoState.Riding("Toyota", state.lengthMinutes) }//CUR: get car
    }

    private fun onStateUpdated(newState: GoState) {
        updateTitleSubscription?.unsubscribe()
        updateTitleSubscription = Observable.interval(1, TimeUnit.MINUTES).bindToLifecycle().subscribe { view.updateTitle(state) }

        view.showState(newState)
    }

}

