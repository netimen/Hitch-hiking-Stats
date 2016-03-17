package ru.netimen.hitch_hikingstats

import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import ru.netimen.hitch_hikingstats.lib.LoadObservable
import ru.netimen.hitch_hikingstats.lib.wrapResult
import rx.Observable
import rx.Scheduler
import rx.internal.util.ScalarSynchronousObservable
import rx.lang.kotlin.PublishSubject
import rx.plugins.RxJavaPlugins
import rx.plugins.RxJavaSchedulersHook
import rx.schedulers.TestScheduler
import java.util.concurrent.TimeUnit

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 *
 * Author: Dmitry Gordeev @dreamindustries.co>
 * Date:   15.03.16
 */
class GoPresenterTest {
    // CUR: observables are unsubscribed
    val view: GoView = mock()
    val logic: GoLogic = mock()

    val stopClicked = PublishSubject<Unit>()
    val waitClicked = PublishSubject<Unit>()
    val rideClicked = PublishSubject<Unit>()

    val backgroundScheduler = TestScheduler()

    lateinit var presenter: GoPresenter

    @Before
    fun setUp() {
        RxJavaPlugins.getInstance().registerSchedulersHook(object : RxJavaSchedulersHook() {
            //            override fun getComputationScheduler(): Scheduler? = backgroundScheduler
                        override fun getIOScheduler(): Scheduler? = backgroundScheduler // waiting for this: https://groups.google.com/forum/#!topic/rxjava/Dz0kBH-RdAo
        })
        //        RxAndroidPlugins.getInstance().registerSchedulersHook(object : RxAndroidSchedulersHook() {
        //            override fun getMainThreadScheduler(): Scheduler? = Schedulers.immediate()
        //        });
        `when`(view.bindToLifeCycle<GoState>()).thenReturn(Observable.Transformer<GoState, GoState>({ it }))
        `when`(view.stopClicked()).thenReturn(stopClicked) // cur automate creation of these subjects
        `when`(view.waitClicked()).thenReturn(waitClicked)
        `when`(view.rideClicked()).thenReturn(rideClicked)
    }

    @Test
    fun testStateLoaded() {
        createPresenter(GoState.Idle())

        verify(logic).loadState()
        verifyNoMoreInteractions(logic) // checking we don't save the same state again
    }

    @Test
    fun testRideAdded() {
        val state = GoState.Riding("Toyota", 5)
        createPresenter(state)
        stopClicked.onNext(Unit)

        verify(logic).addRide(state)
    }

    @Test
    fun testCorrectStateSaved() {
        createPresenter(GoState.Idle())
        waitClicked.onNext(Unit)
        rideClicked.onNext(Unit)
        stopClicked.onNext(Unit)

        //        verify(saveState).invoke(Mockito.argThat(CapturingMatcher<GoState>()) ?: createInstance())
        verify(logic).saveState(isA<GoState.Waiting>() ?: createInstance()) // CUR more elegant sequence checking
        verify(logic).saveState(isA<GoState.Riding>() ?: createInstance())
        verify(logic).saveState(isA<GoState.Idle>() ?: createInstance())
        //                verify(saveState).invoke(eq())
        //        verify(saveState).invoke(Mockito.argThat(sequence(isA<GoState.Idle>(), isA<GoState.Waiting>())) ?: createInstance())
        //        verify(saveState, times(2)).invoke(Mockito.argThat(sequence<GoState>(InstanceOf(GoState.Idle::class.java), InstanceOf(GoState.Waiting::class.java))) ?: createInstance())
        //        verify(saveState).invoke(AdditionalMatchers.and(isA<GoState.Idle>(), isA<GoState.Waiting>()) ?: createInstance())
    }

    @Test
    fun testTitleUpdatedEveryMinute() {
        createPresenter(GoState.Waiting())
        verify(view, never()).updateTitle(any())

        val minutes = 5L
        backgroundScheduler.advanceTimeBy(minutes, TimeUnit.MINUTES)
        verify(view, times(minutes.toInt())).updateTitle(any())
    }

    @Test
    fun testTitleUpdatedTimerRestartsOnStateChanged() {
        createPresenter(GoState.Waiting())

        backgroundScheduler.advanceTimeBy(30, TimeUnit.SECONDS)
        rideClicked.onNext(Unit)
        backgroundScheduler.advanceTimeBy(59, TimeUnit.SECONDS)

        verify(view, never()).updateTitle(any())
    }

    //    private fun <T> sequence(vararg matchers: ArgumentMatcher<Any>): ArgumentMatcher<T> {
    //        return object :ArgumentMatcher<T> {
    //            var index = 0
    //            override fun matches(p0: Any?): Boolean {
    //                return matchers[index++].matches(p0)
    //            }
    //
    //        }
    //    }


    private fun createPresenter(state: GoState) {
        `when`(logic.loadState()).thenReturn(LoadObservable(Observable.just(state).wrapResult { ErrorInfo(it) }))
        presenter = GoPresenter(view, logic)
    }

    //    companion object {
    ////        val loadState: () -> LoadObservable<GoState, ErrorInfo> = mock()
    ////        val saveState: (GoState) -> Unit = mock()
    //
    //        val injector = object : InjektMain() {
    //
    //            override fun InjektRegistrar.registerInjectables() {
    //                addSingleton(fullType(), loadState)
    //                addSingleton(fullType(), saveState)
    //            }
    //        }
    //    }
}