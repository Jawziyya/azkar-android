package io.jawziyya.azkar.ui.settings.theme

import androidx.annotation.StringRes
import io.jawziyya.azkar.R

enum class DarkThemeOption(@StringRes val title: Int) {
    DISABLED(title = R.string.theme_settings_option_disabled),
    ENABLED(title = R.string.theme_settings_option_enabled),
    SYSTEM(title = R.string.theme_settings_option_system),
}