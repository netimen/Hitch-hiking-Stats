package ru.netimen.hitch_hikingstats.lib

import rx.Observable
import rx.subjects.PublishSubject
import java.util.*

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   10.03.16
 */

open class BranchableObservable<T>(protected val observable: Observable<T>) {
    internal val branches = ArrayList<Pair<(T) -> Boolean, (T) -> Unit>>()

    fun subscribe(defaultOnNext: (T) -> Unit, onError: (Throwable) -> Unit) = observable
            .groupBy {
                for ((index, branch) in branches.withIndex())
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


