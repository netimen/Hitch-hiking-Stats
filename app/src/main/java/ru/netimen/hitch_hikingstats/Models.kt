package ru.netimen.hitch_hikingstats

import java.util.concurrent.TimeUnit
import kotlin.reflect.KProperty

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   03.03.16
 */

data class Car(val name: String, val times: Int)

interface IdObject {
    var id: String?
}

data class Ride internal constructor(override var id: String?, val trip: String, val car: String, val waitMinutes: Int, val carMinutes: Int) : IdObject {

    constructor(trip: String, car: String, waitMinutes: Int, carMinutes: Int) : this(null, trip, car, waitMinutes, carMinutes)

    constructor(trip: String, waitMinutes: Int) : this(trip, "", waitMinutes, 0)

    fun hasCar() = carMinutes != 0

    fun sameTrip(trip: String?) = trip.notEmpty()?.equals(this.trip) ?: true
}// : Hitch(trip, waitMinutes)


data class Trip(val carMinutes: Int, val waitMinutes: Int, val minWait: Int, val maxWait: Int)

private class LengthMinutesDelegate<T>() {
    private val creationMillis = System.currentTimeMillis()
    operator fun getValue(t: T, property: KProperty<*>): Int = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - creationMillis).toInt()
}

sealed class GoState {
    //    abstract class StateWithLength() : GoState() {
    //    open val lengthMinutes: Int = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) as Int
    //        get() = ((System.currentTimeMillis() - field) / 60 / 1000) as Int

    open val lengthMinutes: Int by LengthMinutesDelegate()
    //    open val lengthMinutes: Int by lazy { }
    //        get() = ((System.currentTimeMillis() - field) / 60 / 1000) as Int
    //    }

    class Idle : GoState() {
        override val lengthMinutes = 0
    }

    class Waiting : GoState()
    class Riding(val car: Car, val waitMinutes: Int) : GoState()

}