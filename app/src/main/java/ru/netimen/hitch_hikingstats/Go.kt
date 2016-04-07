package ru.netimen.hitch_hikingstats

import android.support.v4.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import dagger.Component
import dagger.Module
import dagger.Provides
import org.jetbrains.anko.*
import ru.netimen.hitch_hikingstats.domain.ErrorInfo
import ru.netimen.hitch_hikingstats.domain.GoState
import ru.netimen.hitch_hikingstats.domain.StateRepo
import ru.netimen.hitch_hikingstats.presentation.*
import ru.netimen.hitch_hikingstats.presentation.GoLogic
import ru.netimen.hitch_hikingstats.presentation.GoPresenter
import ru.netimen.hitch_hikingstats.services.FirebaseStateRepo
import ru.netimen.hitch_hikingstats.services.firebaseRef
import ru.netimen.hitch_hikingstats.test.Block
import ru.netimen.hitch_hikingstats.test.Input
import ru.netimen.hitch_hikingstats.test.Output
import rx.Observable
import rx.Subscription
import uy.kohesive.injekt.api.InjektModule
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.fullType
import uy.kohesive.injekt.api.get
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.properties.Delegates

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   03.03.16
 */

class GetStateUsecase @Inject constructor(val repo: StateRepo) {
    fun execute() = repo.get().wrapResult { ErrorInfo(it) }
}

class SetStateUsecase @Inject constructor(val repo: StateRepo) {
    fun execute(state: GoState) = repo.set(state)
}

interface GoInput : Input {
    fun rideClicked(): Observable<Unit>
    fun stopClicked(): Observable<Unit>
    fun waitClicked(): Observable<Unit>
    fun carSelected(): Observable<String>
}

interface GoOutput : Output {
    fun showState(state: GoState)
    fun updateTitle(state: GoState)
}

sealed class GoIntent {
    class Load : GoIntent()
}

sealed class GoModel {
    class Data(state: GoState) : GoModel()
    class Error(errorInfo: ErrorInfo): GoModel()
}

class GoIntentProcessor @Inject constructor(val getUseCase: GetStateUsecase, val setUsecase: SetStateUsecase) : Function1<GoIntent, Observable<GoModel>> {
    override fun invoke(intent: GoIntent): Observable<GoModel> = when(intent) {
        is GoIntent.Load -> getUseCase.execute().map { when(it) { // CUR easier error handling
            is Result.Success -> GoModel.Data(it.data)
            is Result.Failure -> GoModel.Error(it.error)
        }}
    }
}

@PerScreen
@Component(dependencies = arrayOf(AppComponent::class))
interface GoComponent {
    fun intentProcessor() : GoIntentProcessor
}
//@Module
//class GoModule {
//    @Provides
//    @PerScreen
////    fun provideLogic(repo: StateRepo) = GoLogic(repo)
////}
//
//@Module
//class GoViewModule(private val view: GoView) {
//    @Provides
//    fun providePresenter(logic: GoLogic) = GoPresenter(logic, view)
//}
//
//@PerScreen
//@Component(modules = arrayOf(GoModule::class, GoViewModule::class), dependencies = arrayOf(AppComponent::class))
//interface GoComponent {
//    fun inject(fragment: GoFragment)
//}
class GoBlock : Block<GoIntent, GoModel, GoInput, GoOutput> {
    override fun createIntentProcessor(): (GoIntent) -> Observable<GoModel> = throw UnsupportedOperationException()

    override fun intent(input: GoInput): Observable<GoIntent> {
        throw UnsupportedOperationException()
    }

    override fun output(model: GoModel, output: GoOutput) {
        throw UnsupportedOperationException()
    }
}

class GoLogic(private val stateRepo: StateRepo) : Logic {
    fun loadState() = LoadObservable(stateRepo.get().wrapResult { ErrorInfo(it) }) // CUR usecase?
    fun saveState(state: GoState) = stateRepo.set(state).subscribe()

    fun addRide(state: GoState) = TODO()
}

// cur notification
class GoPresenter(logic: GoLogic, view: GoView) : Presenter<GoLogic, GoView>(logic, view) {
    private var state by Delegates.countedObservable<GoState>(GoState.Idle()) { prop, old, new, setCount ->
        if (setCount > 0)
            logic.saveState(new)

        onStateUpdated(new)
    }
    private var updateTitleSubscription: Subscription? = null

    init {
        logic.loadState().onData { state = it }.subscribe() // cur lifecycle here

        view.stopClicked().bindToLifecycle().subscribe {
            logic.addRide(state)
            state = GoState.Idle()
        }
        view.waitClicked().bindToLifecycle().subscribe { state = GoState.Waiting() }
        view.rideClicked().bindToLifecycle().subscribe { state = GoState.Riding("Toyota", state.lengthMinutes) }//CUR: get car
    }

    private fun onStateUpdated(newState: GoState) {
        updateTitleSubscription?.unsubscribe()
        updateTitleSubscription = Observable.interval(1, TimeUnit.MINUTES).bindToLifecycle().subscribe { view.updateTitle(state) }

        view.showState(newState)
    }

}
//// CUR get current screen. TypeSafe Router.getScreenLogic(GoLogic)
//class GoFragment : MvpFragment<GoPresenter, GoFragment, GoUI>(GoUI()), GoView {
//    @Inject
//    lateinit var presenter: GoPresenter  // CUR move to base class. in MVI view shouldn't know presenter
//
//    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        DaggerGoComponent.builder().appComponent(AppComponentHolder.get(activity).component).goViewModule(GoViewModule(this)).build().inject(this)
//    }
//
//    override fun rideClicked() = ui.ride.clicks() //CUR make one method: getIntentObservable(Intent)
//
//    override fun stopClicked(): Observable<Unit> = ui.stop.clicks()
//
//    override fun waitClicked(): Observable<Unit> = ui.wait.clicks()
//
//    override fun carSelected(): Observable<String> {
//        throw UnsupportedOperationException()
//    }
//
//    override fun showState(state: GoState) = onUiThread {
//        updateTitle(state)
//        ui.wait.visibility = if (state is GoState.Idle) View.VISIBLE else View.GONE
//        ui.stop.visibility = if (ui.wait.visibility == View.VISIBLE) View.GONE else View.VISIBLE
//    }
//
//    override fun updateTitle(state: GoState) = onUiThread {
//        activity.title = getString(ui.getSateCaption(state)) + if (state.lengthMinutes > 0) " ${state.lengthMinutes} " + getString(R.string.min) else "" // CUR never displays 0 min
//    }
//}

//@Module
//class GoModule {
//    @Provides
//    @PerScreen
//    fun provideLogic(repo: StateRepo) = GoLogic(repo)
//}
//
//@Module
//class GoViewModule(private val view: GoView) {
//    @Provides
//    fun providePresenter(logic: GoLogic) = GoPresenter(logic, view)
//}
//
//@PerScreen
//@Component(modules = arrayOf(GoModule::class, GoViewModule::class), dependencies = arrayOf(AppComponent::class))
//interface GoComponent {
//    fun inject(fragment: GoFragment)
//}
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

//class GoInject() : InjektModule {
//    override fun InjektRegistrar.registerInjectables() {
//        //        addSingleton(fullType(), firebaseRef)
//        addSingleton(fullType<StateRepo>(), FirebaseStateRepo(firebaseRef)) // CUR move to separate module
//        addSingleton(fullType(), GoPresenter(GoLogic(get()), get())) // CUR create Logic at another time
//    }
//}

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

