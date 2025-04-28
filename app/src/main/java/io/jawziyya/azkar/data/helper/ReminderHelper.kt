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
import io.jawziyya.azkar.ui.settings.reminder.ReminderType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import timber.log.Timber
import java.time.LocalTime
import java.util.Date

class ReminderHelper(
    private val application: Application,
    private val coroutineScope: CoroutineScope,
    private val alarmManager: AlarmManager,
    private val reminderDataSource: ReminderDataSource,
    private val sharedPreferences: SharedPreferences,
) {

    private val forceUpdateSharedFlow: MutableSharedFlow<Unit> = MutableSharedFlow()

    init {
        val dailyFlow = combine(
            flow = sharedPreferences.observeKey(
                key = Settings.reminderDailyEnabled,
                default = false,
            ),
            flow2 = sharedPreferences.observeKey(
                key = Settings.reminderMorningTime,
                default = ReminderType.Morning.defaultValue,
            ),
            flow3 = sharedPreferences.observeKey(
                key = Settings.reminderEveningTime,
                default = ReminderType.Evening.defaultValue,
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
                default = ReminderType.Djuma.defaultValue,
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

        forceUpdateSharedFlow
            .onStart { emit(Unit) }
            .flatMapLatest {
                combine(
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
                    })
            }
            .onEach { cancelAll() }.flatMapConcat { array -> array.asFlow() }
            .onEach { item ->
                val (type, time) = item
                val isLastEventToday = reminderDataSource.getLastEventDate(type).isToday()
                val date = when {
                    type == ReminderType.Djuma && isLastEventToday -> time.toNextFridayDate()
                    type == ReminderType.Djuma -> time.toFridayDate()
//                    isLastEventToday -> time.toTomorrowDate()
                    else -> time.toTodayDate()
                }

                setAlarm(
                    reminderType = type,
                    date = date,
                )
            }
            .launchIn(coroutineScope)
    }

    fun initAlarms() {
        Timber.d("initAlarms()")
        forceUpdateSharedFlow.tryEmit(Unit)
    }

    private fun cancelAll() {
        Timber.d("cancelAll()")
        ReminderType.entries.toTypedArray().forEach { type ->
            val pendingIntent = createPendingIntent(reminderType = type) ?: return@forEach
            alarmManager.cancel(pendingIntent)

            Timber.d("cancel(), pendingIntent=$pendingIntent")
        }
    }

    private fun createPendingIntent(reminderType: ReminderType): PendingIntent? =
        PendingIntentCompat.getBroadcast(
            application,
            reminderType.id,
            ReminderReceiver.createIntent(
                context = application,
                reminderType = reminderType,
            ),
            PendingIntent.FLAG_UPDATE_CURRENT,
            false,
        )

    private fun setAlarm(reminderType: ReminderType, date: Date, force: Boolean = false) {
        Timber.d("setAlarm(), reminderType=$reminderType, date=$date")
        val pendingIntent = createPendingIntent(reminderType) ?: return

        val canScheduleExactAlarms =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) alarmManager.canScheduleExactAlarms()
            else true

        if (canScheduleExactAlarms) {
            alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(date.time, null), pendingIntent)

            if (force) {
                reminderDataSource.resetLastEventDate(reminderType)
            }
        }
    }
}