package ru.netimen.hitch_hikingstats

import rx.Observable
import rx.schedulers.Schedulers
import java.util.*

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   10.02.16
 */

interface ListParams

data class Result<T, E>(val result: T, val error: E?) { // CUR result->data?

    fun isSuccessfull(): Boolean = error == null

    companion object Factory {
        fun <T> success(result: T) = Result(result, null)
        fun <E> error(error: E) = Result(0, error)
    }
}

interface Repo<T, E, L : ListParams> {

    class Query<L>(val listParams: L, val page: Int = 0, val perPage: Int = 0, val limit: Int = -1)

    fun getMany(query: Query<L>): Observable<Result<List<T>, E>>//CUR getList?

    fun get(id: String): Observable<Result<T, E>>

    fun addOrUpdate(t: T)
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

class GetUseCase<T, E, R : Repo<T, E, ListParams>>(repo: R, protected val uuid: String) : RepoUseCase<T, E, R>(repo) {

    override fun useCaseObservable(): Observable<Result<T, E>> = repo.get(uuid)
}

abstract class GetManyUseCase<T, E, L : ListParams, R : Repo<T, E, L>>(repo: R, protected val listParams: L, protected val perPage: Int) : RepoUseCase<List<T>, E, R>(repo) {
    protected var page: Int = 0;

    override fun useCaseObservable(): Observable<Result<List<T>, E>> = repo.getMany(Repo.Query(listParams, page, perPage))
}

open class BranchableObservable<T>(protected val observable: Observable<T>) {
    protected var branches = ArrayList<Pair<(T) -> Boolean, (T) -> Unit>>()

    fun branch(predicate: (T) -> Boolean, onNext: (T) -> Unit) {
        branches.add(predicate to onNext)
    }

    fun <R> branch(predicate: (T) -> Boolean, onNext: (R) -> Unit, map: (T) -> (R)) = branch(predicate, { onNext(map(it)) })

    fun subscribe(defaultOnNext: (T) -> Unit, onError: (Throwable) -> Unit) = observable
            .groupBy groupBy@ {
                for ((index, branch)in branches.withIndex())
                    if (branch.first(it))
                        return@groupBy index
                return@groupBy -1
            }
            .subscribe({ it.subscribe(if (it.key == -1) defaultOnNext else branches[it.key].second, onError) }, onError)
}

open class ResultObservable<T, E>(observable: Observable<Result<T, E>>) : BranchableObservable<Result<T, E>>(observable) {
    fun onData(onData: (T) -> Unit) = branch({ it.isSuccessfull() }, onData, { it.result })

    fun onError(onError: (E) -> Unit) = branch({ !it.isSuccessfull() }, onError, { it.error!! })

    fun subscribe(onUnexpectedError:(Throwable)->Unit)=subscribe({}, onUnexpectedError)
}

class LoadObservable<T, E>(observable: Observable<Result<T, E>>):ResultObservable<T, E>(observable) {
    fun onNoData(noDataPredicate:(T)->Boolean, onNoData: (Unit)->Unit) = branch({noDataPredicate(it.result)}, onNoData, {})
}
