package io.jawziyya.azkar.database.model

import androidx.annotation.StringRes
import io.jawziyya.azkar.R
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created by uvays on 06.06.2022.
 */

@Serializable
enum class AzkarCategory(
    val value: String,
    @StringRes val titleRes: Int,
    val main: Boolean,
    val counter: Boolean,
) {
    @SerialName("morning")
    Morning(
        value = "morning",
        titleRes = R.string.azkar_category_morning,
        main = true,
        counter = true,
    ),

    @SerialName("evening")
    Evening(
        value = "evening",
        titleRes = R.string.azkar_category_evening,
        main = true,
        counter = true,
    ),

    @SerialName("night")
    Night(
        value = "night",
        titleRes = R.string.azkar_category_night,
        main = true,
        counter = false,
    ),

    @SerialName("after-salah")
    AfterSalah(
        value = "after-salah",
        titleRes = R.string.azkar_category_after_salah,
        main = true,
        counter = false,
    ),

    @SerialName("other")
    Other(
        value = "other",
        titleRes = R.string.azkar_category_other,
        main = false,
        counter = false,
    ),

    @SerialName("hundred-dua")
    HundredDua(
        value = "hundred-dua",
        titleRes = R.string.azkar_category_hundred_dua,
        main = false,
        counter = false,
    );

    companion object {
        fun fromValue(value: String?): AzkarCategory? = entries.find { it.value == value }
    }
}