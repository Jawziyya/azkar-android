package io.jawziyya.azkar.ui.settings.reminder

import androidx.annotation.StringRes
import io.jawziyya.azkar.R
import io.jawziyya.azkar.ui.core.Settings

enum class ReminderType(
    val id: Int,
    val storageKey: String,
    val defaultValue: Int,
    val lastEventStorageKey: String,
    @StringRes val message: Int,
) {
    Morning(
        id = 1001,
        storageKey = Settings.reminderMorningTime,
        defaultValue = 18000,
        lastEventStorageKey = Settings.reminderMorningLastEventDate,
        message = R.string.reminder_message_morning,
    ),
    Evening(
        id = 1002,
        storageKey = Settings.reminderEveningTime,
        defaultValue = 61200,
        lastEventStorageKey = Settings.reminderEveningLastEventDate,
        message = R.string.reminder_message_evening,
    ),
    Djuma(
        id = 1003,
        storageKey = Settings.reminderDjumaTime,
        defaultValue = 54000,
        lastEventStorageKey = Settings.reminderDjumaLastEventDate,
        message = R.string.reminder_message_djuma,
    ),
}