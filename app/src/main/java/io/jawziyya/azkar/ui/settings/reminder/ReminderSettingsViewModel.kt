package io.jawziyya.azkar.ui.settings.reminder

import android.app.AlarmManager
import android.os.Build
import androidx.lifecycle.ViewModel
import io.jawziyya.azkar.data.datasource.ReminderDataSource
import io.jawziyya.azkar.ui.core.Settings
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import java.time.LocalTime

class ReminderSettingsViewModel(
    private val alarmManager: AlarmManager,
    private val reminderDataSource: ReminderDataSource,
) : ViewModel() {

    val enabledFlow: Flow<Boolean> = reminderDataSource.enabledFlow
        .map { value ->
            when {
                Build.VERSION.SDK_INT < Build.VERSION_CODES.S -> value
                alarmManager.canScheduleExactAlarms() -> value
                else -> false
            }
        }

    val dailyEnabledFlow: Flow<Boolean> = reminderDataSource.dailyEnabledFlow
    val djumaEnabledFlow: Flow<Boolean> = reminderDataSource.djumaEnabledFlow
    val morningTimeFlow: Flow<LocalTime> = reminderDataSource.getTimeFlow(ReminderType.Morning)
    val eveningTimeFlow: Flow<LocalTime> = reminderDataSource.getTimeFlow(ReminderType.Evening)
    val djumaTimeFlow: Flow<LocalTime> = reminderDataSource.getTimeFlow(ReminderType.Djuma)

    val launchPermissionSettingsFlow: MutableSharedFlow<Unit> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    fun onEnabledChange() {
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.S ->
                reminderDataSource.toggleEnabled(Settings.reminderEnabledKey)

            alarmManager.canScheduleExactAlarms() ->
                reminderDataSource.toggleEnabled(Settings.reminderEnabledKey)

            else ->
                launchPermissionSettingsFlow.tryEmit(Unit)
        }
    }

    fun onDailyEnabledChange(): Unit =
        reminderDataSource.toggleEnabled(Settings.reminderDailyEnabled)

    fun onDjumaEnabledChange(): Unit =
        reminderDataSource.toggleEnabled(Settings.reminderDjumaEnabled)

    fun onMorningTimeChange(value: LocalTime) =
        reminderDataSource.setTime(ReminderType.Morning, value)

    fun onEveningTimeChange(value: LocalTime) =
        reminderDataSource.setTime(ReminderType.Evening, value)

    fun onDjumaTimeChange(value: LocalTime) =
        reminderDataSource.setTime(ReminderType.Djuma, value)
}