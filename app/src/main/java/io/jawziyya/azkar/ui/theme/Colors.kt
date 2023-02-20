package io.jawziyya.azkar.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

/**
 * Created by uvays on 22.01.2023.
 */

fun defaultColors(): Colors = Colors(
    accent = accent,
    alternativeAccent = alternativeAccent,
    background = background,
    secondaryBackground = secondaryBackground,
    contentBackground = contentBackground,
    text = text,
    secondaryText = secondaryText,
    tertiaryText = tertiaryText,
)

class Colors(
    accent: Color,
    alternativeAccent: Color,
    background: Color,
    secondaryBackground: Color,
    contentBackground: Color,
    text: Color,
    secondaryText: Color,
    tertiaryText: Color,
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

    var secondaryText by mutableStateOf(secondaryText)
        private set

    var tertiaryText by mutableStateOf(tertiaryText)
        private set

    fun copy(
        accent: Color = this.accent,
        alternativeAccent: Color = this.alternativeAccent,
        background: Color = this.background,
        secondaryBackground: Color = this.secondaryBackground,
        contentBackground: Color = this.contentBackground,
        text: Color = this.text,
        secondaryText: Color = this.secondaryText,
        tertiaryText: Color = this.tertiaryText,
    ): Colors = Colors(
        accent,
        alternativeAccent,
        background,
        secondaryBackground,
        contentBackground,
        text,
        secondaryText,
        tertiaryText,
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
    }
}