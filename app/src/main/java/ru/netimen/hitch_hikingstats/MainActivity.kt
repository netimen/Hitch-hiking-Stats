package ru.netimen.hitch_hikingstats

import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.trello.rxlifecycle.RxLifecycle
import dagger.Component
import dagger.Module
import dagger.Provides
import org.jetbrains.anko.*
import org.jetbrains.anko.design.navigationView
import org.jetbrains.anko.design.tabLayout
import org.jetbrains.anko.support.v4.*
import ru.netimen.hitch_hikingstats.presentation.Logic
import ru.netimen.hitch_hikingstats.presentation.MvpView
import ru.netimen.hitch_hikingstats.presentation.Presenter
import rx.Observable
import rx.lang.kotlin.BehaviorSubject
import rx.subscriptions.CompositeSubscription
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject


class MainActivity : AppCompatActivity(), AnkoLogger {

    fun <T> compareLists(list1: List<T>, list2: List<T>): Boolean {
        warn { "BBBB ${list1.size} ${list2.size}" }
        if (list1.size != list2.size)
            return false

        val lll = ArrayList(list2)
        for (elem in list1) {
            if (!lll.contains(elem))
                return false
            lll.remove(elem)
        }
        return true
    }

    //CUR authenticate
    //CUR paginated loading
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        val ref = Firebase("https://dazzling-heat-4079.firebaseio.com/")
        //        ref.removeValue()
        //        val ridesRef = ref.child("rides")
        //        val trips = arrayOf("Big Trip", "Middle Trip", "Small Trip")
        //        val cars = arrayOf("Toyota", "Ford", "Ferrari", "Opel", "Lada")
        //        val r = Random()
        //        val rides = ArrayList<Ride>()
        //        val repo: RidesRepo = FirebaseRidesRepo()
        //        val carsRepo: CarsRepo = FirebaseCarsRepo()
        //        async() {
        //            for (i in 1..4)
        //                repo.addOrUpdate(Ride(trips[r.nextInt(trips.size)], cars[r.nextInt(cars.size)], r.nextInt(100), 1 + r.nextInt(100)).apply { rides.add(this) })
        //
        //            (trips + "").forEach { checkRepTrip(repo, rides, it) }
        //            carsRepo.getList(Repo.Query(TripListParams(""))).subscribe({ error { "CARS: $it" } })
        //            carsRepo.getList(Repo.Query(TripListParams(trips[1]))).subscribe({ error { "CARS: $it" } })
        //
        //            Thread.sleep(2000)
        //            val rr = rides.removeAt(0)
        //            repo.remove(rr)
        //            Thread.sleep(2000)
        //
        //            checkRepTrip(repo, rides, "")
        //            carsRepo.getList(Repo.Query(TripListParams(""))).subscribe({ error { "CARS: $it" } })
        //            carsRepo.getList(Repo.Query(TripListParams(trips[1]))).subscribe({ error { "CARS: $it" } })
        //        }
        //            addRide(ref, Ride(trips[r.nextInt(trips.size)], cars[r.nextInt(cars.size)], r.nextInt(100), 1 + r.nextInt(100)).apply { rides.add(this) })
        //
        //        removeRide(ref, rides[0])
        //        changeRide(ref, rides[1], rides[0])
        //        minWait(ref).subscribe { error("AAAAAAmin$it") }
        //        maxWait(ref).subscribe { error("AAAAAAmax$it") }
        //    .addListenerForSingleValueEvent(object:ValueEventListener{
        //        override fun onCancelled(p0: FirebaseError?) {
        //            throw UnsupportedOperationException()
        //        }
        //
        //        override fun onDataChange(p0: DataSnapshot?) {
        //            val rides = (p0?.value as HashMap<String, HashMap<String, Any>>).map { it -> Ride(it.value["trip"] as String, it.value["car"] as String, (it.value["waitMinutes"] as Long).toInt(), (it.value["carMinutes"]as Long).toInt()) }
        //            error("AAAAAAmin${rides[0].waitMinutes}")
        //        }
        //
        //    })
        val millis = System.currentTimeMillis()
        //        error("AAAAAstart$millis")
        //        ridesRef.orderByChild("trip").equalTo(trip1).addValueEventListener(object : ValueEventListener {
        //            override fun onDataChange(p0: DataSnapshot?) {
        //                error("AAAAAddd${System.currentTimeMillis() - l} ${(p0?.value as HashMap<*,*>).size}")
        //                        val rides = (p0?.value as HashMap<String, HashMap<String, Any>>).map { it -> Ride(it.value["trip"] as String, it.value["car"] as String, (it.value["waitMinutes"] as Long).toInt(), (it.value["carMinutes"]as Long).toInt()) }
        //                val carMinutes = rides?.fold(0) { total, next -> total + next.carMinutes }
        //                error("AAAAAeeee${System.currentTimeMillis() - l} $carMinutes")
        //                toast("${p0?.childrenCount} $carMinutes")
        //            }
        //
        //            override fun onCancelled(p0: FirebaseError?) {
        //            }
        //
        //        })
        /**
         */
        //        ridesRef.addChildEventListener(object : ChildEventListener {
        //            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
        //            }
        //
        //            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
        //            }
        //
        //            override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
        //                error("CCC ${System.currentTimeMillis() - millis} ${(p0?.value as HashMap<*, *>).size}")
        //            }
        //
        //            override fun onChildRemoved(p0: DataSnapshot?) {
        //            }
        //
        //            override fun onCancelled(p0: FirebaseError?) {
        //            }
        //
        //        })
        //        ridesRef.addValueEventListener(object : ValueEventListener {
        //            override fun onCancelled(p0: FirebaseError?) {
        //                throw UnsupportedOperationException()
        //            }
        //
        //            override fun onDataChange(p0: DataSnapshot?) {
        //        error("AAAAAddd${System.currentTimeMillis() - millis} ${(p0?.value as HashMap<*,*>).size}")
        //                //                toast(p0?.value.toString())
        //                //                myFirebaseRef.child("rides").push().setValue(Ride(14))
        //            }
        //        })
        val ui = MainActivityUI()
        ui.setContentView(this)
        val tabsTitles = stringArray(R.array.trip_tabs)
        ui.pager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment? = when (position) {
            //                0 -> GoFragment()
                0 -> TestFragment()
            //                0 -> Router.showFragment(TestFragment())
            //                1 -> Router.showFragment(TestFragment())
            //                1 -> RidesFragment()
            //                2 -> CarsFragment()
            //                else ->                    CarsFragment()
                else -> Fragment()
            }


