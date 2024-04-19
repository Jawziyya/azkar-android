package io.jawziyya.azkar

import android.app.Application
import com.zhuinden.simplestack.GlobalServices
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.rebind
import io.jawziyya.azkar.data.repository.AzkarCounterRepository
import io.jawziyya.azkar.database.DatabaseHelper
import timber.log.Timber

/**
 * Created by uvays on 05.06.2022.
 */

class App : Application() {

    lateinit var globalServices: GlobalServices

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        val sharedPreferences = getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE)
        val databaseHelper = DatabaseHelper(this, resources, sharedPreferences)

        globalServices =
            GlobalServices
                .builder()
                .rebind<Application>(this)
                .add(resources)
                .add(sharedPreferences)
                .add(databaseHelper)
                .add(AzkarCounterRepository())
                .build()
    }
}