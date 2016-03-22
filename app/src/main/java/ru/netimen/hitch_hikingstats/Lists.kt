package ru.netimen.hitch_hikingstats

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.jetbrains.anko.design.floatingActionButton
import org.jetbrains.anko.dimen
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.margin
import org.jetbrains.anko.support.v4.UI
import ru.netimen.hitch_hikingstats.domain.*
import ru.netimen.hitch_hikingstats.lib.ListFragment
import ru.netimen.hitch_hikingstats.lib.SimpleListAdapter
import ru.netimen.hitch_hikingstats.presentation.GetListUseCase
import ru.netimen.hitch_hikingstats.presentation.Logic
import ru.netimen.hitch_hikingstats.presentation.PagingPresenter
import rx.Observable

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   03.03.16
 */

class RidesFragment : ListFragment<Ride, ErrorInfo, Logic, RidesPresenter, RidesFragment, RideView>() {
    private lateinit var add: View
    override val adapter = object : SimpleListAdapter<Ride, RideView>({ RideView(it.context) }, { rideView, ride -> rideView.bind(ride) }) {}// CUR make interface bindable

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? = UI {
        frameLayout {
            addView(super.onCreateView(inflater, container, savedInstanceState))
            add = floatingActionButton {
                imageResource = R.drawable.ic_add
            }.lparams {
                gravity = Gravity.RIGHT or Gravity.BOTTOM
                margin = dimen(R.dimen.margin_big)
            }
        }
    }.view

    override fun <T> bindToLifecycle(): Observable.Transformer<T, T> = Observable.Transformer({ it })
}

class RidesPresenter(logic: Logic, view: RidesFragment) : PagingPresenter<Ride, ErrorInfo, Logic, RidesFragment>(logic, view, GetListUseCase<Ride, ErrorInfo, TripListParams, RidesRepo>(FirebaseRidesRepo(), TripListParams(""), 20)) // CUR presenters shouldn't know about fragment

class CarsFragment : ListFragment<Car, ErrorInfo, Logic, CarsPresenter, CarsFragment, TextView>() {
    override val adapter = object : SimpleListAdapter<Car, TextView>({ TextView(it.context) }, { carView, car -> carView.text = car.toString() }) {}

    override fun <T> bindToLifecycle(): Observable.Transformer<T, T> = Observable.Transformer({ it })
}

class CarsPresenter(logic: Logic, view: CarsFragment) : PagingPresenter<Car, ErrorInfo, Logic, CarsFragment>(logic, view, GetListUseCase<Car, ErrorInfo, TripListParams, CarsRepo>(FirebaseCarsRepo(), TripListParams(""), 20))

