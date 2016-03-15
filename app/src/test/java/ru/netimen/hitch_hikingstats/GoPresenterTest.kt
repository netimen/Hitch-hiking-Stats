package ru.netimen.hitch_hikingstats

import com.nhaarman.mockito_kotlin.mock
import org.mockito.Mockito
import ru.netimen.hitch_hikingstats.lib.LoadObservable
import ru.netimen.hitch_hikingstats.lib.wrapResult
import rx.Observable
import rx.Scheduler
import rx.android.plugins.RxAndroidPlugins
import rx.android.plugins.RxAndroidSchedulersHook
import rx.schedulers.Schedulers
import uy.kohesive.injekt.InjektMain
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.fullType

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 *
 * Author: Dmitry Gordeev @dreamindustries.co>
 * Date:   15.03.16
 */
class GoPresenterTest {
    // CUR: state loaded, saved, title updated, ride created, state changes on buttons, observables are unsubscribed
    val view: GoView = mock()
    lateinit var presenter: GoPresenter

    @org.junit.Before
    fun setUp() {
        RxAndroidPlugins.getInstance().registerSchedulersHook(object :RxAndroidSchedulersHook() {
            override fun getMainThreadScheduler(): Scheduler? = Schedulers.immediate()
        });
        Mockito.`when`(view.bindToLifeCycle<GoState>()).thenReturn(Observable.Transformer<GoState, GoState>({it}))
        presenter = GoPresenter(view)
    }

    @org.junit.After
    fun tearDown() {

    }

    @org.junit.Test
    fun testStateLoaded() {
    }

    companion object : InjektMain() {
        override fun InjektRegistrar.registerInjectables() {
            addSingleton(fullType(), { LoadObservable(Observable.just(GoState.Idle() as GoState).wrapResult { ErrorInfo(it)}) })
            addSingleton(fullType(), { state: GoState -> Unit })
        }
    }
}