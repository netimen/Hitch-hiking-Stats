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

    @Test
    fun testLoadDataOnce() {
        repo.loadDataOnce(firebase) {
            it
        }
    }
}