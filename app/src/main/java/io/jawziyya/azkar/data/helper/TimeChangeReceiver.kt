package io.jawziyya.azkar.data.helper

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.zhuinden.simplestackextensions.servicesktx.getOrNull
import io.jawziyya.azkar.App
import io.jawziyya.azkar.data.repository.AzkarCounterRepository

class TimeChangeReceiver : BroadcastReceiver() {

    private val actions = arrayOf(
        Intent.ACTION_TIMEZONE_CHANGED,
        Intent.ACTION_DATE_CHANGED,
        Intent.ACTION_TIME_CHANGED,
    )

    override fun onReceive(context: Context, intent: Intent) {
        val validAction = actions.any { action -> action == intent.action }
        if (!validAction) return

        val application = context.applicationContext as? Application ?: return
        val globalServices = (application as? App)?.globalServices ?: return
        val repository = globalServices.getOrNull<AzkarCounterRepository>() ?: return

        repository.reset()
    }
}