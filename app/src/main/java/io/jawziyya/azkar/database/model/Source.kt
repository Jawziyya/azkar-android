package io.jawziyya.azkar.database.model

import androidx.annotation.StringRes
import io.jawziyya.azkar.R

/**
 * Created by uvays on 07.06.2022.
 */

enum class Source(
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
        fun fromValue(value: String?): List<Source> = value
            ?.split(", ")
            ?.mapNotNull { source -> values().find { enum -> enum.value == source } }
            ?: emptyList()
    }
}