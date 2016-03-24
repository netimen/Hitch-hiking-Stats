package ru.netimen.hitch_hikingstats.lib

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import org.jetbrains.anko.progressBar
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.UI
import ru.netimen.hitch_hikingstats.presentation.Logic
import ru.netimen.hitch_hikingstats.presentation.MvpView
import ru.netimen.hitch_hikingstats.presentation.PagingView
import ru.netimen.hitch_hikingstats.presentation.Presenter
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.TypeReference
import uy.kohesive.injekt.api.get
import java.util.*
import kotlin.reflect.KClass

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   03.03.16
 */


fun <T: Any> typeRef(type: KClass<T>) = object : TypeReference<T> {
    override val type = type.java
}

fun <T : Any> injectLazy(type: KClass<T>): Lazy<T> {
    return lazy { Injekt.get(forType = typeRef(type)) }
}

abstract class MvpFragment<L : Logic, P : Presenter<in L, in V>, V : MvpFragment<L, P, V>>(presenterClass: KClass<P>) : Fragment(), MvpView {
    protected val presenter by injectLazy(type = presenterClass)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.toString() // so now presenter actually is created
    }
}

class ViewHolder<V : View>(itemView: V) : RecyclerView.ViewHolder(itemView) {
    val view = super.itemView as V
}

abstract class SimpleListAdapter<T, ItemView : View>(protected val createView: (ViewGroup) -> ItemView, protected val bindView: (ItemView, T) -> Unit) : RecyclerView.Adapter<ViewHolder<ItemView>>() {
    protected val data = ArrayList<T>();

    fun addData(newData: Collection<T>) {
        data.addAll(newData)
        notifyDataSetChanged() // CUR use notifyItemAdded instead
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder<ItemView> = ViewHolder(createView(parent!!))

    override fun onBindViewHolder(viewHolder: ViewHolder<ItemView>?, position: Int): Unit = viewHolder?.run { bindView(view, data[position]) } ?: Unit
}

abstract class ListFragment<T, E, L : Logic, P : Presenter<in L, in V>, V : ListFragment<T, E, L, P, V, ItemView>, ItemView : View>(presenterClass: KClass<P>) : MvpFragment<L, P, V>(presenterClass), PagingView<T, E> {
    lateinit var list: RecyclerView
    lateinit var loader: ProgressBar
    lateinit var container: OneVisibleChildLayout // CUR dataLayout
    protected abstract val adapter: SimpleListAdapter<T, ItemView>

    override fun showLoading() = container.showChild(loader)

    override fun showData(data: List<T>) {
        container.showChild(list)
        adapter.addData(data)
    }

    override fun showNoData() {
        throw UnsupportedOperationException()
    }

    override fun showError(error: E) {
        throw UnsupportedOperationException()
    }

    override fun showLoadingNextPage() {
        throw UnsupportedOperationException()
    }

    override fun showErrorNextPage() {
        throw UnsupportedOperationException()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? = UI {
        this@ListFragment.container = oneVisibleChildLayout {

            list = recyclerView {
                layoutManager = LinearLayoutManager(ctx)
                adapter = this@ListFragment.adapter
            }
            loader = progressBar()
        }
    }.view
}
