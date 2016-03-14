package ru.netimen.hitch_hikingstats

import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.*
import com.firebase.client.ChildEventListener
import com.firebase.client.DataSnapshot
import com.firebase.client.Firebase
import com.firebase.client.FirebaseError
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.design.navigationView
import org.jetbrains.anko.design.tabLayout
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4._DrawerLayout
import org.jetbrains.anko.support.v4.drawerLayout
import org.jetbrains.anko.support.v4.viewPager
import ru.netimen.hitch_hikingstats.lib.Repo
import java.util.*
import kotlin.reflect.KProperty


class AddFieldDelegate<T, I>(private val defaultValue: I) {
    private val fieldMap = HashMap<T, I>()

    operator fun getValue(t: T, property: KProperty<*>) = fieldMap[t]?.apply { } ?: defaultValue

    operator fun setValue(t: T, property: KProperty<*>, any: I) = fieldMap.put(t, any)
}

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
        Firebase.setAndroidContext(this)
        Firebase.getDefaultConfig().isPersistenceEnabled = true
        val ref = Firebase("https://dazzling-heat-4079.firebaseio.com/")
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
                0 -> GoFragment()
                1 -> RidesFragment()
                2 -> CarsFragment()
                else -> {
                    CarsFragment()
                }
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

