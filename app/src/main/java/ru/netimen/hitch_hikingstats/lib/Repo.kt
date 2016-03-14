package ru.netimen.hitch_hikingstats.lib

import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   10.02.16
 */

interface ListParams

sealed class Result<out T, out E> {
    class Success<T, E>(val data: T) : Result<T, E>()
    class Failure<T, E>(val error: E) : Result<T, E>()
}

private fun <T, E> wrapResultTransformer(errorInfoFactory: (Throwable) -> E): (Observable<T>) -> Observable<Result<T, E>> = { it.map { Result.Success<T, E>(it) as Result<T, E> }.onErrorReturn { Result.Failure<T, E>(errorInfoFactory(it)) } }

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
            override fun call(p0: Observable<T>?): Observable<T>? = p0?.observeOn(AndroidSchedulers.mainThread())?.subscribeOn(Schedulers.io())
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

open class GetListUseCase<T, E, L : ListParams, R : Repo<T, E, L>>(repo: R, protected val listParams: L, protected val perPage: Int) : RepoUseCase<List<T>, E, R>(repo) {
    protected var page: Int = 0

    override fun useCaseObservable(): Observable<Result<List<T>, E>> = repo.getList(Repo.Query(listParams, page, perPage))
}

interface ValueRepo<T, E> {

    fun get(): Observable<Result<T, E>>

    fun set(t: T)
}

abstract class ValueRepoUseCase<T, E, R : ValueRepo<*, E>>(protected val repo: R) : ResultUseCase<T, E>(SchedulingStrategy.ioMain())

open class GetValueUseCase<T, E, R : ValueRepo<T, E>>(repo: R) : ValueRepoUseCase<T, E, R>(repo) {

    override fun useCaseObservable(): Observable<Result<T, E>> = repo.get()
}

open class SetValueUseCase<T, E, R : ValueRepo<T, E>>(repo: R) : ValueRepoUseCase<T, E, R>(repo) {

    override fun useCaseObservable(): Observable<Result<T, E>> = repo.get()
}
