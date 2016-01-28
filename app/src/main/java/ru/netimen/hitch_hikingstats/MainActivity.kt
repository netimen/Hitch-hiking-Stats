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

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Firebase.setAndroidContext(this)
        Firebase.getDefaultConfig().isPersistenceEnabled = true
        val myFirebaseRef = Firebase("https://dazzling-heat-4079.firebaseio.com/")
        myFirebaseRef.child("rides").push().setValue(Ride(13))
        myFirebaseRef.child("rides").addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: FirebaseError?) {
                throw UnsupportedOperationException()
            }

            override fun onDataChange(p0: DataSnapshot?) {
                toast(p0?.value.toString())
                myFirebaseRef.child("rides").push().setValue(Ride(14))
            }
        })
        val ui = MainActivityUI()
        ui.setContentView(this)
        RxTextView.textChanges(ui.car).subscribe { Log.e("aaa", "aaa" + it) }
    }

}

data class Ride(val minutes: Int)


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
