/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 *
 * Author: Dmitry Gordeev @dreamindustries.co>
 * Date:   18.03.16
 */
package ru.netimen.hitch_hikingstats.test

import rx.Scheduler
import rx.internal.util.ScalarSynchronousObservable
import rx.schedulers.Schedulers

object RxJavaTestHacks {

    fun hackComputationScheduler(testScheduler: Scheduler) {
        try {
            ScalarSynchronousObservable.create(1)
            val instanceField = Schedulers::class.java.getDeclaredField("INSTANCE")
            instanceField.isAccessible = true
            val instance = instanceField.get(null)
            val f = instance.javaClass.getDeclaredField("computationScheduler")
            f.isAccessible = true
            f.set(instance, testScheduler)
        } catch (ignored: Exception) {
        }

    }
}
