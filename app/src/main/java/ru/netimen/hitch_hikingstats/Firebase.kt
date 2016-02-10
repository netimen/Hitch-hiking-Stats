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
//    fun Hitch.addExtraData(ref: Firebase) = addHitchExtraData(ref, this)
//    fun Ride.addExtraData(ref: Firebase) = addHitchExtraData(ref, this)

fun addRide(ref: Firebase, ride: Ride) {
    with(ref.child("rides").push()) {
        ride.id = this.key
        setValue(ride)
    }

    addRideExtraData(ref, ride)
    addRideExtraData(ref.child("trips").child(ride.trip), ride)
}

private fun addRideExtraData(ref: Firebase, ride: Ride) {
    ref.child("waitMinutes").runTransaction { add(ride.waitMinutes) }
    //        ref.child("minWait").runTransaction { value = initialValue().run { if (this > ride.waitMinutes) ride.waitMinutes else this } }
    //        ref.child("maxWait").runTransaction { value = initialValue().run { if (this < ride.waitMinutes) ride.waitMinutes else this } }
    if (ride.hasCar()) {
        ref.child("carMinutes").runTransaction { add(ride.carMinutes) }
        ref.child("cars").child(ride.car).runTransaction { add(1) } // CUR carTime
    }
}

private fun removeRideExtraData(ref: Firebase, ride: Ride) {
    ref.child("waitMinutes").runTransaction { subtract(ride.waitMinutes) }
    //        ref.child("minWait").runTransaction { value = initialValue().run { if (this > ride.waitMinutes) ride.waitMinutes else this } }
    //        ref.child("maxWait").runTransaction { value = initialValue().run { if (this < ride.waitMinutes) ride.waitMinutes else this } }
    if (ride.hasCar()) {
        ref.child("carMinutes").runTransaction { subtract(ride.carMinutes) }
        ref.child("cars").child(ride.car).runTransaction {
            subtract(1)
            if (value as Long == 0L)
                ref.child("cars").child(ride.car).removeValue()
        }
    }
}

fun removeRide(ref: Firebase, ride: Ride) {
    with(ref.child("rides").child(ride.id)) {
        addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: FirebaseError?) {
            }

            override fun onDataChange(p0: DataSnapshot?) {
                if (p0?.exists() ?: false) {
                    this@with.removeValue()
                    removeRideExtraData(ref, ride)
                    removeRideExtraData(ref.child("trips").child(ride.trip), ride)
                }
            }
        })
    }
}
