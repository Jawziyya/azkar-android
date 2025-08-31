package io.jawziyya.azkar.ui.settings.reminder

import java.time.DayOfWeek

sealed class Regularity {
    data object Daily : Regularity()
    data class Weekly(val day: DayOfWeek) : Regularity()
}