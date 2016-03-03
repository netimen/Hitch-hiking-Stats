package ru.netimen.hitch_hikingstats

import com.firebase.client.*
import com.soikonomakis.rxfirebase.RxFirebase
import ru.netimen.hitch_hikingstats.lib.*
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

//fun minWait(ref: Firebase) = minMaxWait(ref, Query::limitToFirst)
//fun maxWait(ref: Firebase) = minMaxWait(ref, Query::limitToLast)

//private fun minMaxWait(ref: Firebase, lmt: Query.(Int) -> Query) = RxFirebase.getInstance()
//        .observeSingleValue(ref.child("rides").orderByChild("waitMinutes").lmt(1))
//        .map(::extractRides)
//        .map { it[0].waitMinutes }

//private fun extractRides(it: DataSnapshot) = (it?.value as HashMap<String, *>).map { it -> Ride(it.value["trip"] as String, it.value["car"] as String, (it.value["waitMinutes"] as Long).toInt(), (it.value["carMinutes"]as Long).toInt()) }


//class FirebaseModel<T> :T{
//    var id: String? = null
//}

//var Ride.id by AddFieldDelegate<Ride, String?>(null)


// cur thread-safety
abstract class FirebaseRepo<T> internal constructor(url: String) : HitchRepo<T> {
    protected val firebase = Firebase(url)

    override fun getList(query: Repo.Query<TripListParams>): Observable<Result<List<T>, ErrorInfo>> = RxFirebase.getInstance()
            .observeSingleValue(query.listParams.trip.notEmpty()?.let { tripObjectsRef(it) } ?: objectsRef())
            .first() // RxFirebase observeSingleValue has a bug https://github.com/spirosoik/Android-RxFirebase/issues/2
            .map({ extractObjects(it) })
            .wrapResult { ErrorInfo(it) }

    protected open fun tripObjectsRef(trip: String): Query = objectsRef(firebase.trip(trip))

    override fun get(id: String): Observable<Result<T, ErrorInfo>> = throw UnsupportedOperationException()

    protected abstract fun objectsRef(root: Firebase): Firebase

    protected fun objectsRef() = objectsRef(firebase)

    protected fun extractObjects(dataSnapshot: DataSnapshot) = (dataSnapshot.value as? Map<String, *>)?.map { extractObject(it.key, it.value!!) } ?: ArrayList()

    protected abstract fun extractObject(key: String, value: Any): T
}

abstract class FirebaseIdRepo<T : IdObject> internal constructor(url: String) : FirebaseRepo<T>(url) {

    override fun addOrUpdate(t: T) = exists(t, { add(t) }) { ride, rideRef -> change(rideRef, ride, t) }

    override fun remove(t: T) = exists(t) { t, objectRef ->
        objectRef.removeValue()
        onRemove(t)
    }

    private fun objectRef(t: T) = t.id?.let { objectsRef().child(it) }

    protected fun add(t: T) = with(objectsRef().push()) {
        setValue(t.apply { id = key })
        onAdd(t)
    }

    protected fun change(objectRef: Firebase, old: T, new: T) {
        new.id = old.id // make sure we keep the id
        objectRef.setValue(new)
        onChange(old, new)
    }

    protected open fun onAdd(t: T) = Unit

    protected open fun onRemove(t: T) = Unit

    protected open fun onChange(old: T, new: T) = Unit

    protected fun exists(t: T, onNotExists: (T) -> Unit = {}, onExists: (T, Firebase) -> Unit) = objectRef(t)?.run {
        onDataLoaded { if (it.exists()) onExists(extractObject(it.key, it.value), this) else onNotExists(t) }
    } ?: onNotExists(t)
}

class FirebaseRidesRepo(url: String) : FirebaseIdRepo<Ride>(url), RidesRepo {
    constructor() : this(URL)

    override fun onAdd(t: Ride) = changeRideExtraData(firebase, t, ::addRideExtraData)

    override fun onChange(old: Ride, new: Ride) {
        changeRideExtraData(firebase, new, ::addRideExtraData)
        changeRideExtraData(firebase, old, ::removeRideExtraData)
    }

    override fun onRemove(t: Ride) = changeRideExtraData(firebase, t, ::removeRideExtraData)

    override fun extractObject(key: String, value: Any) = (value as Map<*, *>).let { Ride(it["id"] as String, it["trip"] as String, it["car"] as String, (it["waitMinutes"] as Long).toInt(), (it["carMinutes"] as Long).toInt()) }

    override fun objectsRef(root: Firebase): Firebase = root.child("rides")

    override fun tripObjectsRef(trip: String) = objectsRef().orderByChild("trip").equalTo(trip)
}

class FirebaseCarsRepo(url: String) : FirebaseRepo<Car>(url), CarsRepo {
    constructor() : this(URL)

    override fun objectsRef(root: Firebase): Firebase = root.child("cars")

    override fun extractObject(key: String, value: Any) = Car(key, (value as Long).toInt())

    override fun addOrUpdate(t: Car) = throw UnsupportedOperationException()

    override fun remove(t: Car) = throw UnsupportedOperationException()
}

private val URL = "https://dazzling-heat-4079.firebaseio.com/" // cUr support "/test" as main ref


