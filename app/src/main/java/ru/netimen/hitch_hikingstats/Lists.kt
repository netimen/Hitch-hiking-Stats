package ru.netimen.hitch_hikingstats

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import org.jetbrains.anko.*
import org.jetbrains.anko.design.floatingActionButton
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.UI
import ru.netimen.hitch_hikingstats.lib.*
import java.util.*

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   03.03.16
 */

class RidesFragment : ListFragment<Ride, RidesPresenter, RidesFragment, RideView>() {
    private lateinit var add: View
    override val adapter = object : SimpleListAdapter<Ride, RideView>({ RideView(it.context) }, { rideView, ride -> rideView.bind(ride) }) {}// CUR make interface bindable
    override var presenter = RidesPresenter() // CUR make base fragment instantiate presenter
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
}

class RidesPresenter : PagingPresenter<Ride, ErrorInfo, RidesFragment>(GetListUseCase<Ride, ErrorInfo, TripListParams, RidesRepo>(FirebaseRidesRepo(), TripListParams(""), 20)) // CUR presenters shouldn't know about fragment

class CarsFragment : ListFragment<Car, CarsPresenter, CarsFragment, TextView>() {
    override val adapter = object : SimpleListAdapter<Car, TextView>({ TextView(it.context) }, { carView, car -> carView.text = car.toString() }) {}
    override var presenter = CarsPresenter()
}

class CarsPresenter : PagingPresenter<Car, ErrorInfo, CarsFragment>(GetListUseCase<Car, ErrorInfo, TripListParams, CarsRepo>(FirebaseCarsRepo(), TripListParams(""), 20))

