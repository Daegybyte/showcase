package com.example.project

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/**
 * The application class for the app. This class is used to initialize the database and the repository.
 * @see application the application context
 * @see [Application]
 * @see [UserDatabase]
 */
class App : Application() {
    // coroutine scope for the app
    val applicationScope = CoroutineScope(SupervisorJob())

    // lazy initialization of the database
    val database by lazy {
        RoomDB.getDatabase(this, applicationScope)
    }

    // lazy initialization of the repository
    val repository by lazy {
        SharedRepository.getInstance(database.UserDao(), database.DBWeatherDao(), applicationScope)
    }

}