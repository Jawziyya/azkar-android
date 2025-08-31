package io.jawziyya.azkar.data.datasource

import android.content.SharedPreferences
import androidx.core.content.edit
import io.jawziyya.azkar.data.helper.observeKey
import io.jawziyya.azkar.ui.core.Settings
import io.jawziyya.azkar.ui.settings.reminder.ReminderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalTime
import java.util.Date

class ReminderDataSource(
    private val sharedPreferences: SharedPreferences,
) {

    val enabledFlow: Flow<Boolean>
        get() = sharedPreferences.observeKey(Settings.reminderEnabledKey, false)

    val dailyEnabledFlow: Flow<Boolean>
        get() = sharedPreferences.observeKey(Settings.reminderDailyEnabled, false)

    val djumaEnabledFlow: Flow<Boolean>
        get() = sharedPreferences.observeKey(Settings.reminderDjumaEnabled, false)

    fun toggleEnabled(key: String) {
        val oldValue = sharedPreferences.getBoolean(key, false)
        sharedPreferences.edit { putBoolean(key, !oldValue) }
    }

    fun getTime(reminderType: ReminderType): LocalTime {
        val seconds = sharedPreferences.getInt(
            reminderType.storageKey,
            reminderType.defaultValue.toSecondOfDay(),
        )
        return LocalTime.ofSecondOfDay(seconds.toLong())
    }

    fun getTimeFlow(reminderType: ReminderType): Flow<LocalTime> =
        sharedPreferences
            .observeKey<Int>(reminderType.storageKey, reminderType.defaultValue.toSecondOfDay())
            .map { value -> LocalTime.ofSecondOfDay(value.toLong()) }

    fun setTime(reminderType: ReminderType, time: LocalTime) {
        sharedPreferences.edit { putInt(reminderType.storageKey, time.toSecondOfDay()) }
    }

    fun getLastEventDate(reminderType: ReminderType): Date =
        Date(sharedPreferences.getLong(reminderType.lastEventStorageKey, 0L))

    fun setLastEventDateNow(reminderType: ReminderType) {
        sharedPreferences.edit {
            putLong(
                reminderType.lastEventStorageKey,
                System.currentTimeMillis(),
            )
        }
    }

    fun resetLastEventDate(reminderType: ReminderType) {
        sharedPreferences.edit { putLong(reminderType.lastEventStorageKey, 0L) }
    }
}