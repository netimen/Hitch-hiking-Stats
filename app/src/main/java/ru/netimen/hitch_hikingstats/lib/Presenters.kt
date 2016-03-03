package ru.netimen.hitch_hikingstats.lib

import ru.netimen.hitch_hikingstats.lib.*
import ru.netimen.hitch_hikingstats.onNull
import rx.Subscription
import rx.subscriptions.CompositeSubscription
import java.util.*

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   26.02.16
 */

abstract class Presenter<V : MvpView> { // cur make Presenter die with the view, so get view in the constructor
    private val allSubscriptions = CompositeSubscription()
    private var attachCount = 0
    protected var view: V? = null

    fun isViewAttached() = view != null

    protected fun assertViewAttached() = view.onNull { throw IllegalStateException("A View must be attached to this presenter to access the view property") }

    fun attachView(view: V) {
        this.view = view
        if (attachCount++ == 0)
            onFirstAttach()

        onAttachView()
    }

    fun detachView() {
        view = null
        allSubscriptions.clear()
        onDetachView()
    }

    protected open fun onAttachView() = Unit

    protected open fun onFirstAttach() = Unit

    protected open fun onDetachView() = Unit

    protected fun unsubscribeOnDetach(s: Subscription) = allSubscriptions.add(s)

}

open class PagingPresenter<T, E, V : PagingView<T, E>>(protected val loadUseCase: ResultUseCase<List<T>, E>) : Presenter<V>() {
    protected val objects = ArrayList<T>()

    override fun onFirstAttach() = load()

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
