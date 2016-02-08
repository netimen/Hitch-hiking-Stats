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
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.firebase.client.*
import org.jetbrains.anko.*
import org.jetbrains.anko.design.floatingActionButton
import org.jetbrains.anko.design.navigationView
import org.jetbrains.anko.design.tabLayout
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4._DrawerLayout
import org.jetbrains.anko.support.v4.drawerLayout
import org.jetbrains.anko.support.v4.viewPager
import java.util.*

class MainActivity : AppCompatActivity(), AnkoLogger {

    fun Firebase.runTransaction(transformValue: MutableData.() -> Unit) = runTransaction(object : Transaction.Handler {
        override fun onComplete(p0: FirebaseError?, p1: Boolean, p2: DataSnapshot?) {
        }

        override fun doTransaction(p0: MutableData?): Transaction.Result? = Transaction.success(p0?.apply(transformValue))

    })

    fun MutableData.add(v: Int) {
        value = v + initialValue()
    }

    private fun MutableData.initialValue() = if (value == null) 0 else value as Long
    fun Hitch.addExtraData(ref: Firebase) = addHitchExtraData(ref, this)
    fun Ride.addExtraData(ref: Firebase) = addHitchExtraData(ref, this)

    fun addRide(ref: Firebase, ride: Hitch) {
        ref.child("rides").push().setValue(ride)
        ride.addExtraData(ref)
//        addHitchExtraData(ref, ride)
        addHitchExtraData(ref.child("trips").child(ride.trip), ride)
    }

    /**
     * CUR Ride without car
     */
    private fun addHitchExtraData(ref: Firebase, hitch: Hitch) {
        ref.child("waitMinutes").runTransaction { add(hitch.waitMinutes) }
        ref.child("minWait").runTransaction { value = initialValue().run { if (this > hitch.waitMinutes) hitch.waitMinutes else this } }
        ref.child("maxWait").runTransaction { value = initialValue().run { if (this < hitch.waitMinutes) hitch.waitMinutes else this } }
    }

    private fun addHitchExtraData(ref: Firebase, ride: Ride) {
        addHitchExtraData(ref, ride)
        ref.child("carMinutes").runTransaction { add(ride.carMinutes) }
        ref.child("cars").child(ride.car).runTransaction { add(1) } // CUR carTime
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Firebase.setAndroidContext(this)
        Firebase.getDefaultConfig().isPersistenceEnabled = true
        val ref = Firebase("https://dazzling-heat-4079.firebaseio.com/")
        ref.removeValue()
        val ridesRef = ref.child("rides")
        val trip1 = "Big Trip"
        val trip2 = "Small Trip"
        addRide(ref, Ride(trip1, "Toyota", 7, 15))
        addRide(ref, Ride(trip1, "Toyota", 8, 21))
        addRide(ref, Ride(trip1, "Toyo", 9, 121))
        addRide(ref, Ride(trip1, "Ford", 1, 3))
        addRide(ref, Ride(trip1, "Ferrari", 2, 8))
        //
        addRide(ref, Ride(trip2, "Toyota", 6, 48))
        val millis = System.currentTimeMillis()
        //        error("AAAAAstart$millis")
        //        ridesRef.orderByChild("trip").equalTo(trip1).addValueEventListener(object : ValueEventListener {
        //            override fun onDataChange(p0: DataSnapshot?) {
        //                error("AAAAAddd${System.currentTimeMillis() - l} ${(p0?.value as HashMap<*,*>).size}")
        //                val rides = (p0?.value as HashMap<String, HashMap<String, Any>>).map { it -> Ride(it.value["trip"] as String, it.value["car"] as String, (it.value["waitMinutes"] as Long).toInt(), (it.value["carMinutes"]as Long).toInt()) }
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
        ridesRef.addChildEventListener(object : ChildEventListener {
            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
            }

            override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                error("AAAAAddd${System.currentTimeMillis() - millis} ${(p0?.value as HashMap<*, *>).size}")
            }

            override fun onChildRemoved(p0: DataSnapshot?) {
            }

            override fun onCancelled(p0: FirebaseError?) {
            }

        })
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
            override fun getItem(position: Int): Fragment? = if (position == 0) GoFragment() else ListFragment()


            override fun getCount(): Int = tabsTitles.size
            override fun getPageTitle(position: Int): CharSequence? = tabsTitles[position]
        }
        ui.tabs.setupWithViewPager(ui.pager)
        //        RxTextView.textChanges(ui.car).subscribe { Log.e("aaa", "aaa" + it) }
    }

}

//data class Car(val name: String)
open class Hitch(val trip: String, val waitMinutes: Int)

class Ride(trip: String, val car: String, waitMinutes: Int, val carMinutes: Int) : Hitch(trip, waitMinutes)


class GoFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? = GoFragmentUI().createView(UI {})
}

class ListFragment : Fragment() {
    lateinit var list: RecyclerView
    lateinit var add: View

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? = UI {
        frameLayout {

            recyclerView {
                list = this
                layoutManager = LinearLayoutManager(ctx)
                adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                    override fun getItemCount(): Int = 100

                    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? = object : RecyclerView.ViewHolder(TextView(ctx).apply { text = "ccc" }) {}

                    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int): Unit {
                    }
                }
            }
            add = floatingActionButton {
                imageResource = R.drawable.ic_add
            }.lparams {
                gravity = Gravity.RIGHT or Gravity.BOTTOM;
                margin = dimen(R.dimen.margin_big)
            }
        }
    }.view
}

class GoFragmentUI : AnkoComponent<Fragment> {
    lateinit var title: TextView
    lateinit var car: EditText
    lateinit var ride: Button

    override fun createView(ui: AnkoContext<Fragment>) = with(ui) {

        relativeLayout {
            fitsSystemWindows = true // CUR on small screens run button is invisible
            padding = dimen(R.dimen.margin_big)

            val rideStates = stringArray(R.array.hitch_states)
            title = textView(rideStates[0]) { // CUR display state in toolbar instead
                id = 1
                textSize = 20f
            }.lparams {
                alignParentTop()
                centerHorizontally()
                bottomMargin = dip(40)
                topMargin = dimen(R.dimen.margin_big)
            }

            val buttonMargin = dimen(R.dimen.margin_small)
            val b = button(rideStates[1]) {
                id = 2
            }.lparams {
                margin = buttonMargin
                alignParentRight()
                below(title)
            }
            ride = button(rideStates[2]) {
                id = 3
            }.lparams {
                margin = buttonMargin
                alignParentRight()
                below(b)
            }
            car = editText {
                hintResource = R.string.toyota
            }.lparams(width = dip(120)) {
                margin = buttonMargin
                sameBottom(ride)
                leftOf(ride)
            }
        }
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
    //                        height = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics);
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

//fun ViewManager.floatingActionButton(init: FloatingActionButton.() -> Unit = {}) = ankoView({ FloatingActionButton(it) }, init) // https://gist.github.com/cnevinc/539d6659a4afbf6a0b08

inline fun View.stringArray(resource: Int): Array<out String> = context.stringArray(resource)
fun Context.stringArray(resource: Int): Array<out String> = resources.getStringArray(resource)
