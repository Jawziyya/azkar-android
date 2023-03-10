package io.jawziyya.azkar.data.model

import androidx.annotation.StringRes
import io.jawziyya.azkar.R

/**
 * Created by uvays on 07.06.2022.
 */

enum class AzkarSource(
    val value: String,
    @StringRes val titleRes: Int,
) {
    QURAN(value = "Quran", titleRes = R.string.quran),
    BUKHARI(value = "Bukhari", titleRes = R.string.bukhari),
    MUSLIM(value = "Muslim", titleRes = R.string.muslim),
    AHMAD(value = "Ahmad", titleRes = R.string.ahmad),
    TIRMIDHI(value = "Tirmidhi", titleRes = R.string.tirmidhi),
    DARIMI(value = "Darimi", titleRes = R.string.darimi),
    ABUDAUD(value = "AbuDaud", titleRes = R.string.abudaud),
    NASAI(value = "Nasai", titleRes = R.string.nasai),
    IBNHUZEYMA(value = "IbnHuzeyma", titleRes = R.string.ibnhuzeyma);

    companion object {
        private val values = values()

        fun fromValue(value: String?): List<AzkarSource> = value
            ?.split(", ")
            ?.mapNotNull { source -> values.find { enumValue -> enumValue.value == source } }
            ?: emptyList()
    }
}