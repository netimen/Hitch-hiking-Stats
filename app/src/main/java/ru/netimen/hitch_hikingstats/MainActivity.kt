package ru.netimen.hitch_hikingstats

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.firebase.client.DataSnapshot
import com.firebase.client.Firebase
import com.firebase.client.FirebaseError
import com.firebase.client.ValueEventListener
import com.jakewharton.rxbinding.widget.RxTextView
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView
import java.util.*

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
            }
        })
        val ui = MainActivityUI()
        ui.setContentView(this)
        RxTextView.textChanges(ui.car).subscribe { Log.e("aaa", "aaa" + it) }
    }

}

//data class Car(val name: String)
open class Hitch(val trip: String, val waitMinutes: Int)

class Ride(trip: String, val car: String, waitMinutes: Int, val carMinutes: Int) : Hitch(trip, waitMinutes)


class MainActivityUI : AnkoComponent<MainActivity> {
    lateinit var title: TextView
    lateinit var car: EditText
    lateinit var ride: Button
    lateinit var add: View

    override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
        relativeLayout {
            padding = dimen(R.dimen.margin_big)

            title = textView(R.string.idle) {
                id = 1
                textSize = 20f
            }.lparams {
                alignParentTop()
                centerHorizontally()
                bottomMargin = dip(40)
                topMargin = dimen(R.dimen.margin_big)
            }

            val buttonMargin = dimen(R.dimen.margin_small)
            val b = button(R.string.wait) {
                id = 2
            }.lparams {
                margin = buttonMargin
                alignParentRight()
                below(title)
            }
            ride = button(R.string.ride) {
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
            add = floatingActionButton {
                imageResource = R.drawable.ic_add
            }.lparams {
                margin = buttonMargin
                alignParentRight()
                below(ride)
            }
        }
    }
}

fun ViewManager.floatingActionButton(init: FloatingActionButton.() -> Unit = {}) = ankoView({ FloatingActionButton(it) }, init) // https://gist.github.com/cnevinc/539d6659a4afbf6a0b08
