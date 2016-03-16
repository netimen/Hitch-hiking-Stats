package ru.netimen.hitch_hikingstats

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import ru.netimen.hitch_hikingstats.lib.LoadObservable
import ru.netimen.hitch_hikingstats.lib.wrapResult
import rx.Observable
import rx.Scheduler
import rx.android.plugins.RxAndroidPlugins
import rx.android.plugins.RxAndroidSchedulersHook
import rx.lang.kotlin.PublishSubject
import rx.schedulers.Schedulers
import uy.kohesive.injekt.InjektMain
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.fullType
import org.mockito.Mockito.`when`
import com.nhaarman.mockito_kotlin.*
import org.mockito.AdditionalMatchers
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatcher
import org.mockito.Mockito
import org.mockito.internal.matchers.InstanceOf

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 *
 * Author: Dmitry Gordeev @dreamindustries.co>
 * Date:   15.03.16
 */
class GoPresenterTest {
    // CUR: saved, title updated, ride created, state changes on buttons, observables are unsubscribed
    val view: GoView = mock()
    val stopClicked = PublishSubject<Unit>()
    val waitClicked = PublishSubject<Unit>()
    val rideClicked = PublishSubject<Unit>()
    lateinit var presenter: GoPresenter

    @Before
    fun setUp() {
        RxAndroidPlugins.getInstance().registerSchedulersHook(object : RxAndroidSchedulersHook() {
            override fun getMainThreadScheduler(): Scheduler? = Schedulers.immediate()
        });
        `when`(view.bindToLifeCycle<GoState>()).thenReturn(Observable.Transformer<GoState, GoState>({ it }))
        `when`(view.stopClicked()).thenReturn(stopClicked) // cur automate creation of these subjects
        `when`(view.waitClicked()).thenReturn(waitClicked)
        `when`(view.rideClicked()).thenReturn(rideClicked)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun testStateLoaded() {
        createPresenter(GoState.Idle())

        verify(loadState).invoke()
        verifyZeroInteractions(saveState) // checking we don't save the same state again
    }

    @Test
    fun testCorrectStateSaved() {
        createPresenter(GoState.Idle())
        waitClicked.onNext(Unit)
        rideClicked.onNext(Unit)
        stopClicked.onNext(Unit)

        verify(saveState).invoke(isA<GoState.Waiting>() ?: createInstance())
        verify(saveState).invoke(isA<GoState.Riding>() ?: createInstance())
        verify(saveState).invoke(isA<GoState.Idle>() ?: createInstance())
//                verify(saveState).invoke(eq())
//        verify(saveState).invoke(Mockito.argThat(sequence(isA<GoState.Idle>(), isA<GoState.Waiting>())) ?: createInstance())
//        verify(saveState, times(2)).invoke(Mockito.argThat(sequence<GoState>(InstanceOf(GoState.Idle::class.java), InstanceOf(GoState.Waiting::class.java))) ?: createInstance())
//        verify(saveState).invoke(AdditionalMatchers.and(isA<GoState.Idle>(), isA<GoState.Waiting>()) ?: createInstance())
    }

    private fun <T> sequence(vararg matchers: ArgumentMatcher<Any>): ArgumentMatcher<T> {
        return object :ArgumentMatcher<T> {
            var index = 0
            override fun matches(p0: Any?): Boolean {
                return matchers[index++].matches(p0)
            }

        }
    }


    private fun createPresenter(state: GoState) {
        `when`(loadState()).thenReturn(LoadObservable(Observable.just(state).wrapResult { ErrorInfo(it) }))
        presenter = GoPresenter(view)
    }

    companion object {
        val loadState: () -> LoadObservable<GoState, ErrorInfo> = mock()
        val saveState: (GoState) -> Unit = mock()

        val injector = object : InjektMain() {

            override fun InjektRegistrar.registerInjectables() {
                addSingleton(fullType(), loadState)
                addSingleton(fullType(), saveState)
            }
        }
    }
}