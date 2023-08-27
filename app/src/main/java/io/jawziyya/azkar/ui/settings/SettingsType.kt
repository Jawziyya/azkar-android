package io.jawziyya.azkar.ui.settings

import androidx.annotation.StringRes
import io.jawziyya.azkar.R

enum class SettingsType(@StringRes val title: Int) {
    DARK_THEME(title = R.string.settings_type_dark_theme),
    LANGUAGE(title = R.string.settings_type_language),
}