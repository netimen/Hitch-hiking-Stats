package ru.netimen.hitch_hikingstats

import com.firebase.client.*

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   10.02.16
 */

private fun Firebase.runTransaction(transformValue: MutableData.() -> Unit) = runTransaction(object : Transaction.Handler {
    override fun onComplete(p0: FirebaseError?, p1: Boolean, p2: DataSnapshot?) {
    }

    override fun doTransaction(p0: MutableData?): Transaction.Result? = Transaction.success(p0?.apply(transformValue))

})

private fun MutableData.add(v: Int) {
    value = initialValue() + v
}

private fun MutableData.subtract(v: Int) {
    value = initialValue() - v
}

private fun MutableData.initialValue() = if (value == null) 0 else value as Long

private fun Firebase.trips() = child("trips")
private fun Firebase.trip(key: String) = trips().child(key)
private fun Firebase.rides() = child("rides")
private fun Firebase.ride(key: String) = rides().child(key)
private fun Firebase.cars() = child("cars")
private fun Firebase.car(key: String) = cars().child(key)

fun addRide(ref: Firebase, ride: Ride) {
    with(ref.rides().push()) {
        ride.id = this.key
        setValue(ride)
    }

    extraData(ref, ride, ::addRideExtraData)
}

fun removeRide(ref: Firebase, ride: Ride) = with(ref.ride(ride.id)) {
    addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onCancelled(p0: FirebaseError?) {
        }

        override fun onDataChange(p0: DataSnapshot?) {
            if (p0?.exists() ?: false) {
                this@with.removeValue()
                extraData(ref, ride, ::removeRideExtraData)
            }
        }
    })
}

fun changeRide(ref: Firebase, old: Ride, new: Ride) {
    new.id = old.id // make sure we keep the id
    ref.ride(old.id).setValue(new)
    extraData(ref, new, ::addRideExtraData)
    extraData(ref, old, ::removeRideExtraData)
}

private fun extraData(ref: Firebase, ride: Ride, fn: (Firebase, Ride) -> Unit) = with(ref) {
    fn(ref, ride)
    fn(ref.trip(ride.trip), ride)
}

private fun addRideExtraData(ref: Firebase, ride: Ride) {
    ref.child("waitMinutes").runTransaction { add(ride.waitMinutes) }
    //        ref.child("minWait").runTransaction { value = initialValue().run { if (this > ride.waitMinutes) ride.waitMinutes else this } }
    //        ref.child("maxWait").runTransaction { value = initialValue().run { if (this < ride.waitMinutes) ride.waitMinutes else this } }
    if (ride.hasCar()) {
        ref.child("carMinutes").runTransaction { add(ride.carMinutes) }
        ref.car(ride.car).runTransaction { add(1) } // CUR carTime
    }
}

private fun removeRideExtraData(ref: Firebase, ride: Ride) {
    ref.child("waitMinutes").runTransaction { subtract(ride.waitMinutes) }

    if (ride.hasCar()) {
        ref.child("carMinutes").runTransaction { subtract(ride.carMinutes) }
        ref.car(ride.car).runTransaction {
            subtract(1)
            if (value as Long == 0L)
                ref.car(ride.car).removeValue()
        }
    }
}

