package ru.netimen.hitch_hikingstats.test

import junit.framework.Assert
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runners.model.Statement
import rx.Observable
import rx.Scheduler
import rx.Subscription
import rx.internal.schedulers.EventLoopsScheduler
import rx.lang.kotlin.PublishSubject
import rx.plugins.RxJavaObservableExecutionHook
import rx.plugins.RxJavaPlugins
import rx.plugins.RxJavaSchedulersHook
import rx.schedulers.TestScheduler
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   18.03.16
 */

class RxTestRule : TestWatcher() {
    val backgroundScheduler = TestScheduler()
    private val lifecycle by lazy { PublishSubject<Unit>() }
    private val subscriptionsCollector = SubscriptionCollector()

    override fun apply(base: Statement?, description: Description?): Statement? {
        RxJavaPlugins.getInstance().registerObservableExecutionHook(object : RxJavaObservableExecutionHook() {
            override fun <T : Any?> onSubscribeReturn(subscription: Subscription?): Subscription? {
                return subscription.apply { subscriptionsCollector.add(this) }
            }
        })

        RxJavaPlugins.getInstance().registerSchedulersHook(object : RxJavaSchedulersHook() {
            override fun getComputationScheduler(): Scheduler? = EventLoopsScheduler()
        })
        RxJavaTestHacks.hackComputationScheduler(backgroundScheduler) // https://groups.google.com/forum/#!topic/rxjava/Dz0kBH-RdAo

        return super.apply(base, description)
    }

    override fun succeeded(description: Description?) {
        lifecycle.onNext(Unit)
        subscriptionsCollector.verifyAllSubscriptionsUnsubscribed()
    }

    fun <T> bindToLifecycle() = Observable.Transformer<T, T> { source -> source?.takeUntil(lifecycle) }
}

class SubscriptionCollector {
    val subscriptions = Collections.synchronizedMap(HashMap<Subscription, String>())

    fun add(subscription: Subscription?) = subscriptions.put(subscription, getStackTraceString(Exception("Subscription still subscribed!")))

    fun verifyAllSubscriptionsUnsubscribed() {
        for ((subscription, trace)in subscriptions)
            if (!subscription.isUnsubscribed)
                Assert.fail("There was an non-unsubscribed subscription detected which was created at the following place:\n<-------------------- start unsubscribed stack -------------------->\n$trace<--------------------- end unsubscribed stack --------------------->")
        subscriptions.clear()
    }
}

private fun getStackTraceString(tr: Throwable): String? {
    val sw = StringWriter()
    val pw = PrintWriter(sw)
    tr.printStackTrace(pw)
    pw.flush()
    return sw.toString()
}
