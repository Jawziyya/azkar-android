package io.jawziyya.azkar.database.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

/**
 * Created by uvays on 23.02.2023.
 */

@Immutable
@Parcelize
data class Hadith(
    val id: Long,
    val text: String,
    val translation: String?,
    val sources: List<Source>,
    val sourceExt: String,
) : Parcelable