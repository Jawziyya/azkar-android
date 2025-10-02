package io.jawziyya.azkar.ui.settings

import androidx.annotation.StringRes
import io.jawziyya.azkar.R

enum class SettingsType(@StringRes val title: Int) {
    DarkTheme(title = R.string.settings_type_dark_theme),
    Language(title = R.string.settings_type_language),
    Reminder(title = R.string.settings_type_reminder),
    ArabicFont(title = R.string.settings_type_font_arabic),
    TranslationFont(title = R.string.settings_type_font_translation),
}