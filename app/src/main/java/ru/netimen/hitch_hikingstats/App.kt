package ru.netimen.hitch_hikingstats

import android.app.Application
import android.content.Context
import com.firebase.client.Firebase
import dagger.Component
import dagger.Module
import dagger.Provides
import ru.netimen.hitch_hikingstats.domain.StateRepo
import ru.netimen.hitch_hikingstats.services.FirebaseStateRepo
import ru.netimen.hitch_hikingstats.services.firebaseRef
import javax.inject.Scope
import javax.inject.Singleton

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   28.03.16
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class PerScreen

@Module
class ContextModule(private val context: Context) {

    @Provides
    @Singleton
    fun provideContext() = context
}

interface ComponentHolder<C> { // CUR move to lib
    val component: C
}

interface AppComponentHolder : ComponentHolder<AppComponent> {
    companion object {
        fun get(context: Context) = context.applicationContext as AppComponentHolder
    }
}

class App : Application(), AppComponentHolder {
    override val component by lazy { DaggerProductionAppComponent.builder().contextModule(ContextModule(this)).firebaseReposModule(FirebaseReposModule()).build() }
}

@Module
class FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebase(context: Context): Firebase {
        Firebase.setAndroidContext(context)
        Firebase.getDefaultConfig().isPersistenceEnabled = true
        return firebaseRef
    }
}

@Module(includes = arrayOf(FirebaseModule::class))
class FirebaseReposModule {

    @Provides
    @Singleton
    fun provideStateRepo(firebase: Firebase): StateRepo = FirebaseStateRepo(firebase)
}

interface AppComponent {
    fun stateRepo(): StateRepo
}

@Singleton
@Component(modules = arrayOf(ContextModule::class, FirebaseReposModule::class))
interface ProductionAppComponent : AppComponent {
}
