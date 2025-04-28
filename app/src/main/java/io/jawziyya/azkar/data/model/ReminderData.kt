package io.jawziyya.azkar.data.model

import io.jawziyya.azkar.ui.settings.reminder.ReminderType
import java.time.LocalTime

data class ReminderData(
    val reminderType: ReminderType,
    val time: LocalTime,
)

data class DailyReminderData(
    val enabled: Boolean,
    val morningTime: LocalTime,
    val eveningTime: LocalTime,
)

data class DjumaReminderData(
    val enabled: Boolean,
    val time: LocalTime,
)
