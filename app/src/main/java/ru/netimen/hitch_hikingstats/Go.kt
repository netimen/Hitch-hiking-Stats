package ru.netimen.hitch_hikingstats

import android.support.v4.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import com.jakewharton.rxbinding.view.clicks
import dagger.Component
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.onUiThread
import ru.netimen.hitch_hikingstats.domain.*
import ru.netimen.hitch_hikingstats.presentation.Result2
import ru.netimen.hitch_hikingstats.presentation.wrapResult2
import ru.netimen.hitch_hikingstats.test.Block
import ru.netimen.hitch_hikingstats.test.BlockFragment
import ru.netimen.hitch_hikingstats.test.Input
import ru.netimen.hitch_hikingstats.test.Output
import rx.Observable
import rx.Subscription
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   03.03.16
 */

class GetStateUsecase @Inject constructor(val repo: StateRepo) {
    fun execute() = repo.get().wrapResult2()
}

class SetStateUsecase @Inject constructor(val repo: StateRepo) {
    fun execute(state: GoState) = repo.set(state)
}

class AddRideUsecase @Inject constructor(val repo: RidesRepo) {
    fun execute(state: GoState) = when (state) {
        is GoState.Waiting -> repo.addOrUpdate(Ride("", state.lengthMinutes))
        is GoState.Riding -> repo.addOrUpdate(Ride("", state.carName, state.waitMinutes, state.lengthMinutes))
        else -> Unit
    }
}

interface GoInput : Input {
    fun waitClicked(): Observable<Unit>
    fun rideClicked(): Observable<Unit>
    fun stopClicked(): Observable<Unit>
    fun carSelected(): Observable<String>
}

interface GoOutput : Output {
    fun showState(state: GoState)
    fun updateTitle(state: GoState)
}

sealed class GoIntent {
    class Load : GoIntent()
    class Wait : GoIntent()
    class Ride(val carName: String) : GoIntent()
    class Stop : GoIntent()
}

sealed class DataErrorModel<out T, out E> { //TODO waiting for type aliases
    class Data<T, E>(val data: T) : DataErrorModel<T, E>()
    class Error<T, E>(val error: E) : DataErrorModel<T, E>()
}

sealed class GoModel {
    //CUR DataErrorModel
    class Data(val state: GoState) : GoModel()

    class Error(val errorInfo: ErrorInfo) : GoModel()
}

class GoIntentProcessor @Inject constructor(val getUseCase: GetStateUsecase, val setUsecase: SetStateUsecase, val addRideUsecase: AddRideUsecase) : Function1<GoIntent, Observable<GoModel>> {
    private lateinit var state: GoState

    override fun invoke(intent: GoIntent): Observable<GoModel> = when (intent) {
        is GoIntent.Load -> getUseCase.execute().flatMap {
            when (it) { // CUR easier error handling
                is Result2.Success -> changeState(it.data, save = false)
                is Result2.Failure -> Observable.just(GoModel.Error(ErrorInfo(it.error)))
            }
        }
        is GoIntent.Wait -> changeState(GoState.Waiting())
        is GoIntent.Ride -> changeState(GoState.Riding(intent.carName, state.lengthMinutes))
        is GoIntent.Stop -> {
            addRideUsecase.execute(state)
            changeState(GoState.Idle())
        }
    }

    private fun changeState(newState: GoState, save: Boolean = true): Observable<GoModel> {
        state = newState
        if (save)
            setUsecase.execute(state).subscribe()

        return Observable.just(GoModel.Data(state))
    }
}

@PerScreen
@Component(dependencies = arrayOf(AppComponent::class))
interface GoComponent {
    fun intentProcessor(): GoIntentProcessor
}

class GoBlock : Block<GoIntent, GoModel, GoInput, GoOutput> {
    //CUR make output a delegate
    override fun initialIntent() = GoIntent.Load()

    override fun createIntentProcessor(): (GoIntent) -> Observable<GoModel> = DaggerGoComponent.builder().appComponent(AppComponent.instance).build().intentProcessor()

    override fun parseInput(input: GoInput): Observable<GoIntent> = input.waitClicked().map { GoIntent.Wait() as GoIntent }
            .mergeWith(input.rideClicked().map { GoIntent.Ride("Toyo") })
            .mergeWith(input.stopClicked().map { GoIntent.Stop() })

    override fun outputModel(model: GoModel, output: GoOutput, takeUntil: Observable<Unit>) = when (model) {
        is GoModel.Data -> onNewState(model, output, takeUntil)
        is GoModel.Error -> TODO()
    }

    private var titleSubscription: Subscription? = null

    private fun onNewState(model: GoModel.Data, output: GoOutput, takeUntil: Observable<Unit>) {
        output.showState(model.state)

        titleSubscription = titleSubscription?.let { it.unsubscribe(); null }
        if (model.state !is GoState.Idle)
            titleSubscription = Observable.interval(1, TimeUnit.MINUTES).takeUntil(takeUntil).subscribe { output.updateTitle(model.state) } // cur display correct state age
    }
}

class GoFragment : BlockFragment<GoIntent, GoModel, GoInput, GoOutput, GoBlock, GoUI>(GoBlock(), GoUI()), GoInput, GoOutput {

    override fun rideClicked() = ui.ride.clicks()

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
//class GoLogic(private val stateRepo: StateRepo) : Logic {
//    fun loadState() = LoadObservable(stateRepo.get().wrapResult { ErrorInfo(it) })
//    fun saveState(state: GoState) = stateRepo.set(state).subscribe()
//
//    fun addRide(state: GoState) = TODO()
//}
//
//class GoPresenter(logic: GoLogic, view: GoView) : Presenter<GoLogic, GoView>(logic, view) {
//    private var state by Delegates.countedObservable<GoState>(GoState.Idle()) { prop, old, new, setCount ->
//        if (setCount > 0)
//            logic.saveState(new)
//
//        onStateUpdated(new)
//    }
//    private var updateTitleSubscription: Subscription? = null
//
//    init {
//        logic.loadState().onData { state = it }.subscribe()
//
//        view.stopClicked().bindToLifecycle().subscribe {
//            logic.addRide(state)
//            state = GoState.Idle()
//        }
//        view.waitClicked().bindToLifecycle().subscribe { state = GoState.Waiting() }
//        view.rideClicked().bindToLifecycle().subscribe { state = GoState.Riding("Toyota", state.lengthMinutes) }
//    }
//
//    private fun onStateUpdated(newState: GoState) {
//        updateTitleSubscription?.unsubscribe()
//        updateTitleSubscription = Observable.interval(1, TimeUnit.MINUTES).bindToLifecycle().subscribe { view.updateTitle(state) }
//
//        view.showState(newState)
//    }
//
//}
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

//fun gi(): (GoView) -> GoPresenter = { GoPresenter(GoLogic(FirebaseStateRepo(firebaseRef)), it) }

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

