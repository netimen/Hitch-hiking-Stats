package ru.netimen.hitch_hikingstats

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import com.firebase.client.Firebase
import com.jakewharton.rxbinding.view.clicks
import dagger.Component
import dagger.Module
import dagger.Provides
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.onUiThread
import ru.netimen.hitch_hikingstats.domain.GoState
import ru.netimen.hitch_hikingstats.domain.StateRepo
import ru.netimen.hitch_hikingstats.lib.MvpFragment
import ru.netimen.hitch_hikingstats.presentation.GoLogic
import ru.netimen.hitch_hikingstats.presentation.GoPresenter
import ru.netimen.hitch_hikingstats.presentation.GoView
import ru.netimen.hitch_hikingstats.services.FirebaseStateRepo
import ru.netimen.hitch_hikingstats.services.firebaseRef
import rx.Observable
import uy.kohesive.injekt.api.InjektModule
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.fullType
import uy.kohesive.injekt.api.get
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   03.03.16
 */

// CUR get current screen. TypeSafe Router.getScreenLogic(GoLogic)
class GoFragment : MvpFragment<GoPresenter, GoFragment, GoUI>(GoUI()), GoView {
    @Inject
    lateinit var presenter: GoPresenter  // CUR move to base class. in MVI view shouldn't know presenter

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DaggerGoComponent.builder().mainModule(MainModule(activity.applicationContext as App)).goViewModule(GoViewModule(this)).build().inject(this)
    }

    override fun rideClicked() = ui.ride.clicks() //CUR make one method: getIntentObservable(Intent)

    override fun stopClicked(): Observable<Unit> = ui.stop.clicks()

    override fun waitClicked(): Observable<Unit> = ui.wait.clicks()

    override fun carSelected(): Observable<String> {
        throw UnsupportedOperationException()
    }

    override fun showState(state: GoState) = onUiThread {
        updateTitle(state)
        ui.wait.visibility = if (state is GoState.Idle) View.VISIBLE else View.GONE
        ui.stop.visibility = if (ui.wait.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    override fun updateTitle(state: GoState) = onUiThread {
        activity.title = getString(ui.getSateCaption(state)) + if (state.lengthMinutes > 0) " ${state.lengthMinutes} " + getString(R.string.min) else "" // CUR never displays 0 min
    }
}

@Module
class ReposModule {
    @Provides
    @Singleton
    fun provideRepo(firebase: Firebase): StateRepo = FirebaseStateRepo(firebase)
}

@Module
class GoModule {
    @Provides
    @Singleton
    fun provideLogic(repo: StateRepo) = GoLogic(repo)
}

@Module
class GoViewModule(private val view: GoView) {
    @Provides
    fun providePresenter(logic: GoLogic) = GoPresenter(logic, view)
}

@Singleton
@Component(modules = arrayOf(MainModule::class, ReposModule::class, GoModule::class, GoViewModule::class))
interface GoComponent {
    fun inject(fragment: GoFragment)
}
//ServicesModule {
//    context
//}
//ReposModule {
//
//}

//abstract class PresenterDelegate {
//
//}
//interface Screen<L: Logic, P : Presenter<L, V> , V: MvpView> {
//    fun getLogic() : L
//    fun getPresenter() : P
//}
//
//class GoScreen: Screen<GoLogic, GoPresenter, GoView> {
//    override fun getLogic(): GoLogic {
//        throw UnsupportedOperationException()
//    }
//
//    override fun getPresenter(): GoPresenter {
//        throw UnsupportedOperationException()
//    }
//}

class GoInject() : InjektModule {
    override fun InjektRegistrar.registerInjectables() {
        //        addSingleton(fullType(), firebaseRef)
        addSingleton(fullType<StateRepo>(), FirebaseStateRepo(firebaseRef)) // CUR move to separate module
        addSingleton(fullType(), GoPresenter(GoLogic(get()), get())) // CUR create Logic at another time
    }
}

fun gi(): (GoView) -> GoPresenter = { GoPresenter(GoLogic(FirebaseStateRepo(firebaseRef)), it) }

class GoUI : AnkoComponent<Fragment> {
    lateinit var car: EditText
    lateinit var ride: Button
    lateinit var wait: Button
    lateinit var stop: Button // CUR: discard button

    fun getSateCaption(state: GoState) = when (state) {
        is GoState.Idle -> R.string.idle
        is GoState.Waiting -> R.string.waiting
        is GoState.Riding -> R.string.riding
    }

    override fun createView(ui: AnkoContext<Fragment>) = with(ui) {

        relativeLayout {
            fitsSystemWindows = true
            padding = dimen(R.dimen.margin_big)

            val buttonMargin = dimen(R.dimen.margin_small)
            ride = button(R.string.ride) {
                id = 3
            }.lparams {
                margin = buttonMargin
                alignParentRight()
                centerVertically()
            }
            //            car = editText {
            //                hintResource = R.string.toyota
            //            }.lparams(width = dip(120)) {
            //                margin = buttonMargin
            //                sameBottom(ride)
            //                leftOf(ride)
            //            }
            val waitStopLparams: RelativeLayout.LayoutParams.() -> Unit = {
                margin = buttonMargin
                alignParentRight()
                above(ride)
            }
            wait = button(R.string.wait) {
            }.lparams(init = waitStopLparams)
            stop = button(R.string.stop) {
                visibility = View.GONE
            }.lparams(init = waitStopLparams)
        }
    }

}

