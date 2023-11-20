package io.jawziyya.azkar.ui.settings

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackcomposeintegration.core.LocalBackstack
import com.zhuinden.simplestackcomposeintegration.services.rememberService
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.lookup
import io.jawziyya.azkar.ui.core.navigation.ComposeKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class SettingsScreenKey(val placeholder: String = "") : ComposeKey() {
    override fun bindServices(serviceBinder: ServiceBinder) {
        super.bindServices(serviceBinder)
        with(serviceBinder) {
            val application = lookup<Application>()

            add(
                SettingsViewModel(
                    screenKey = this@SettingsScreenKey,
                    backstack = backstack,
                    resources = lookup(),
                    application = application,
                    sharedPreferences = lookup(),
                )
            )
        }
    }

    @Composable
    override fun ScreenComposable(modifier: Modifier) {
        val backstack = LocalBackstack.current
        val viewModel = rememberService<SettingsViewModel>()
        val items by viewModel.itemsFlow.collectAsState()

        SettingsScreen(
            onBackClick = backstack::goBack,
            items = items,
            onItemClick = viewModel::onItemClick,
        )
    }
}
