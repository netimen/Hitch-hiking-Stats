package ru.netimen.hitch_hikingstats.services

import com.firebase.client.Firebase
import com.nhaarman.mockito_kotlin.mock
import org.junit.Test

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

    interface A {
        fun a() : Int
    }
//    class AImpl : A {
//        override fun a() = 0
//    }

    interface B : A
    class BImpl : B {
        override fun a() = 1
    }

    interface C : A
    class CImpl : C {
        override fun a() = 2
    }

    class D(val b: B, val c: C) : B by b, C

    @Test
    fun testLoadDataOnce() {
        repo.loadDataOnce(firebase) {
            it
        }
    }
}