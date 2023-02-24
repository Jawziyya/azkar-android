package io.jawziyya.azkar.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by uvays on 06.06.2022.
 */

@Parcelize
data class Zikr(
    val id: Long,
    val category: AzkarCategory?,
    val source: List<Source?>,
    val title: String,
    val text: String,
    val translation: String?,
    val transliteration: String?,
    val order: Int,
    val audioFileUrl: String?,
    val audioFileName: String?,
    val repeats: Int,
    val hadith: Long?,
) : Parcelable
