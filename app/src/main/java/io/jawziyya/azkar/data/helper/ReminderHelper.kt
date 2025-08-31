package io.jawziyya.azkar.data.helper

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.SharedPreferences
import android.os.Build
import androidx.core.app.PendingIntentCompat
import io.jawziyya.azkar.data.datasource.ReminderDataSource
import io.jawziyya.azkar.data.model.ReminderData
import io.jawziyya.azkar.ui.core.Settings
import io.jawziyya.azkar.ui.reminder.ReminderReceiver
import io.jawziyya.azkar.ui.settings.reminder.Regularity
import io.jawziyya.azkar.ui.settings.reminder.ReminderType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters
import java.util.Date

class ReminderHelper(
    private val application: Application,
    private val coroutineScope: CoroutineScope,
    private val alarmManager: AlarmManager,
    private val reminderDataSource: ReminderDataSource,
    private val sharedPreferences: SharedPreferences,
) {

    private var job: Job? = null

    init {
        initAlarms()
    }

    fun initAlarms() {
        Timber.d("initAlarms()")

        val dailyFlow = combine(
            flow = sharedPreferences.observeKey(
                key = Settings.reminderDailyEnabled,
                default = false,
            ),
            flow2 = sharedPreferences.observeKey(
                key = Settings.reminderMorningTime,
                default = ReminderType.Morning.defaultValue.toSecondOfDay(),
            ),
            flow3 = sharedPreferences.observeKey(
                key = Settings.reminderEveningTime,
                default = ReminderType.Evening.defaultValue.toSecondOfDay(),
            ),
            transform = { enabled, morning, evening ->
                if (!enabled) {
                    return@combine emptyArray<ReminderData>()
                }

                return@combine arrayOf(
                    ReminderData(
                        reminderType = ReminderType.Morning,
                        time = LocalTime.ofSecondOfDay(morning.toLong()),
                    ),
                    ReminderData(
                        reminderType = ReminderType.Evening,
                        time = LocalTime.ofSecondOfDay(evening.toLong()),
                    ),
                )
            },
        )

        val djumaFlow = combine(
            flow = sharedPreferences.observeKey(
                key = Settings.reminderDjumaEnabled,
                default = false,
            ),
            flow2 = sharedPreferences.observeKey(
                key = Settings.reminderDjumaTime,
                default = ReminderType.Djuma.defaultValue.toSecondOfDay(),
            ),
            transform = { enabled, time ->
                if (!enabled) {
                    return@combine emptyArray<ReminderData>()
                }

                return@combine arrayOf(
                    ReminderData(
                        reminderType = ReminderType.Djuma,
                        time = LocalTime.ofSecondOfDay(time.toLong()),
                    )
                )
            },
        )

        job?.cancel()
        job = combine(
            flow = sharedPreferences.observeKey(Settings.reminderEnabledKey, false),
            flow2 = dailyFlow,
            flow3 = djumaFlow,
            transform = { enabled, dailyArray, djumaArray ->
                if (!enabled) {
                    return@combine emptyArray<ReminderData>()
                }

                return@combine arrayOf(
                    *dailyArray,
                    *djumaArray,
                )
            },
        )
            .onEach { cancelAll() }
            .flatMapConcat { array -> array.asFlow() }
            .onEach { (type, time) ->
                val isLastEventToday = reminderDataSource.getLastEventDate(type).isToday()
                val date = when (type.regularity) {
                    Regularity.Daily -> nextDailyTrigger(
                        time = time,
                        isLastEventToday = isLastEventToday,
                    )

                    is Regularity.Weekly -> nextWeeklyTrigger(
                        time = time,
                        day = type.regularity.day,
                        isLastEventToday = isLastEventToday,
                    )
                }

                setAlarm(
                    reminderType = type,
                    date = date,
                )
            }
            .launchIn(coroutineScope)
    }

    private fun nextDailyTrigger(time: LocalTime, isLastEventToday: Boolean): Date {
        val now = ZonedDateTime.now()
        val todayTrigger = now.with(time)

        val nextTrigger = when {
            isLastEventToday -> todayTrigger.plusDays(1)
            todayTrigger.isAfter(now) -> todayTrigger
            else -> todayTrigger.plusDays(1)
        }

        return Date.from(nextTrigger.toInstant())
    }

    private fun nextWeeklyTrigger(
        time: LocalTime,
        day: DayOfWeek,
        isLastEventToday: Boolean
    ): Date {
        val now = ZonedDateTime.now()
        val initialTrigger = now.with(TemporalAdjusters.nextOrSame(day)).with(time)

        val nextTrigger = when {
            isLastEventToday -> initialTrigger.plusWeeks(1)
            initialTrigger.isBefore(now) -> initialTrigger.plusWeeks(1)
            else -> initialTrigger
        }

        return Date.from(nextTrigger.toInstant())
    }

    private fun createPendingIntent(reminderType: ReminderType): PendingIntent? =
        PendingIntentCompat.getBroadcast(
            application,
            reminderType.requestCode,
            ReminderReceiver.createIntent(
                context = application,
                reminderType = reminderType,
            ),
            PendingIntent.FLAG_UPDATE_CURRENT,
            false,
        )

    private fun cancelAll() {
        Timber.d("cancelAll()")
        ReminderType.entries.toTypedArray().forEach { type ->
            val pendingIntent = createPendingIntent(reminderType = type) ?: return@forEach
            alarmManager.cancel(pendingIntent)

            Timber.d("cancel(), pendingIntent=$pendingIntent")
        }
    }

    private fun setAlarm(
        reminderType: ReminderType,
        date: Date,
        force: Boolean = false,
    ) {
        Timber.d("setAlarm(), reminderType=$reminderType, date=$date")
        val pendingIntent = createPendingIntent(reminderType) ?: return

        val canScheduleExactAlarms =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) alarmManager.canScheduleExactAlarms()
            else true

        if (canScheduleExactAlarms) {
            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(date.time, null),
                pendingIntent,
            )

            if (force) {
                reminderDataSource.resetLastEventDate(reminderType)
            }
        }
    }
}