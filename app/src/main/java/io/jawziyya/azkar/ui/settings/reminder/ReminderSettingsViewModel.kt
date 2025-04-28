package io.jawziyya.azkar.ui.settings.reminder

import androidx.lifecycle.ViewModel
import io.jawziyya.azkar.data.datasource.ReminderDataSource
import io.jawziyya.azkar.ui.core.Settings
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime

class ReminderSettingsViewModel(
    private val reminderDataSource: ReminderDataSource,
) : ViewModel() {

    val enabledFlow: Flow<Boolean> = reminderDataSource.enabledFlow
    val dailyEnabledFlow: Flow<Boolean> = reminderDataSource.dailyEnabledFlow
    val djumaEnabledFlow: Flow<Boolean> = reminderDataSource.djumaEnabledFlow
    val morningTimeFlow: Flow<LocalTime> = reminderDataSource.getTimeFlow(ReminderType.Morning)
    val eveningTimeFlow: Flow<LocalTime> = reminderDataSource.getTimeFlow(ReminderType.Evening)
    val djumaTimeFlow: Flow<LocalTime> = reminderDataSource.getTimeFlow(ReminderType.Djuma)

    fun onEnabledChange() = reminderDataSource.toggleEnabled(Settings.reminderEnabledKey)
    fun onDailyEnabledChange() = reminderDataSource.toggleEnabled(Settings.reminderDailyEnabled)
    fun onDjumaEnabledChange() = reminderDataSource.toggleEnabled(Settings.reminderDjumaEnabled)
    fun onMorningTimeChange(value: LocalTime) =
        reminderDataSource.setTime(ReminderType.Morning, value)

    fun onEveningTimeChange(value: LocalTime) =
        reminderDataSource.setTime(ReminderType.Evening, value)

    fun onDjumaTimeChange(value: LocalTime) = reminderDataSource.setTime(ReminderType.Djuma, value)
}