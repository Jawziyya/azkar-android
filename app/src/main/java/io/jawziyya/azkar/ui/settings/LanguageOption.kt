package io.jawziyya.azkar.ui.settings

import androidx.annotation.StringRes
import io.jawziyya.azkar.R
import java.util.Locale

enum class LanguageOption(
    @StringRes val title: Int,
    val value: String,
    val main: Boolean,
) {
    ENGLISH(
        title = R.string.language_en,
        value = "en",
        main = true,
    ),
    RUSSIAN(
        title = R.string.language_ru,
        value = "ru",
        main = true,
    ),
    GEORGIAN(
        title = R.string.language_ka,
        value = "ka",
        main = false,
    ),
    CHECHEN(
        title = R.string.language_che,
        value = "che",
        main = false,
    ),
    INGUSH(
        title = R.string.language_inh,
        value = "inh",
        main = false,
    ),
    UZBEK(
        title = R.string.language_uz,
        value = "uz",
        main = false,
    ),
    KYRGYZ(
        title = R.string.language_ky,
        value = "ky",
        main = false,
    ),
    KAZAKH(
        title = R.string.language_kz,
        value = "kz",
        main = false,
    );

    companion object {
        val mainArray: Array<LanguageOption> = values().filter { enum -> enum.main }.toTypedArray()

        fun getFallback(): LanguageOption = Locale.getDefault().language
            .let { language -> mainArray.find { enum -> enum.value == language } }
            ?: RUSSIAN
    }
}