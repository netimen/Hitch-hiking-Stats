package ru.netimen.hitch_hikingstats

import com.firebase.client.*
import com.soikonomakis.rxfirebase.RxFirebase
import rx.Observable
import java.util.*

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

private fun Firebase.onDataLoaded(onDataLoaded: (DataSnapshot) -> Unit) = addListenerForSingleValueEvent(object : ValueEventListener {
    override fun onDataChange(p0: DataSnapshot?) = p0?.let(onDataLoaded) ?: Unit

    override fun onCancelled(p0: FirebaseError?) {
    }
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


private fun changeRideExtraData(ref: Firebase, ride: Ride, fn: (Firebase, Ride) -> Unit) {
    fn(ref, ride)
    fn(ref.trip(ride.trip), ride)
}

private fun addRideExtraData(ref: Firebase, ride: Ride) {
    ref.child("waitMinutes").runTransaction { add(ride.waitMinutes) }
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

fun minWait(ref: Firebase) = minMaxWait(ref, Query::limitToFirst)
fun maxWait(ref: Firebase) = minMaxWait(ref, Query::limitToLast)

private fun minMaxWait(ref: Firebase, lmt: Query.(Int) -> Query) = RxFirebase.getInstance()
        .observeSingleValue(ref.child("rides").orderByChild("waitMinutes").lmt(1))
        .map(::extractRides)
        .map { it[0].waitMinutes }

private fun extractRides(it: DataSnapshot) = (it?.value as HashMap<String, HashMap<String, *>>).map { it -> Ride(it.value["trip"] as String, it.value["car"] as String, (it.value["waitMinutes"] as Long).toInt(), (it.value["carMinutes"]as Long).toInt()) }


//class FirebaseModel<T> :T{
//    var id: String? = null
//}

var Ride.id by AddFieldDelegate<Ride, String?>(null)


abstract class FirebaseRepo internal constructor() : RidesRepo { // CUR use delegation instead https://kotlinlang.org/docs/reference/delegation.html
    protected val firebase = Firebase("https://dazzling-heat-4079.firebaseio.com/")
}

//class FirebaseRidesRepo : FirebaseRepo() {
//    override fun getList(query: Repo.Query<TripListParams>): Observable<Result<List<Ride>, ErrorInfo>> {
//        throw UnsupportedOperationException()
//    }
//
//    override fun get(id: String): Observable<Result<Ride, ErrorInfo>> {
//        throw UnsupportedOperationException()
//    }
//
//
//    override fun addOrUpdate(t: Ride) = rideRef(t).onDataLoaded {
//        if (it.exists()) changeRide(firebase, it.value as Ride, t) else addRide(firebase, t)
//    }
//
//    private fun exists(t: Ride, onNotExists: () -> Unit = {}, onExists: (Ride, Firebase) -> Unit) = t.id?.let { onExists(t, firebase.ride(t.id!!)) } ?: onNotExists()
//    //    private fun rideRef(ride: Ride) = firebase.ride(ride.id)
//
//    private fun addRide(ref: Firebase, ride: Ride) {
//        with(ref.rides().push()) {
//            ride.id = this.key
//            setValue(ride)
//        }
//
//        changeRideExtraData(ref, ride, ::addRideExtraData)
//    }
//
//    private fun removeRide(ref: Firebase, ride: Ride) = exists(ride) { ride, rideRef ->
//        rideRef.removeValue()
//        changeRideExtraData(ref, ride, ::removeRideExtraData)
//    }
//    //    private fun removeRide(ref: Firebase, ride: Ride) = with(rideRef(ride)) {
//    //        onDataLoaded {
//    //            if (it.exists()) {
//    //                this@with.removeValue()
//    //                changeRideExtraData(ref, ride, ::removeRideExtraData)
//    //            }
//    //        }
//    //    }
//
//    private fun changeRide(ref: Firebase, old: Ride, new: Ride) {
//        new.id = old.id // make sure we keep the id
//        rideRef(old).setValue(new)
//        changeRideExtraData(ref, new, ::addRideExtraData)
//        changeRideExtraData(ref, old, ::removeRideExtraData)
//    }
//}

