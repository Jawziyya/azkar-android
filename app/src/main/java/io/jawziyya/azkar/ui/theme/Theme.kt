package io.jawziyya.azkar.ui.theme

import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import io.jawziyya.azkar.ui.settings.ArabicFontOption
import io.jawziyya.azkar.ui.settings.TranslationFontOption

@Immutable
data class Typography(
    val header: TextStyle,
    val body: TextStyle,
    val title: TextStyle,
    val subtitle: TextStyle,
    val sectionHeader: TextStyle,
    val tip: TextStyle,
    val arabic: TextStyle,
    val translation: TextStyle,
    val digits: TextStyle,
)

@Composable
fun AppTheme(
    darkTheme: Boolean,
    translationFontOption: TranslationFontOption,
    arabicFontOption: ArabicFontOption,
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) darkColors() else defaultColors()
    val rememberedColors = remember(darkTheme) { colors.copy() }
        .apply { updateColorsFrom(colors) }
    val selectionColors = rememberTextSelectionColors(rememberedColors)

    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !darkTheme

    LaunchedEffect(systemUiController, useDarkIcons) {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons,
        )
    }

    val typography = remember(
        translationFontOption,
        arabicFontOption,
    ) {
        provideTypography(
            translationFontFamily = translationFontOption.fontFamily,
            arabicFontFamily = arabicFontOption.fontFamily,
        )
    }

    CompositionLocalProvider(
        LocalColors provides rememberedColors,
        LocalTypography provides typography,
        LocalTextSelectionColors provides selectionColors,
        LocalDarkTheme provides darkTheme,
    ) {
        ProvideTextStyle(
            value = typography.title.copy(color = rememberedColors.text),
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

private fun provideTypography(
    translationFontFamily: FontFamily,
    arabicFontFamily: FontFamily,
): Typography {
    return Typography(
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
            fontWeight = FontWeight.Normal,
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
            fontFamily = arabicFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 30.sp,
            textDirection = TextDirection.Rtl,
        ),
        translation = TextStyle(
            letterSpacing = 0.sp,
            fontFamily = translationFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            lineHeight = 24.sp,
        ),
        digits = TextStyle(
            letterSpacing = 0.sp,
            fontSize = 14.sp,
            fontFamily = googleSansFamily,
            fontWeight = FontWeight.Light,
            fontFeatureSettings = "tnum",
        ),
    )
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
    val darkTheme: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalDarkTheme.current
}

internal val LocalDarkTheme = staticCompositionLocalOf { false }

internal val LocalColors = staticCompositionLocalOf { defaultColors() }

internal val LocalTypography = staticCompositionLocalOf {
    provideTypography(
        translationFontFamily = googleSansFamily,
        arabicFontFamily = marheyArabicFamily,
    )
}
