package io.jawziyya.azkar.database.model

import androidx.annotation.StringRes
import io.jawziyya.azkar.R

/**
 * Created by uvays on 06.06.2022.
 */

enum class AzkarCategory(
    val value: String,
    @StringRes val titleRes: Int,
    val main: Boolean,
) {
    Morning(
        value = "morning",
        titleRes = R.string.azkar_category_morning,
        main = true,
    ),
    Evening(
        value = "evening",
        titleRes = R.string.azkar_category_evening,
        main = true,
    ),
    Night(
        value = "night",
        titleRes = R.string.azkar_category_night,
        main = true,
    ),
    AfterSalah(
        value = "after-salah",
        titleRes = R.string.azkar_category_after_salah,
        main = true,
    ),
    Other(
        value = "other",
        titleRes = R.string.azkar_category_other,
        main = false,
    );

    companion object {
        fun fromValue(value: String?): AzkarCategory? = values().find { it.value == value }
    }
}