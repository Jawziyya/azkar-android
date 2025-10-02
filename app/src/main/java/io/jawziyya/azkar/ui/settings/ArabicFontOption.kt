package io.jawziyya.azkar.ui.settings

import androidx.compose.ui.text.font.FontFamily
import io.jawziyya.azkar.ui.theme.KFGQPCArabicFamily
import io.jawziyya.azkar.ui.theme.adobeArabicFamily
import io.jawziyya.azkar.ui.theme.googleSansFamily
import io.jawziyya.azkar.ui.theme.marheyArabicFamily
import io.jawziyya.azkar.ui.theme.notoNaskhArabicFamily

enum class ArabicFontOption(
    val title: String,
    val fontFamily: FontFamily,
) {
    GoogleSans(
        title = "Google Sans",
        fontFamily = googleSansFamily,
    ),
    Adobe(
        title = "Adobe",
        fontFamily = adobeArabicFamily,
    ),
    KFGQP(
        title = "KFGQP",
        fontFamily = KFGQPCArabicFamily,
    ),
    NotoNaskh(
        title = "Noto Naskh",
        fontFamily = notoNaskhArabicFamily,
    ),
    Marhey(
        title = "Marhey",
        fontFamily = marheyArabicFamily,
    ),
    ;

    companion object {
        val fallback get() = KFGQP
    }
}