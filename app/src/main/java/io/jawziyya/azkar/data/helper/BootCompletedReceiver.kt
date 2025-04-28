package io.jawziyya.azkar.data.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.koin.java.KoinJavaComponent
import timber.log.Timber

class BootCompletedReceiver : BroadcastReceiver() {

    private val reminderHelper: ReminderHelper
            by KoinJavaComponent.inject(ReminderHelper::class.java)

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("onReceive")
        if (Intent.ACTION_BOOT_COMPLETED != intent.action) {
            return
        }

        reminderHelper.initAlarms()
    }
}