            override fun getCount(): Int = tabsTitles.size
            override fun getPageTitle(position: Int): CharSequence? = tabsTitles[position]
        }
        ui.tabs.setupWithViewPager(ui.pager)
        //        RxTextView.textChanges(ui.car).subscribe { Log.e("aaa", "aaa" + it) }
    }

    //    private fun checkRepTrip(repo: RidesRepo, rides: ArrayList<Ride>, t: String) {
    //        repo.getList(Repo.Query(TripListParams(t)))
    //                .map { it.data!! }
    //                .map { compareLists(it, rides.filter { it.sameTrip(t) }) }
    //                .subscribe { error { "AAAAA check trip: $t $it" } }
    //    }


}

// CUR:  view.showData not called; remove defaultPresenterFactory
// CUR: TODAY: move factories to Component, pass data from intent to logic, logicconfig, how to show new screen from presenter and pass data?
// CUR: modules: lib, lib-android
var callCount = 0

fun longOperation(): Int {
    Log.e("AAAAA", "AAAAA long operation ${++callCount}")
    return callCount
}

class TestLogic @Inject constructor(val dependency: String) : Logic { // CUR unsubscribe
    private val allSubscriptions = CompositeSubscription()

    protected fun <T> addCachedUsecase(useCase: Observable<T>) = BehaviorSubject<T>().apply { allSubscriptions.add(useCase.subscribe(this)) } as Observable<T>

    val someUsecase = addCachedUsecase(Observable.fromCallable(::longOperation).map { "$dependency $it" })
    val dontStartUsecase by lazy { addCachedUsecase(Observable.fromCallable(::longOperation)) }
}

interface TestView : MvpView {
    fun showData(data: String)
}

class TestPresenter(logic: TestLogic, view: TestView) : Presenter<TestLogic, TestView>(logic, view) {
    init {
        logic.someUsecase.subscribe { view.showData(it) }
        logic.dontStartUsecase.subscribe()
    }
}

class TestUI : AnkoComponent<Fragment> {
    lateinit var b: Button

    override fun createView(ui: AnkoContext<Fragment>): View = with(ui) {
        b = button("hello")
        return b
    }
}

class TestFragment : MvpFragment<TestLogic, TestPresenter, TestView, TestUI>(::testLogicFactory, ::defaultPresenterFactory<TestLogic, TestPresenter, TestView>, TestUI()), TestView {

    override fun showData(data: String) = onUiThread { ui.b.run { text = data } }
}

private fun testLogicFactory(context: Context) = DaggerTestComponent.builder().testModule(TestModule()).build().testLogic() // CUR move to TestComponent as static method?

@Module
class TestModule {

    @Provides
    fun provideDependency() = "depAAAAA"
}

@PerScreen
@Component(modules = arrayOf(TestModule::class), dependencies = arrayOf(AppComponent::class))
interface TestComponent {
    fun testLogic(): TestLogic

//    companion object Factory :
}




interface Factories<L : Logic, P : Presenter<in L, in V>, V : MvpView> {
    fun createLogic(): L
    fun createPresenter(logic: L, view: V): P
}

class HasId {
    var id: Int = -1
}

object LogicCache {
    private val id = AtomicInteger()
    private val logicMap = HashMap<Int, Logic>()

    fun <L : Logic> get(hasId: HasId, logicFactory: () -> L): L {
        if (hasId.id < 0) hasId.id = id.incrementAndGet()

        return logicMap[hasId.id]?.let { it as L } ?: logicFactory().apply { logicMap[hasId.id] = this }
    }

