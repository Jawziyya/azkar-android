package io.jawziyya.azkar.data.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.jawziyya.azkar.data.repository.AzkarCounterRepository
import org.koin.java.KoinJavaComponent
import org.koin.java.KoinJavaComponent.get

class TimeChangeReceiver : BroadcastReceiver() {

    private val reminderHelper: ReminderHelper
            by KoinJavaComponent.inject(ReminderHelper::class.java)

    private val actions = arrayOf(
        Intent.ACTION_TIMEZONE_CHANGED,
        Intent.ACTION_DATE_CHANGED,
        Intent.ACTION_TIME_CHANGED,
    )

    override fun onReceive(context: Context, intent: Intent) {
        val validAction = actions.any { action -> action == intent.action }
        if (!validAction) {
            return
        }

        val repository: AzkarCounterRepository = get(AzkarCounterRepository::class.java)
        repository.reset()

        reminderHelper.initAlarms()
    }
}