package io.jawziyya.azkar.ui.theme

import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.ProvideTextStyle
import androidx.compose.runtime.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.sp

@Immutable
data class Typography(
    val header: TextStyle,
    val body: TextStyle,
    val title: TextStyle,
    val subtitle: TextStyle,
    val sectionHeader: TextStyle,
    val tip: TextStyle,
    val arabic: TextStyle,
    val time: TextStyle,
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography

    val rememberedColors = remember { colors.copy() }.apply { updateColorsFrom(colors) }
    val selectionColors = rememberTextSelectionColors(rememberedColors)

    CompositionLocalProvider(
        LocalColors provides rememberedColors,
        LocalTypography provides typography,
        LocalTextSelectionColors provides selectionColors,
    ) {
        ProvideTextStyle(
            value = typography.title.copy(color = colors.text),
            content = content,
        )
    }
}

@Composable
private fun rememberTextSelectionColors(colors: Colors): TextSelectionColors {
    return remember(colors.accent, colors.accent.copy(alpha = 0.4f)) {
        TextSelectionColors(
            handleColor = colors.accent,
            backgroundColor = colors.accent.copy(alpha = 0.4f),
        )
    }
}

object AppTheme {
    val colors: Colors
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current
    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current
}

internal val LocalColors = staticCompositionLocalOf { defaultColors() }

internal val LocalTypography = staticCompositionLocalOf {
    Typography(
        header = TextStyle(
            letterSpacing = 0.sp,
            fontFamily = googleSansFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
        ),
        body = TextStyle(
            letterSpacing = 0.sp,
            fontFamily = googleSansFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            lineHeight = 24.sp,
        ),
        title = TextStyle(
            letterSpacing = 0.sp,
            fontFamily = googleSansFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
        ),
        subtitle = TextStyle(
            letterSpacing = 0.sp,
            fontFamily = googleSansFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
        ),
        sectionHeader = TextStyle(
            letterSpacing = .5.sp,
            fontFamily = googleSansFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
        ),
        tip = TextStyle(
            letterSpacing = 0.sp,
            fontFamily = googleSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
        ),
        arabic = TextStyle(
            letterSpacing = 0.sp,
            fontFamily = notoNaskhArabicFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 34.sp,
            textDirection = TextDirection.Rtl,
        ),
        time = TextStyle(
            letterSpacing = 0.sp,
            fontSize = 14.sp,
            fontFamily = googleSansFamily,
            fontWeight = FontWeight.Light,
            fontFeatureSettings = "tnum",
        ),
    )
}