package io.jawziyya.azkar.ui.settings.reminder

import androidx.annotation.StringRes
import io.jawziyya.azkar.R
import io.jawziyya.azkar.ui.core.Settings
import java.time.DayOfWeek
import java.time.LocalTime

enum class ReminderType(
    val id: Int,
    val defaultValue: LocalTime,
    val storageKey: String,
    val lastEventStorageKey: String,
    @StringRes val message: Int,
    val regularity: Regularity,
) {
    Morning(
        id = 1000,
        defaultValue = LocalTime.of(8, 0),
        storageKey = Settings.reminderMorningTime,
        lastEventStorageKey = Settings.reminderMorningLastEventDate,
        message = R.string.reminder_message_morning,
        regularity = Regularity.Daily,
    ),
    Evening(
        id = 1010,
        defaultValue = LocalTime.of(16, 0),
        storageKey = Settings.reminderEveningTime,
        lastEventStorageKey = Settings.reminderEveningLastEventDate,
        message = R.string.reminder_message_evening,
        regularity = Regularity.Daily,
    ),
    Djuma(
        id = 1020,
        defaultValue = LocalTime.of(18, 0),
        storageKey = Settings.reminderDjumaTime,
        lastEventStorageKey = Settings.reminderDjumaLastEventDate,
        message = R.string.reminder_message_djuma,
        regularity = Regularity.Weekly(
            day = DayOfWeek.FRIDAY,
        )
    );

    val requestCode get() = when (regularity) {
        Regularity.Daily -> id
        is Regularity.Weekly -> id + regularity.day.value
    }
}