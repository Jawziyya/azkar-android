package io.jawziyya.azkar.ui.settings

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Resources
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.ScopedServices
import io.jawziyya.azkar.data.helper.observeKey
import io.jawziyya.azkar.ui.core.BaseViewModel
import io.jawziyya.azkar.ui.core.Settings
import io.jawziyya.azkar.ui.settings.theme.DarkThemeOption
import io.jawziyya.azkar.ui.settings.theme.ThemeSettingsScreenKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val screenKey: SettingsScreenKey,
    private val backstack: Backstack,
    private val resources: Resources,
    private val application: Application,
    private val sharedPreferences: SharedPreferences,
) : BaseViewModel(), ScopedServices.Registered {

    val itemsFlow: MutableStateFlow<List<Pair<SettingsType, String>>> =
        MutableStateFlow(emptyList())

    override fun onServiceRegistered() {
        coroutineScope.launch {
            combine(
                sharedPreferences
                    .observeKey(Settings.darkThemeKey, DarkThemeOption.SYSTEM.name)
                    .map { value ->
                        val option = DarkThemeOption.valueOf(value)
                        val title = resources.getString(option.title)
                        SettingsType.DARK_THEME to title
                    },
//                sharedPreferences
//                    .observeKey(Settings.languageKey, "Не выбран язык")
//                    .map { value -> SettingsType.LANGUAGE to value },
                transform = { (theme) -> listOf(theme) }
            )
                .collect { value -> itemsFlow.value = value }
        }
    }

    fun onItemClick(type: SettingsType) {
        backstack.goTo(ThemeSettingsScreenKey())
//        when (type) {
//            SettingsType.DARK_THEME -> backstack.goTo(ThemeSettingsScreenKey())
//            SettingsType.LANGUAGE -> {}
//        }
    }
}