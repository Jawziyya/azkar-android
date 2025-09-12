package io.jawziyya.azkar

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import io.jawziyya.azkar.data.datasource.ReminderDataSource
import io.jawziyya.azkar.data.helper.ReminderHelper
import io.jawziyya.azkar.data.repository.AzkarCounterRepository
import io.jawziyya.azkar.data.repository.FileRepository
import io.jawziyya.azkar.data.repository.MoonPhaseRepository
import io.jawziyya.azkar.database.DatabaseHelper
import io.jawziyya.azkar.ui.azkarlist.AzkarListViewModel
import io.jawziyya.azkar.ui.azkarpager.AzkarPagerViewModel
import io.jawziyya.azkar.ui.core.SimpleMediaPlayer
import io.jawziyya.azkar.ui.hadith.HadithViewModel
import io.jawziyya.azkar.ui.main.MainViewModel
import io.jawziyya.azkar.ui.settings.SettingsDetailViewModel
import io.jawziyya.azkar.ui.settings.SettingsViewModel
import io.jawziyya.azkar.ui.settings.reminder.ReminderSettingsViewModel
import kotlinx.coroutines.GlobalScope
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber

/**
 * Created by uvays on 05.06.2022.
 */

class App : Application() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app")

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        val appModule = module {
            single<Application> { this@App }
            single<Resources> { resources }
            single<SharedPreferences> {
                getSharedPreferences(
                    BuildConfig.APPLICATION_ID,
                    MODE_PRIVATE,
                )
            }
            single<DataStore<Preferences>> { dataStore }
            single<DatabaseHelper> {
                DatabaseHelper(
                    context = get(),
                    resources = get(),
                    sharedPreferences = get(),
                )
            }
            single<AzkarCounterRepository> { AzkarCounterRepository() }
            single<MoonPhaseRepository> {
                MoonPhaseRepository(
                    coroutineScope = GlobalScope,
                )
            }
            single<ReminderDataSource> {
                ReminderDataSource(
                    sharedPreferences = get(),
                )
            }
            single<AlarmManager> { getSystemService(AlarmManager::class.java) }
            single<ReminderHelper>(
                createdAtStart = true,
            ) {
                ReminderHelper(
                    application = get(),
                    coroutineScope = GlobalScope,
                    alarmManager = get(),
                    reminderDataSource = get(),
                    sharedPreferences = get(),
                )
            }
            viewModel {
                MainViewModel(
                    databaseHelper = get(),
                    moonPhaseRepository = get(),
                )
            }
            viewModel { params ->
                AzkarListViewModel(
                    azkarCategory = params.get(),
                    databaseHelper = get(),
                )
            }
            viewModel { params ->
                AzkarPagerViewModel(
                    azkarCategory = params.get(),
                    azkarIndex = params.get(),
                    application = get(),
                    databaseHelper = get(),
                    fileRepository = FileRepository(application = get()),
                    simpleMediaPlayer = SimpleMediaPlayer(application = get()),
                    sharedPreferences = get(),
                    azkarCounterRepository = get(),
                )
            }
            viewModel { params ->
                HadithViewModel(
                    hadithId = params.get(),
                    databaseHelper = get(),
                )
            }
            viewModel {
                SettingsViewModel(
                    resources = get(),
                    application = get(),
                    sharedPreferences = get(),
                )
            }
            viewModel {
                SettingsDetailViewModel(
                    sharedPreferencesKey = get(),
                    titles = get(),
                    values = get(),
                    defaultValueIndex = get(),
                    sharedPreferences = get(),
                )
            }
            viewModel {
                ReminderSettingsViewModel(
                    alarmManager = get(),
                    reminderDataSource = get(),
                )
            }
        }

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appModule)
        }
    }
}