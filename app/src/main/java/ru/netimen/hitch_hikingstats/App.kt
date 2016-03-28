package ru.netimen.hitch_hikingstats

import android.app.Application
import com.firebase.client.Firebase
import dagger.Component
import dagger.Module
import dagger.Provides
import ru.netimen.hitch_hikingstats.services.firebaseRef
import javax.inject.Singleton

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   28.03.16
 */
class App : Application()

@Module
class MainModule(private val app: App) {
    @Provides
    @Singleton
    fun provideApp() = app

    @Provides
    @Singleton
    fun provideFirebase(): Firebase {
        Firebase.setAndroidContext(app)
        Firebase.getDefaultConfig().isPersistenceEnabled = true
        return firebaseRef
    }

}

@Singleton
@Component(modules = arrayOf(MainModule::class))
interface MainComponent
