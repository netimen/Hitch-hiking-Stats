package ru.netimen.hitch_hikingstats.test

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding.view.RxView
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.button
import org.jetbrains.anko.support.v4.UI
import ru.netimen.hitch_hikingstats.HasId
import ru.netimen.hitch_hikingstats.domain.ErrorInfo
import ru.netimen.hitch_hikingstats.domain.Ride
import rx.Observable
import rx.lang.kotlin.BehaviorSubject
import rx.lang.kotlin.PublishSubject
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   25.02.16
 */

// CUR Ride/Rides - unify naming
interface Input

interface Output

interface Block<I, M, In : Input, Out : Output> {
    fun createIntentProcessor(): (I) -> Observable<M>
    fun intent(input: In): Observable<I>
    fun output(model: M, output: Out)
}

class HasId {
    var id: Int = -1
}

object PipeCache {
    private val id = AtomicInteger()
    private val map = HashMap<Int, IntentPipe<*, *>>()

    fun <I, M> get(hasId: HasId, pipeFactory: (HasId) -> IntentPipe<I, M>): IntentPipe<I, M> {
        if (hasId.id < 0) hasId.id = id.incrementAndGet()

        return map[hasId.id]?.let { it as IntentPipe<I, M> } ?: pipeFactory(hasId).apply { map[hasId.id] = this }
    }

    fun remove(hasId: HasId) {
        map.remove(hasId.id)
    }
}

class IntentPipe<I, M> private constructor(processIntent: (I) -> Observable<M>, private val hasId: HasId) {
    val intent = PublishSubject<I>()
    val model = BehaviorSubject<M>()
    private val subscription = intent.flatMap(processIntent).subscribe(model) // CUR initial intent here

    fun destroy() {
        subscription.unsubscribe()
        PipeCache.remove(hasId)
    }

    companion object {
        fun <I, M> get(processIntent: (I) -> Observable<M>, hasId: HasId) = PipeCache.get(hasId, { IntentPipe(processIntent, it) })
    }
}

class InputOutputPipe<I, M, In : Input, Out : Output, B : Block<I, M, In, Out>>(private val block: B, private val hasId: HasId) {
    private val intentPipe = IntentPipe.get(block.createIntentProcessor(), hasId)

    fun setup(input: In, output: Out, takeUntil: Observable<Unit>) {
        block.intent(input).takeUntil(takeUntil).subscribe(intentPipe.intent)
        intentPipe.model.takeUntil(takeUntil).subscribe { block.output(it, output) } // CUR handle unexpected errors
    }

    fun onBlockDestroyed() = intentPipe.destroy()
}

abstract class BlockFragment<I, M, In : Input, Out : Output, B : Block<I, M, In, Out>, U : AnkoComponent<Fragment>>(private val block: B, private val ui: U) : Fragment() { // CUR remove I, M, force fragment implement In, Out?
    private val hasId = HasId()
    private var stateSaved = false
    private lateinit var pipe: InputOutputPipe<I, M, In, Out, B>

    companion object {
        val ARG_ID = "ARG_MVP_FRAGMENT_VIEW_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.getInt(ARG_ID)?.let { hasId.id = it }
        pipe = InputOutputPipe(block, hasId)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt(ARG_ID, hasId.id)
        stateSaved = true
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) = ui.createView(UI {})

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pipe.setup(this as In, this as Out, RxView.detaches(view!!).map { Unit })
    }

    override fun onDestroyView() {
        if (!stateSaved)
            pipe.onBlockDestroyed()
        super.onDestroyView()
    }
}

interface RidesListInput : Input {
    fun reloadClicked(): Observable<Unit>
    fun rideClicked(): Observable<Int> // CUR may be not INT but Ride here?
    fun rideSwiped(): Observable<Int>
    //    fun scrolledToNextPage(): Observable<Unit>
}

interface RideListOutput : Output {
    fun showProgress()
    fun showData(data: List<Ride>)
    fun showEmpty()
    fun showError(error: ErrorInfo)
    fun showRideDetails(args: RideDetailsArgs)
}

sealed class RidesListIntent {
    class Load : RidesListIntent()
    class ShowRideDetails(val position: Int) : RidesListIntent()
    class DeleteRides(val positions: Array<Int>) : RidesListIntent()
}

sealed class RidesListModel {
    class Loading : RidesListModel()
    class Error(error: ErrorInfo) : RidesListModel()
    class Data(data: List<Ride>) : RidesListModel()
    class ShowingRideDetails(args: RideDetailsArgs) : RidesListModel()
}

class RideListIntentProcessor(val load: Observable<List<Ride>>) : Function1<RidesListIntent, Observable<RidesListModel>> {
    override fun invoke(intent: RidesListIntent) = when (intent) {
        is RidesListIntent.Load -> load.map { RidesListModel.Data(it) as RidesListModel }.startWith(RidesListModel.Loading())
        is RidesListIntent.ShowRideDetails -> TODO()
        is RidesListIntent.DeleteRides -> TODO()
    }
}

class RideListBlock : Block<RidesListIntent, RidesListModel, RidesListInput, RideListOutput> {
    override fun createIntentProcessor(): (RidesListIntent) -> Observable<RidesListModel> = RideListIntentProcessor(load)

    override fun intent(input: RidesListInput): Observable<RidesListIntent> = input.reloadClicked().map { RidesListIntent.Load() as RidesListIntent } // cur more elegant syntax here
//            .mergeWith(input.rideClicked().map { RidesListIntent.ShowRideDetails(it) })
//            .mergeWith(input.rideSwiped().map { RidesListIntent.DeleteRides(arrayOf(it)) })
            .startWith(RidesListIntent.Load()) // CUR separate input2intent and first intent

    override fun output(model: RidesListModel, output: RideListOutput) = when (model) {
        is RidesListModel.Data -> TODO()
        is RidesListModel.Loading -> output.showProgress()
        is RidesListModel.Error -> TODO()
        is RidesListModel.ShowingRideDetails -> TODO()
    }
}

class RideListFragment : BlockFragment<RidesListIntent, RidesListModel, RidesListInput, RideListOutput, RideListBlock, RideListUi>(RideListBlock(), RideListUi()), RidesListInput, RideListOutput {
    override fun reloadClicked(): Observable<Unit> = Observable.empty()

    override fun rideClicked(): Observable<Int> = throw UnsupportedOperationException()

    override fun rideSwiped(): Observable<Int> = throw UnsupportedOperationException()

    override fun showProgress(): Unit = throw UnsupportedOperationException()

    override fun showData(data: List<Ride>): Unit = throw UnsupportedOperationException()

    override fun showEmpty(): Unit = throw UnsupportedOperationException()

    override fun showError(error: ErrorInfo): Unit = throw UnsupportedOperationException()

    override fun showRideDetails(args: RideDetailsArgs): Unit = throw UnsupportedOperationException()
}

class RideListUi : AnkoComponent<Fragment> {
    override fun createView(ui: AnkoContext<Fragment>) = with(ui) {
        button("aaaa")
    }

}

fun showRideDetails(args: RideDetailsArgs) {
} // starts fragment or activity

class RideDetailsArgs

//class Ride


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
