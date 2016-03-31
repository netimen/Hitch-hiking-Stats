package ru.netimen.hitch_hikingstats.test

import android.content.Context
import dagger.Component
import dagger.Module
import dagger.Provides
import ru.netimen.hitch_hikingstats.DaggerTestComponent
import ru.netimen.hitch_hikingstats.TestLogic
import ru.netimen.hitch_hikingstats.presentation.Logic
import javax.inject.Scope
import javax.inject.Singleton

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   25.02.16
 */


//@Module
//class TestModule2(private val context: Context) {
//    @Singleton
//    @Provides
//    fun provideContext2() = context
//
//    @Provides
//    fun provideDependencyInt() = 13
//
//    @Singleton
//    @Provides
//    fun provideDependencyS() = "depAAAAA"
//}
//
//@Singleton
//@Component(modules = arrayOf(TestModule2::class))
//interface TestAppComponent2 {
//    fun depInt(): String
//
//    companion object {
//        private var instance: TestAppComponent2? = null
//        fun get(context: Context) = instance?: DaggerTestAppComponent2.builder().testModule2(TestModule2(context)).build().apply { instance = this }
//    }
//}
//
//interface LogicComponent<L : Logic> {
//    fun logic(): L
//}
//
//@PerScreen
//@Component(dependencies = arrayOf(TestAppComponent2::class))
//interface TestActivityComponent : LogicComponent<TestLogic>
//
////fun daggerLogicFactory() = DaggerTestComponent.builder().testAppComponent2(TestAppComponent2.instance).build().logic()
//
//fun cc(c: Context) = { TestLogic(c.toString()) }
//val cccc = ::cc
//fun test(t: (Context) -> () -> TestLogic) = 1
//fun test2() = test(::cc)
//

//interface Presenter
//class IP : Presenter
//fun test() {
//    val i = IP::class
//    i.java
//    val ccc = IP::class.java
//}


//abstract class MvpFragment<P : Presenter>(presenterClass: KClass<P>) : Fragment() {
//    protected val presenter by injectLazy(type = presenterClass)
//}
//abstract class MvpFragment<P: Presenter>(presenterClass: Class<P>) : Fragment() {
//////    val i: P by injectLazy(type=presenterClass)
//}
//interface MvpView
//
////abstract class BaseView<P> : MvpView {
////    protected abstract val presenter: P
////}
//
////interface EnhancedView<P> : MvpView<P>
//
//abstract class Presenter<V : MvpView> {
//    protected var view: V? = null
//
//    fun attachView(view: V) {
//        this.view = view
//    }
//
//}
//
////abstract class BaseView<P : Presenter<in BaseView<P>>> : MvpView {
////    protected abstract val presenter: P
////
////    constructor() {
////        presenter.attachView(this)
////    }
////}
////
////class AP : Presenter<BV>()
////class BV : BaseView<Presenter<BV>>() {
////    override val presenter = AP()
////}
//
////abstract class AF<P : Presenter<AF<P>>> : Fragment(), MvpView<P> {
//////    abstract override val presenter: P
////    //
////    //        override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
////    //            super.onViewCreated(view, savedInstanceState)
////    //            presenter.attachView(this)
////    //        }
////}
////
////class AP : Presenter<AF<AP>>()
////class AAF : AF<AP>()
//
//interface ViewImpl<V : MvpView> : MvpView
//
//abstract class MvpFragment<P : Presenter<in V>, V : MvpFragment<P, V>> : Fragment(), MvpView {
//    protected abstract val presenter: P
//
//    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        presenter.attachView(this as V)
//    }
//}
//
//interface AV : MvpView {
//    fun showA()
//}
//
//class AP : Presenter<AV>() {
//    fun loadA() = throw UnsupportedOperationException()
//}
//
//class AF : MvpFragment<AP, AF>(), AV {
//    override val presenter: AP
//        get() = throw UnsupportedOperationException()
//
//    override fun showA() {
//        throw UnsupportedOperationException()
//    }
//
//}
////
////class RP : Presenter<AAAF>()
////
////abstract class AAAF : MvpFragment<Presenter<AAAF>>()
////abstract class AAAF2 : MvpFragment<RP>()
//
//
////class S<in T> {
//////    val t: T? = null
//////    fun inf(t: T) = 0
//////    fun outf() = t!!
////}
//
//fun test(a: Presenter<out MvpView>) {
//    //    val b: MvpView = a.getView2()
//    //    val c: Presenter<out MvpFragment<Presenter<AAAF>>> = RP()
//    //    a.vi
//    //    val a: MvpFragment<Presenter<MvpFragment>>
//}
//
//class test2 : test() {
//    //   override var a : Int = 0
////        override val item  = 0
//    fun test() {
//        item
//    }
//}
//
//open class A(open val a: Int)
//
//class B : A(0) {
//    override val a : Int = super.a
//}
