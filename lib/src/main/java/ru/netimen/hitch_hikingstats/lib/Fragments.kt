package ru.netimen.hitch_hikingstats.lib

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.support.v4.UI
import ru.netimen.hitch_hikingstats.presentation.MvpView
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


fun <T : Any> typeRef(type: KClass<T>) = object : TypeReference<T> {
    override val type = type.java
}

fun <T : Any> injectLazy(type: KClass<T>): Lazy<T> {
    return lazy { Injekt.get(forType = typeRef(type)) }
}

//abstract class MvpFragment<L : Logic, P : Presenter<in L, in V>, V : MvpFragment<L, P, V>>(val presenterClass: KClass<P>, val presenterModule: InjektModule) : Fragment(), MvpView {
//abstract class MvpFragment<P : Presenter<*, in V>, V : MvpFragment<P,V,U>, U : AnkoComponent<Fragment>>(private val createPresenter:(V)->P,protected val ui: U, val presenterClass: KClass<V>, val presenterModule: InjektModule) : Fragment(), MvpView {
abstract class MvpFragment<P : Presenter<*, in V>, V : MvpFragment<P, V, U>, U : AnkoComponent<Fragment>>(private val createPresenter: (V) -> P, protected val ui: U) : Fragment(), MvpView {
    //        protected val presenter by injectLazy(type = presenterClass)
    //    val injectScope = InjektScope(DefaultRegistrar())
    //    lateinit private var injectModule: InjektModule
    private val presenter by lazy { createPresenter(this as V) }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) = ui.createView(UI {})

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.toString() // creates the presenter actually
        //        injectModule = object : InjektScopedMain(InjektScope(DefaultRegistrar())) {
        //            override fun InjektRegistrar.registerInjectables() {
        //                addSingleton(typeRef(presenterClass), this@MvpFragment as V)
        //                importModule(presenterModule)
        //            }
        //
        //        }
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

//abstract class ListFragment<T, E, L : Logic, P : Presenter<in L, in V>, V : ListFragment<T, E, L, P, V, ItemView>, ItemView : View>(presenterClass: KClass<P>) : MvpFragment<L, P, V>(presenterClass, object:InjektMain(){
//    override fun InjektRegistrar.registerInjectables() {
//        throw UnsupportedOperationException()
//    }
//
//}), PagingView<T, E> {
//    lateinit var list: RecyclerView
//    lateinit var loader: ProgressBar
//    lateinit var container: OneVisibleChildLayout // CUR dataLayout
//    protected abstract val adapter: SimpleListAdapter<T, ItemView>
//
//    override fun showLoading() = container.showChild(loader)
//
//    override fun showData(data: List<T>) {
//        container.showChild(list)
//        adapter.addData(data)
//    }
//
//    override fun showNoData() {
//        throw UnsupportedOperationException()
//    }
//
//    override fun showError(error: E) {
//        throw UnsupportedOperationException()
//    }
//
//    override fun showLoadingNextPage() {
//        throw UnsupportedOperationException()
//    }
//
//    override fun showErrorNextPage() {
//        throw UnsupportedOperationException()
//    }
//
//    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? = UI {
//        this@ListFragment.container = oneVisibleChildLayout {
//
//            list = recyclerView {
//                layoutManager = LinearLayoutManager(ctx)
//                adapter = this@ListFragment.adapter
//            }
//            loader = progressBar()
//        }
//    }.view
//}
