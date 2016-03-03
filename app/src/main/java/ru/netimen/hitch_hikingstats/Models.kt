package ru.netimen.hitch_hikingstats

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
