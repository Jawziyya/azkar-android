package io.jawziyya.azkar.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.graphicsLayer
import androidx.core.view.WindowCompat
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.zhuinden.simplestack.History
import com.zhuinden.simplestackcomposeintegration.core.ComposeNavigator
import com.zhuinden.simplestackcomposeintegration.core.ComposeStateChanger
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider
import com.zhuinden.simplestackextensions.servicesktx.get
import io.jawziyya.azkar.App
import io.jawziyya.azkar.data.helper.observeKey
import io.jawziyya.azkar.ui.core.Settings
import io.jawziyya.azkar.ui.main.MainScreenKey
import io.jawziyya.azkar.ui.settings.DarkThemeOption
import io.jawziyya.azkar.ui.theme.AppTheme
import kotlinx.coroutines.flow.map

class AppActivity : AppCompatActivity() {

    companion object {
        fun createIntent(context: Context): Intent = Intent(context, AppActivity::class.java)
    }

    private val appUpdateLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { _ -> }

    private lateinit var appUpdateManager: AppUpdateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fixes issue when starting the app again from icon on launcher
        if (isTaskRoot.not()
            && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
            && intent.action != null
            && intent.action == Intent.ACTION_MAIN
        ) {
            finish()
            return
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val animationConfiguration = ComposeStateChanger.AnimationConfiguration(
            previousComposableTransition = { modifier, _, animationProgress ->
                modifier.graphicsLayer { alpha = (1 - animationProgress.value) }
            },
            newComposableTransition = { modifier, _, animationProgress ->
                modifier.graphicsLayer { alpha = 0 + animationProgress.value }
            },
        )

        setContent {
            val systemDarkTheme = isSystemInDarkTheme()
            val darkThemeFlow = remember(systemDarkTheme) {
                (application as App).globalServices
                    .get<SharedPreferences>()
                    .observeKey(Settings.darkThemeKey, DarkThemeOption.SYSTEM.name)
                    .map { name -> DarkThemeOption.valueOf(name) }
                    .map { selectedOption ->
                        return@map when (selectedOption) {
                            DarkThemeOption.DISABLED -> false
                            DarkThemeOption.ENABLED -> true
                            DarkThemeOption.SYSTEM -> systemDarkTheme
                        }
                    }
            }
            val darkTheme by darkThemeFlow.collectAsState(systemDarkTheme)

            LaunchedEffect(darkTheme) {
                val drawable = ColorDrawable(if (darkTheme) Color.BLACK else Color.WHITE)
                window.setBackgroundDrawable(drawable)
            }

            AppTheme(darkTheme = darkTheme) {
                ComposeNavigator(animationConfiguration = animationConfiguration) {
                    createBackstack(
                        initialKeys = History.of(MainScreenKey()),
                        scopedServices = DefaultServiceProvider(),
                        globalServices = (application as App).globalServices
                    )
                }
            }
        }

        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            val available = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val allowed = info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)

            if (available && allowed) {
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    appUpdateLauncher,
                    AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE),
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()

        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    appUpdateLauncher,
                    AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE),
                )
            }
        }
    }
}