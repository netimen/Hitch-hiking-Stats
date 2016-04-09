package ru.netimen.hitch_hikingstats.services

import ru.netimen.hitch_hikingstats.domain.ErrorInfo
import ru.netimen.hitch_hikingstats.domain.Ride
import ru.netimen.hitch_hikingstats.domain.RidesRepo
import ru.netimen.hitch_hikingstats.domain.TripListParams
import ru.netimen.hitch_hikingstats.presentation.Repo
import ru.netimen.hitch_hikingstats.presentation.Result
import ru.netimen.hitch_hikingstats.presentation.wrapResult
import rx.Observable
import java.util.*

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   18.02.16
 */

class MemoryRidesRepo : RidesRepo {
    private val rides = HashSet<Ride>()

    override fun getList(query: Repo.Query<TripListParams>): Observable<Result<List<Ride>, ErrorInfo>> = Observable.just(rides.filter { it.sameTrip(query.listParams.trip) }).wrapResult { ErrorInfo(it) }

    override fun get(id: String): Observable<Result<Ride, ErrorInfo>> = throw UnsupportedOperationException()

    override fun addOrUpdate(t: Ride) {
        rides.add(t)
    }

    override fun remove(t: Ride) {
        rides.remove(t)
    }

}


