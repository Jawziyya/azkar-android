package io.jawziyya.azkar.database.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class Azkar(
    val id: Long,
    val repeats: Int,
    val category: AzkarCategory,
    val source: List<Source>,
    val title: String,
    val text: String,
    val translation: String?,
    val transliteration: String?,
    val order: Int,
    val benefits: String?,
    val notes: String?,
    val hadith: Long?,
    val audioName: String?,
) : Parcelable
