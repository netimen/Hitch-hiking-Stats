package ru.netimen.hitch_hikingstats.test

import android.app.Fragment
import android.os.Bundle
import android.view.View

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   25.02.16
 */


interface MvpView

//abstract class BaseView<P> : MvpView {
//    protected abstract val presenter: P
//}

//interface EnhancedView<P> : MvpView<P>

abstract class Presenter<V : MvpView> {
    protected var view: V? = null

    fun attachView(view: V) {
        this.view = view
    }

}

abstract class BaseView<P : Presenter<in BaseView<P>>> : MvpView {
    protected abstract val presenter: P

    constructor() {
        presenter.attachView(this)
    }
}

class AP : Presenter<BV>()
class BV : BaseView<Presenter<BV>>() {
    override val presenter = AP()
}

//abstract class AF<P : Presenter<AF<P>>> : Fragment(), MvpView<P> {
////    abstract override val presenter: P
//    //
//    //        override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
//    //            super.onViewCreated(view, savedInstanceState)
//    //            presenter.attachView(this)
//    //        }
//}
//
//class AP : Presenter<AF<AP>>()
//class AAF : AF<AP>()

abstract class MvpFragment<P : Presenter<out MvpFragment<P>>> : Fragment(), MvpView {
    protected abstract val presenter: P

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachView(this)
    }
}

//
class RP : Presenter<AAAF>()

abstract class AAAF : MvpFragment<Presenter<AAAF>>()
//abstract class AAAF2 : MvpFragment<RP>()


//class S<in T> {
////    val t: T? = null
////    fun inf(t: T) = 0
////    fun outf() = t!!
//}

fun test(a: Presenter<out MvpView>) {
    val b: MvpView = a.getView2()
    val c: Presenter<out MvpFragment<Presenter<AAAF>>> = RP()
    //    a.vi
    //    val a: MvpFragment<Presenter<MvpFragment>>
}
