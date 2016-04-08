package ru.netimen.hitch_hikingstats.services

import com.firebase.client.Firebase
import com.nhaarman.mockito_kotlin.mock
import org.junit.Test
import rx.Observable
import rx.lang.kotlin.PublishSubject
import java.util.concurrent.TimeUnit

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 *
 * Author: Dmitry Gordeev @dreamindustries.co>
 * Date:   15.03.16
 */

class FirebaseRepoTest { // CUR test what happens on error; test this func, test data conversion

    val firebase: Firebase = mock()
    val repo = object : FirebaseRepo(firebase) {}

    @Test
    fun testLoadDataOnce() {

        val intent = PublishSubject<Int>()
        intent
                .flatMap {
                    when (it) {
                        2 -> Observable.interval(1, TimeUnit.SECONDS).map { "sec$it" }.takeUntil
//                        2 -> Observable.just("a")
                    //        updateTitleSubscription = Observable.interval(1, TimeUnit.MINUTES).bindToLifecycle().subscribe { view.updateTitle(state) }
                        else -> Observable.error<String>(NullPointerException("aaaa")).onErrorReturn { "cccc" }
                    }
                }
                .subscribe({ print("\nbbbbb $it") }, { print("eeeeeee $it") })
        intent.onNext(1)
        intent.onNext(2)
        intent.onNext(3)
        intent.onNext(2)
        Thread.sleep(10000);
        repo.loadDataOnce(firebase) {
            it
        }
    }
}