    fun remove(hasId: HasId) {
        logicMap.remove(hasId.id)
    }
}

abstract class MvpFragment<L : Logic, P : Presenter<in L, in V>, V : MvpView, U : AnkoComponent<Fragment>>(private val logicFactory: (Context) -> L, private val presenterFactory: (L, V) -> P, protected val ui: U) : Fragment(), MvpView {
    private lateinit var logic: L // CUR move this to some delegate so MvpActivity would be easy to create
    private var presenter: P? = null

    private val hasId = HasId()
    private var stateSaved = false

    companion object {
        val ARG_ID = "ARG_MVP_FRAGMENT_VIEW_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.getInt(ARG_ID)?.let { hasId.id = it }
        logic = LogicCache.get(hasId, { logicFactory(activity) })
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt(ARG_ID, hasId.id)
        stateSaved = true
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) = ui.createView(UI {})

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = presenterFactory(logic, this as V)
    }

    override fun onDestroyView() {
        presenter = null
        if (!stateSaved)
            LogicCache.remove(hasId)
        super.onDestroyView()
    }

    override fun <T> bindToLifecycle() = RxLifecycle.bindView<T>(view as View)
}

//inline fun <reified L : Logic, reified P : Presenter<in L, in V>, reified V : MvpView> defaultPresenterFactory(): (L, V) -> P = { logic, view -> P::class.java.constructors[0].newInstance(logic, view) as P }
inline fun <reified L : Logic, reified P : Presenter<in L, in V>, reified V : MvpView> defaultPresenterFactory(logic: L, view: V) = P::class.java.constructors[0].newInstance(logic, view) as P

//object Router {
//    private val stack = Stack<Logic>()
//    fun get() = stack.last()
//    fun showFragment(fragment: Fragment): Fragment {
//        stack.push(TestLogic())
//        return fragment
//    }
//
//    inline fun <reified T> getLogic(): T {
//        //        return stack.last() as T
//        return get() as T
//    }
//
//}

class TestViewImpl(context: Context) : FrameLayout(context) {

}

class TestUI2 : AnkoComponent<ViewGroup> {

    override fun createView(ui: AnkoContext<ViewGroup>): View = with(ui) {
        button("hahahaha")
    }

}


class MainActivityUI : AnkoComponent<MainActivity> {
    lateinit var pager: ViewPager
    lateinit var tabs: TabLayout
    //    override fun createView(ui: AnkoContext<MainActivity>): View = with(ui) {
    //        drawerLayout {
    //            //            id = R.id.drawer
    //            fitsSystemWindows = true
    //            createAppBar(ui)
    //            createNavigationView(ui)
    //        }
    //    }

    //    fun _DrawerLayout.createAppBar(ui: AnkoContext<MainActivity>) {
    //        coordinatorLayout {
    //            fitsSystemWindows = true
    //
    //            appBarLayout {
    //                toolbar {
    //                    //                    id = R.id.toolbar
    //                    //                    popupTheme = R.style.AppTheme_PopupOverlay
    //                    backgroundResource = R.color.colorPrimary
    //                }.lparams(width = matchParent) {
    //                    val tv = TypedValue()
    //                    if (ui.owner.theme.resolveAttribute(R.attr.actionBarSize, tv, true)) {
    //                        height = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
    //                    }
    //                }
    //            }.lparams(width = matchParent)
    //
    //            relativeLayout {
    //                padding = dimen(R.dimen.margin_big)
    //                textView("Hello World!")
    //            }.lparams(width = matchParent, height = matchParent) {
    //                behavior = AppBarLayout.ScrollingViewBehavior()
    //            }
    //
    //            floatingActionButton {
    //                imageResource = android.R.drawable.ic_dialog_email
    //                backgroundColor = ContextCompat.getColor(ui.owner, R.color.colorAccent)
    //                onClick {
    //                    //                    snackbar("Replace with your own action", Snackbar.LENGTH_LONG) {
    //                    //                        setAction("Action") { ui.toast("Clicked Snack") }
    //                    //                    }
    //                }
    //            }.lparams {
    //                margin = dimen(R.dimen.margin_big)
    //                gravity = Gravity.BOTTOM or GravityCompat.END
    //            }
    //        }.lparams(width = matchParent, height = matchParent)
    //    }

    fun _DrawerLayout.createNavigationView(ui: AnkoContext<MainActivity>) {
        navigationView {
            fitsSystemWindows = true
            menu.add("aaaa")
            menu.add("bbb")
            //            setNavigationItemSelectedListener(ui.owner)
            //            inflateHeaderView(R.layout.nav_header_main)
            //            inflateMenu(R.menu.activity_main_drawer)
        }.lparams(height = matchParent, gravity = GravityCompat.START)
    }

    override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {

        drawerLayout {
            linearLayout {
                orientation = LinearLayout.VERTICAL

                tabs = tabLayout {}
                pager = viewPager {
                    id = 32 // needed when using FragmentPagerAdapter http://stackoverflow.com/a/26028697/190148
                }
            }
            createNavigationView(ui)
        }
    }
}

