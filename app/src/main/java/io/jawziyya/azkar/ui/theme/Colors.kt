package io.jawziyya.azkar.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

/**
 * Created by uvays on 22.01.2023.
 */

fun defaultColors(): Colors = Colors(
    accent = Color(0xFF9D8ECE),
    alternativeAccent = Color(0xFFA290EB),
    background = Color(0xFFF7F7F7),
    secondaryBackground = Color(0xFFF1F1F7),
    contentBackground = Color(0xFFFFFFFF),
    text = Color(0xFF000000),
    textOnAccent = Color(0xFFFFFFFF),
    secondaryText = Color(0xFF515151),
    tertiaryText = Color(0xFFB4B4B4),
    icon = Color(0xFF000000),
    progressBackground = Color(0xFFF1F1F7),
    divider = Color(0xFFDADADA),
)

fun darkColors(): Colors = Colors(
    accent = Color(0xFF916DD5),
    alternativeAccent = Color(0xFF916DD5),
    background = Color(0xFF111111),
    secondaryBackground = Color(0xFF111111),
    contentBackground = Color(0xFF1B1A1B),
    text = Color(0xFFFFFFFF),
    textOnAccent = Color(0xFFFFFFFF),
    secondaryText = Color(0xFFCCCED9),
    tertiaryText = Color(0xFF5A5A5A),
    icon = Color(0xFFFFFFFF),
    progressBackground = Color(0xFF252525),
    divider = Color(0xFF252525),
)

class Colors(
    accent: Color,
    alternativeAccent: Color,
    background: Color,
    secondaryBackground: Color,
    contentBackground: Color,
    text: Color,
    textOnAccent: Color,
    secondaryText: Color,
    tertiaryText: Color,
    icon: Color,
    progressBackground: Color,
    divider: Color,
) {
    var accent by mutableStateOf(accent)
        private set

    var alternativeAccent by mutableStateOf(alternativeAccent)
        private set

    var background by mutableStateOf(background)
        private set

    var secondaryBackground by mutableStateOf(secondaryBackground)
        private set

    var contentBackground by mutableStateOf(contentBackground)
        private set

    var text by mutableStateOf(text)
        private set

    var textOnAccent by mutableStateOf(textOnAccent)
        private set

    var secondaryText by mutableStateOf(secondaryText)
        private set

    var tertiaryText by mutableStateOf(tertiaryText)
        private set

    var icon by mutableStateOf(icon)
        private set

    var progressBackground by mutableStateOf(progressBackground)
        private set

    var divider by mutableStateOf(divider)
        private set

    fun copy(
        accent: Color = this.accent,
        alternativeAccent: Color = this.alternativeAccent,
        background: Color = this.background,
        secondaryBackground: Color = this.secondaryBackground,
        contentBackground: Color = this.contentBackground,
        text: Color = this.text,
        textOnAccent: Color = this.textOnAccent,
        secondaryText: Color = this.secondaryText,
        tertiaryText: Color = this.tertiaryText,
        icon: Color = this.icon,
        progressBackground: Color = this.progressBackground,
        divider: Color = this.divider,
    ): Colors = Colors(
        accent = accent,
        alternativeAccent = alternativeAccent,
        background = background,
        secondaryBackground = secondaryBackground,
        contentBackground = contentBackground,
        text = text,
        textOnAccent = textOnAccent,
        secondaryText = secondaryText,
        tertiaryText = tertiaryText,
        icon = icon,
        progressBackground = progressBackground,
        divider = divider,
    )

    fun updateColorsFrom(other: Colors) {
        accent = other.accent
        alternativeAccent = other.alternativeAccent
        background = other.background
        secondaryBackground = other.secondaryBackground
        contentBackground = other.contentBackground
        text = other.text
        secondaryText = other.secondaryText
        tertiaryText = other.tertiaryText
        icon = other.icon
        progressBackground = other.progressBackground
        divider = other.divider
    }
}