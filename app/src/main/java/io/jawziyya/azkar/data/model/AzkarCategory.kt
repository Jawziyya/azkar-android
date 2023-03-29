package io.jawziyya.azkar.data.model

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
    MORNING(
        value = "morning",
        titleRes = R.string.azkar_category_morning,
        main = true,
    ),
    EVENING(
        value = "evening",
        titleRes = R.string.azkar_category_evening,
        main = true,
    ),
    AFTER_SALAH(
        value = "after-salah",
        titleRes = R.string.azkar_category_after_salah,
        main = false,
    ),
    OTHER(
        value = "other",
        titleRes = R.string.azkar_category_other,
        main = false,
    );

    companion object {
        fun fromValue(value: String?): AzkarCategory? =
            values().find { enum -> enum.value == value }
    }
}