package ru.netimen.hitch_hikingstats.lib

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   26.02.16
 */

interface MvpView

interface DataView<T, E> : MvpView {
    fun showLoading()
    fun showData(data: T)
    fun showNoData()
    fun showError(error: E)
}

interface PagingView<T, E> : DataView<List<T>, E> {
    fun showLoadingNextPage()
    fun showErrorNextPage()
}

