package ru.netimen.hitch_hikingstats.presentation

import rx.Observable
import java.util.*

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   26.02.16
 */

abstract class Presenter<V : MvpView>(protected val view: V) {
    fun Observable<*>.bindToLifecycle() = compose(view.bindToLifecycle())
}

open class PagingPresenter<T, E, V : PagingView<T, E>>(view: V, protected val loadUseCase: ResultUseCase<List<T>, E>) : Presenter<V>(view) {
    protected val objects = ArrayList<T>() // CUR store this in ViewModel

    fun load() {

        view.showLoading()

        LoadObservable(loadUseCase.execute())
                .onError { view.showError(it) }
                .onData { view.showData(objects.apply { addAll(it) }) }
                .onNoData { view.showNoData() }
                .subscribe() // CUR show unexpected error
    }
}
