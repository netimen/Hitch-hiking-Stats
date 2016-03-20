package ru.netimen.hitch_hikingstats

import ru.netimen.hitch_hikingstats.presentation.notEmpty
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

data class Ride constructor(override var id: String?, val trip: String, val car: String, val waitMinutes: Int, val carMinutes: Int) : IdObject { // CUR make this constructor inaccessible

    constructor(trip: String, car: String, waitMinutes: Int, carMinutes: Int) : this(null, trip, car, waitMinutes, carMinutes)

    constructor(trip: String, waitMinutes: Int) : this(trip, "", waitMinutes, 0)

    fun hasCar() = carMinutes != 0

    fun sameTrip(trip: String?) = trip.notEmpty()?.equals(this.trip) ?: true
}// : Hitch(trip, waitMinutes) // cur creationMillis


data class Trip(val carMinutes: Int, val waitMinutes: Int, val minWait: Int, val maxWait: Int)

private class LengthMinutesDelegate<T>(private val creationMillis: Long) {
    operator fun getValue(t: T, property: KProperty<*>): Int = Math.max(TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - creationMillis).toInt(), 1)
}

sealed class GoState(val creationMillis: Long) {
    open val lengthMinutes: Int by LengthMinutesDelegate(creationMillis)

    class Idle : GoState(0) {
        override val lengthMinutes = 0
    }

    class Waiting(creationMillis: Long = System.currentTimeMillis()) : GoState(creationMillis)
    class Riding(val carName: String, val waitMinutes: Int, creationMillis: Long = System.currentTimeMillis()) : GoState(creationMillis)
}