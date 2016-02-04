package ru.netimen.hitch_hikingstats

import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
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
import com.firebase.client.DataSnapshot
import com.firebase.client.Firebase
import com.firebase.client.FirebaseError
import com.firebase.client.ValueEventListener
import org.jetbrains.anko.*
import org.jetbrains.anko.design.floatingActionButton
import org.jetbrains.anko.design.tabLayout
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.viewPager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Firebase.setAndroidContext(this)
        Firebase.getDefaultConfig().isPersistenceEnabled = true
        val ref = Firebase("https://dazzling-heat-4079.firebaseio.com/")
        val ridesRef = ref.child("rides")
        ridesRef.removeValue()
        val trip1 = "Big Trip"
        val trip2 = "Small Trip"
        ridesRef.push().setValue(Ride(trip1, "Toyota", 7, 15))
        ridesRef.push().setValue(Ride(trip1, "Toyota", 8, 21))
        ridesRef.push().setValue(Ride(trip1, "Toyo", 9, 121))
        ridesRef.push().setValue(Ride(trip1, "Ford", 1, 3))
        ridesRef.push().setValue(Ride(trip1, "Ferrari", 2, 8))

        ridesRef.push().setValue(Ride(trip2, "Toyota", 6, 48))
        ridesRef.orderByChild("trip").equalTo(trip1).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot?) {
                val rides = p0?.children?.map { it -> it.getValue(Ride::class.java) }
                val carMinutes = rides?.fold(0) { total, next -> total + next.carMinutes }
                toast("${p0?.childrenCount} $carMinutes")
            }

            override fun onCancelled(p0: FirebaseError?) {
            }

        })
        /**
         * CUR sort cars by rides
         * CUR total ride time
         * CUR total wait time
         * CUR max/min wait
         * CUR trips
         * CUR Ride without car
         */
        ridesRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: FirebaseError?) {
                throw UnsupportedOperationException()
            }

            override fun onDataChange(p0: DataSnapshot?) {
                //                toast(p0?.value.toString())
                //                myFirebaseRef.child("rides").push().setValue(Ride(14))
            }
        })
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
            padding = dimen(R.dimen.margin_big)

            val rideStates = stringArray(R.array.hitch_states)
            title = textView(rideStates[0]) {
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
            }.lparams {
                margin = buttonMargin
                width = dip(120)
                sameBottom(ride)
                leftOf(ride)
            }
        }
    }
}

class MainActivityUI : AnkoComponent<MainActivity> {
    lateinit var pager: ViewPager
    lateinit var tabs: TabLayout

    override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
        linearLayout {
            orientation = LinearLayout.VERTICAL

            tabs = tabLayout {}
            pager = viewPager {
                id = 32 // needed when using FragmentPagerAdapter http://stackoverflow.com/a/26028697/190148
            }
        }
    }
}

//fun ViewManager.floatingActionButton(init: FloatingActionButton.() -> Unit = {}) = ankoView({ FloatingActionButton(it) }, init) // https://gist.github.com/cnevinc/539d6659a4afbf6a0b08

inline fun View.stringArray(resource: Int): Array<out String> = context.stringArray(resource)
fun Context.stringArray(resource: Int): Array<out String> = resources.getStringArray(resource)
