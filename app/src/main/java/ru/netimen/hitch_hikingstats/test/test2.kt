package ru.netimen.hitch_hikingstats.test

import android.util.Log
import ru.netimen.hitch_hikingstats.domain.Ride
import rx.Observable
import java.util.*

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   06.04.16
 */
class RideRepo {
    private var generateCount = 0

    fun getRides(): Observable<List<Ride>> = Observable.just(generateRides())

    private fun generateRides(): ArrayList<Ride> {
        Log.e("AAAAA", "AAAAA generate users ${++generateCount}")
        return arrayListOf(Ride("name 1", 1), Ride("name 2", 2))
    }
}

val load = RideRepo().getRides()

interface A {
    fun a(): Int
}

class AImpl : A {
    override fun a(): Int = 0
}

interface B {
    fun a(): Int
    fun b(): Int
}

class C(val a: AImpl) : B, A by a {
    override fun b(): Int {
        throw UnsupportedOperationException()
    }

}
