package ru.netimen.hitch_hikingstats

import android.support.annotation.CallSuper
import rx.Observable
import rx.Subscription
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.util.*

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   10.02.16
 */

interface ListParams

data class Result<T, E>(val data: T? = null, val error: E? = null) {

    fun isSuccessful(): Boolean = data != null

    //    companion object Factory {
    //        fun <T, E> success(result: T) = Result(result, null as E)
    //        fun <T, E> error(error: E) = Result(null as T, error)
    //    }
}

private fun <T, E> wrapResultTransformer(errorInfoFactory: (Throwable) -> E): (Observable<T>) -> Observable<Result<T, E>> = { it.map { Result<T, E>(it) }.onErrorReturn { Result<T, E>(error = errorInfoFactory(it)) } }

fun <T, E> Observable<T>.wrapResult(errorInfoFactory: (Throwable) -> E) = compose(wrapResultTransformer<T, E>(errorInfoFactory))

interface Repo<T, E, L : ListParams> {

    class Query<L>(val listParams: L, val page: Int = 0, val perPage: Int = 0, val limit: Int = -1)

    fun getList(query: Query<L>): Observable<Result<List<T>, E>>

    fun get(id: String): Observable<Result<T, E>> // CUR IdRepo

    fun addOrUpdate(t: T)

    fun remove(t: T)
}


// this is ugly. Waiting for type aliases
interface SchedulingStrategy<T> : Observable.Transformer<T, T> {

    companion object Factory {

        fun <T> ioMain() = object : SchedulingStrategy<T> {
            override fun call(p0: Observable<T>?): Observable<T>? = p0?.observeOn(Schedulers.io())?.subscribeOn(Schedulers.io())
        }

        fun <T> justThisThread() = object : SchedulingStrategy<T> {
            override fun call(p0: Observable<T>?): Observable<T>? = p0
        }
    }
}

abstract class UseCase<T>(protected val schedulingStrategy: SchedulingStrategy<T>) {

    fun execute() = useCaseObservable().compose(schedulingStrategy)

    protected abstract fun useCaseObservable(): Observable<T>

}

abstract class ResultUseCase<T, E>(schedulingStrategy: SchedulingStrategy<Result<T, E>>) : UseCase<Result<T, E>> (schedulingStrategy)

abstract class RepoUseCase<T, E, R : Repo<*, E, *>>(protected val repo: R) : ResultUseCase<T, E>(SchedulingStrategy.ioMain())

open class GetUseCase<T, E, R : Repo<T, E, ListParams>>(repo: R, protected val uuid: String) : RepoUseCase<T, E, R>(repo) {

    override fun useCaseObservable(): Observable<Result<T, E>> = repo.get(uuid)
}

abstract class GetListUseCase<T, E, L : ListParams, R : Repo<T, E, L>>(repo: R, protected val listParams: L, protected val perPage: Int) : RepoUseCase<List<T>, E, R>(repo) {
    protected var page: Int = 0

    override fun useCaseObservable(): Observable<Result<List<T>, E>> = repo.getList(Repo.Query(listParams, page, perPage))
}

open class BranchableObservable<T>(protected val observable: Observable<T>) {
    internal val branches = ArrayList<Pair<(T) -> Boolean, (T) -> Unit>>()

    fun subscribe(defaultOnNext: (T) -> Unit, onError: (Throwable) -> Unit) = observable
            .groupBy groupBy@ {
                for ((index, branch)in branches.withIndex())
                    if (branch.first(it))
                        return@groupBy index
                return@groupBy -1
            }
            .subscribe({ it.subscribe(if (it.key == -1) defaultOnNext else branches[it.key].second, onError) }, onError)
}

// these should be extension functions to support chaining and inheritance: http://stackoverflow.com/a/35432682/190148
fun <T, O : BranchableObservable<T>> O.branch(predicate: (T) -> Boolean, onNext: (T) -> Unit) = apply { branches.add(predicate to onNext) }

fun <T, O : BranchableObservable<T>, R> O.branch(predicate: (T) -> Boolean, onNext: (R) -> Unit, map: (T) -> (R)) = branch(predicate, { onNext(map(it)) })


open class ResultObservable<T, E>(observable: Observable<Result<T, E>>) : BranchableObservable<Result<T, E>>(observable) {
    fun subscribe(onUnexpectedError: (Throwable) -> Unit = {}) = subscribe({}, onUnexpectedError)
}

// these should be extension functions to support chaining and inheritance: http://stackoverflow.com/a/35432682/190148
fun <T, E, O : ResultObservable<T, E>> O.onData(onData: (T) -> Unit) = branch({ it.isSuccessful() }, onData, { it.data!! })

fun <T, E, O : ResultObservable<T, E>> O.onError(onError: (E) -> Unit) = branch({ !it.isSuccessful() }, onError, { it.error!! })


open class LoadObservable<T, E>(observable: Observable<Result<T, E>>) : ResultObservable<T, E>(observable)

// these should be extension functions to support chaining and inheritance: http://stackoverflow.com/a/35432682/190148
fun <T, E, O : LoadObservable<T, E>> O.onNoData(noDataPredicate: (T) -> Boolean, onNoData: () -> Unit) = branch({ it.data?.let(noDataPredicate) ?: false }, { onNoData() }, {})

fun <T, E, O : LoadObservable<List<T>, E>> O.onNoData(onNoData: () -> Unit) = this.onNoData({ it.isEmpty() }, onNoData)


interface MVPView

interface DataView<T, E> : MVPView {
    fun showLoading()
    fun showData(data: T)
    fun showNoData()
    fun showError(error: E)
}

interface PagingView<T, E> : DataView<List<T>, E> {
    fun showLoadingNextPage()
    fun showErrorNextPage()
}

abstract class Presenter<V : MVPView> {
    private val allSubscriptions = CompositeSubscription()
    protected var view: V? = null

    fun isViewAttached() = view != null

    fun assertViewAttached() = view.onNull { throw IllegalStateException("A View must be attached to this presenter to access the view property") }

    @CallSuper
    fun onAttachView(view: V): Unit {
        this.view = view
    }

    protected fun unsubscribeOnDetach(s: Subscription) = allSubscriptions.add(s)

    @CallSuper
    fun onDetachView() {
        view = null
        allSubscriptions.clear()
    }
}

open class PagingPresenter<T, E, V : PagingView<T, E>>(protected val loadUseCase: ResultUseCase<List<T>, E>) : Presenter<V>() {
    protected val objects = ArrayList<T>()

    fun load() {
        assertViewAttached()

        view?.showLoading()

        unsubscribeOnDetach(LoadObservable(loadUseCase.execute())
                .onError { view?.showError(it) }
                .onData { view?.showData(objects.apply { addAll(it) }) }
                .onNoData { view?.showNoData() }
                .subscribe()) // CUR show unexpected error
    }
}

class TripListParams(val trip: String) : ListParams

class ErrorInfo

interface HitchRepo<T> : Repo<T, ErrorInfo, TripListParams>

interface IdRepo<T: IdObject> : HitchRepo<T>

interface RidesRepo : IdRepo<Ride>

