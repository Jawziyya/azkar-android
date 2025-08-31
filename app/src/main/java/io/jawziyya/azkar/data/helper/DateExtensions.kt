package io.jawziyya.azkar.data.helper

import java.time.DayOfWeek
import java.time.LocalTime
import java.util.Calendar
import java.util.Date

fun LocalTime.toTodayDate(): Date = Calendar.getInstance()
    .apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    .time

fun LocalTime.toTomorrowDate(): Date = Calendar.getInstance()
    .apply {
        add(Calendar.DAY_OF_YEAR, 1)

        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    .time

fun LocalTime.toFridayDate(): Date = Calendar.getInstance()
    .apply {
        val difference = Calendar.FRIDAY - get(Calendar.DAY_OF_WEEK)
        val value = if (difference < 0) difference + 7 else difference
        add(Calendar.DAY_OF_YEAR, value)

        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    .time

fun LocalTime.toDayOfWeekDate(dayOfWeek: DayOfWeek): Date = Calendar.getInstance()
    .apply {
        val difference = dayOfWeek.toCalendarDayOfWeek() - get(Calendar.DAY_OF_WEEK)
        val value = if (difference < 0) difference + 7 else difference
        add(Calendar.DAY_OF_YEAR, value)

        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    .time

fun LocalTime.toNextFridayDate(): Date = Calendar.getInstance()
    .apply {
        val difference = Calendar.FRIDAY - get(Calendar.DAY_OF_WEEK)
        val value = if (difference <= 0) difference + 7 else difference
        add(Calendar.DAY_OF_YEAR, value)

        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    .time

fun LocalTime.toNextDayOfWeekDate(dayOfWeek: DayOfWeek): Date = Calendar.getInstance()
    .apply {
        val difference = dayOfWeek.toCalendarDayOfWeek() - get(Calendar.DAY_OF_WEEK)
        val value = if (difference < 0) difference + 7 else difference
        add(Calendar.DAY_OF_YEAR, value)

        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    .time

fun Date.isToday(): Boolean {
    val calendar = Calendar.getInstance()
    calendar.time = this

    val now = Calendar.getInstance()

    if (calendar.get(Calendar.YEAR) != now.get(Calendar.YEAR)) {
        return false
    }

    return calendar.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)

}

fun DayOfWeek.toCalendarDayOfWeek(): Int = when (this) {
    DayOfWeek.MONDAY -> Calendar.MONDAY
    DayOfWeek.TUESDAY -> Calendar.TUESDAY
    DayOfWeek.WEDNESDAY -> Calendar.WEDNESDAY
    DayOfWeek.THURSDAY -> Calendar.THURSDAY
    DayOfWeek.FRIDAY -> Calendar.FRIDAY
    DayOfWeek.SATURDAY -> Calendar.SATURDAY
    DayOfWeek.SUNDAY -> Calendar.SUNDAY
}