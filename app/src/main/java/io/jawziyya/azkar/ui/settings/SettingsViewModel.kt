package io.jawziyya.azkar.ui.settings

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.jawziyya.azkar.data.helper.observeKey
import io.jawziyya.azkar.ui.core.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val resources: Resources,
    private val sharedPreferences: SharedPreferences,
) : ViewModel() {

    val itemsFlow: MutableStateFlow<List<Pair<SettingsType, String>>> =
        MutableStateFlow(emptyList())

    init {
        viewModelScope.launch {
            combine(
                sharedPreferences
                    .observeKey(Settings.darkThemeKey, DarkThemeOption.SYSTEM.name)
                    .map { value ->
                        val option = DarkThemeOption.valueOf(value)
                        val title = resources.getString(option.title)
                        return@map SettingsType.DarkTheme to title
                    },
                sharedPreferences
                    .observeKey(Settings.languageKey, LanguageOption.getFallback().name)
                    .map { value ->
                        val option = LanguageOption.valueOf(value)
                        val title = resources.getString(option.title)
                        return@map SettingsType.Language to title
                    },
                sharedPreferences
                    .observeKey(Settings.arabicFontKey, ArabicFontOption.fallback.name)
                    .map { value ->
                        val option = ArabicFontOption.valueOf(value)
                        return@map SettingsType.ArabicFont to option.title
                    },
                sharedPreferences
                    .observeKey(Settings.translationFontKey, TranslationFontOption.fallback.name)
                    .map { value ->
                        val option = TranslationFontOption.valueOf(value)
                        return@map SettingsType.TranslationFont to option.title
                    },
                flowOf(SettingsType.Reminder to ""),
                transform = { (theme, language, arabicFont, translationFont, reminder) ->
                    listOf(
                        theme,
                        language,
                        arabicFont,
                        translationFont,
                        reminder,
                    )
                },
            )
                .collect { value -> itemsFlow.value = value }
        }
    }
}