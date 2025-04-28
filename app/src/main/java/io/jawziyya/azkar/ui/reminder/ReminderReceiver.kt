package io.jawziyya.azkar.ui.reminder

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.app.PendingIntentCompat
import androidx.core.content.getSystemService
import io.jawziyya.azkar.R
import io.jawziyya.azkar.data.datasource.ReminderDataSource
import io.jawziyya.azkar.data.helper.toTomorrowDate
import io.jawziyya.azkar.ui.AppActivity
import io.jawziyya.azkar.ui.settings.reminder.ReminderType
import io.jawziyya.azkar.ui.theme.defaultColors
import org.koin.java.KoinJavaComponent
import timber.log.Timber

/**
 * Reminder notification logic:
 * - every single reminder type launches a notification once per day
 * - if already shown today, don't repeat, also if rebooted
 * -
 */

class ReminderReceiver : BroadcastReceiver() {
    companion object {
        private const val EXTRA_REMINDER_TYPE = "EXTRA_REMINDER_TYPE"
        private const val REMINDER_CHANNEL_ID = "reminder_channel_id"

        fun createIntent(context: Context, reminderType: ReminderType): Intent =
            Intent(context, ReminderReceiver::class.java)
                .putExtra(EXTRA_REMINDER_TYPE, reminderType.name)
    }

    private val accentColor by lazy { defaultColors().accent.toArgb() }
    private val reminderDataSource: ReminderDataSource
            by KoinJavaComponent.inject(ReminderDataSource::class.java)
    private val alarmManager: AlarmManager
            by KoinJavaComponent.inject(AlarmManager::class.java)

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.d("onReceive")
        if (context == null) {
            return
        }

        val reminderTypeName = intent?.extras?.getString(EXTRA_REMINDER_TYPE) ?: return
        val reminderType = ReminderType.valueOf(reminderTypeName)
        val message = context.resources.getString(reminderType.message)

//        if (reminderDataSource.getLastEventDate(reminderType).isToday()) {
//            Timber.d("already shown today")
//            setTomorrowAlarm(context = context, reminderType = reminderType)
//            return
//        }

        Timber.d("onReceive, ${reminderType.message}")
        val notificationManager = context.getSystemService<NotificationManager>() ?: return
        val pendingIntent = PendingIntentCompat.getActivity(
            context,
            1000,
            AppActivity.createIntent(context),
            PendingIntent.FLAG_UPDATE_CURRENT,
            false,
        )

        val notification = NotificationCompat
            .Builder(context, REMINDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setColor(accentColor)
            .setContentTitle("Напоминание")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(pendingIntent)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(REMINDER_CHANNEL_ID, "Reminder", importance)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(reminderType.id, notification)

        reminderDataSource.setLastEventDateNow(reminderType)
        setTomorrowAlarm(context = context, reminderType = reminderType)

        Timber.d("onReceive, notification shown")
    }

    private fun setTomorrowAlarm(context: Context, reminderType: ReminderType) {
        val date = reminderDataSource.getTime(reminderType).toTomorrowDate()

        val intent = createIntent(
            context = context,
            reminderType = reminderType,
        )
        val pendingIntent = PendingIntentCompat.getBroadcast(
            context,
            reminderType.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT,
            false,
        ) ?: return

        val canScheduleExactAlarms =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) alarmManager.canScheduleExactAlarms()
            else true

        if (canScheduleExactAlarms) {
            alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(date.time, null), pendingIntent)
            Timber.d("setAlarmClock, $date")
        }
    }
}