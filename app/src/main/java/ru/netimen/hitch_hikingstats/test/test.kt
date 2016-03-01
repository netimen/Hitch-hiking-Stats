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

//abstract class BaseView<P : Presenter<in BaseView<P>>> : MvpView {
//    protected abstract val presenter: P
//
//    constructor() {
//        presenter.attachView(this)
//    }
//}
//
//class AP : Presenter<BV>()
//class BV : BaseView<Presenter<BV>>() {
//    override val presenter = AP()
//}

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

interface ViewImpl<V : MvpView> : MvpView

abstract class MvpFragment<P : Presenter<in V>, V : MvpFragment<P, V>> : Fragment(), MvpView {
    protected abstract val presenter: P

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachView(this as V)
    }
}

interface AV : MvpView {
    fun showA()
}

class AP : Presenter<AV>() {
    fun loadA() = throw UnsupportedOperationException()
}

class AF : MvpFragment<AP, AF>(), AV {
    override val presenter: AP
        get() = throw UnsupportedOperationException()

    override fun showA() {
        throw UnsupportedOperationException()
    }

}
//
//class RP : Presenter<AAAF>()
//
//abstract class AAAF : MvpFragment<Presenter<AAAF>>()
//abstract class AAAF2 : MvpFragment<RP>()


//class S<in T> {
////    val t: T? = null
////    fun inf(t: T) = 0
////    fun outf() = t!!
//}

fun test(a: Presenter<out MvpView>) {
    //    val b: MvpView = a.getView2()
    //    val c: Presenter<out MvpFragment<Presenter<AAAF>>> = RP()
    //    a.vi
    //    val a: MvpFragment<Presenter<MvpFragment>>
}

class test2 : test() {
    //   override var a : Int = 0
//        override val item  = 0
    val item
        get() = super.item
    fun test() {
        item
    }
}

open class A(open val a: Int)

class B : A(0) {
    override val a : Int = super.a
}
