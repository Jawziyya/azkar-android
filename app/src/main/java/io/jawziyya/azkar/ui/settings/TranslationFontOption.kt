package io.jawziyya.azkar.ui.settings

import androidx.compose.ui.text.font.FontFamily
import io.jawziyya.azkar.ui.theme.googleSansFamily

enum class TranslationFontOption(
    val title: String,
    val fontFamily: FontFamily,
) {
    System(
        title = "System",
        fontFamily = FontFamily.Default,
    ),
    SansSerif(
        title = "SansSerif",
        fontFamily = FontFamily.SansSerif,
    ),
    Serif(
        title = "Serif",
        fontFamily = FontFamily.Serif,
    ),
    Monospace(
        title = "Monospace",
        fontFamily = FontFamily.Monospace,
    ),
    Cursive(
        title = "Cursive",
        fontFamily = FontFamily.Cursive,
    ),
    GoogleSans(
        title = "GoogleSans",
        fontFamily = googleSansFamily,
    ),
    ;

    companion object {
        val fallback get() = Serif
    }
}