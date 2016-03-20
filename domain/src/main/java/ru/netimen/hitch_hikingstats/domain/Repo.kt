package ru.netimen.hitch_hikingstats.domain

import ru.netimen.hitch_hikingstats.presentation.ListParams
import ru.netimen.hitch_hikingstats.presentation.Repo
import ru.netimen.hitch_hikingstats.presentation.ValueRepo

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   10.03.16
 */

class TripListParams(val trip: String) : ListParams

class ErrorInfo(val t: Throwable) // cur move to presentation

interface HitchRepo<T> : Repo<T, ErrorInfo, TripListParams>

interface IdRepo<T : IdObject> : HitchRepo<T>

interface RidesRepo : IdRepo<Ride>

interface CarsRepo : HitchRepo<Car>

interface StateRepo: ValueRepo<GoState, ErrorInfo>
