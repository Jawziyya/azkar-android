package io.jawziyya.azkar.ui.settings

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Resources
import com.zhuinden.simplestack.Backstack
import io.jawziyya.azkar.R
import io.jawziyya.azkar.data.helper.observeKey
import io.jawziyya.azkar.ui.core.BaseViewModel
import io.jawziyya.azkar.ui.core.Settings
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
) : BaseViewModel() {

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
                        return@map SettingsType.DARK_THEME to title
                    },
                sharedPreferences
                    .observeKey(Settings.languageKey, LanguageOption.getFallback().name)
                    .map { value ->
                        val option = LanguageOption.valueOf(value)
                        val title = resources.getString(option.title)
                        return@map SettingsType.LANGUAGE to title
                    },
                transform = { (theme, language) -> listOf(theme, language) },
            )
                .collect { value -> itemsFlow.value = value }
        }
    }

    fun onItemClick(type: SettingsType) {
        val screenKey = when (type) {
            SettingsType.DARK_THEME -> {
                val array = DarkThemeOption.values()
                ThemeSettingsScreenKey(
                    sharedPreferencesKey = Settings.darkThemeKey,
                    title = resources.getString(R.string.settings_type_dark_theme),
                    titles = array.map { value -> resources.getString(value.title) },
                    values = array.map { value -> value.name },
                    defaultValueIndex = DarkThemeOption.SYSTEM.ordinal,
                )
            }

            SettingsType.LANGUAGE -> {
                val array = LanguageOption.values()
                ThemeSettingsScreenKey(
                    sharedPreferencesKey = Settings.languageKey,
                    title = resources.getString(R.string.settings_type_language),
                    titles = array.map { value -> resources.getString(value.title) },
                    values = array.map { value -> value.name },
                    defaultValueIndex = LanguageOption.RUSSIAN.ordinal,
                )
            }
        }

        backstack.goTo(screenKey)
    }
}