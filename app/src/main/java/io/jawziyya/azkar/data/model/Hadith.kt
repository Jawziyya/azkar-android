package io.jawziyya.azkar.data.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
 * Created by uvays on 23.02.2023.
 */

@Parcelize
@JsonClass(generateAdapter = true)
data class Hadith(
    @Json(name = "id") val id: Long,
    @Json(name = "text") val text: String?,
    @Json(name = "translation") val translation: String?,
    @Json(name = "translation_en") val translationEn: String?,
    @Json(name = "source") val sourceRaw: String?,
    @Json(name = "source_ext") val sourceExt: String?,
) : Parcelable {
    val source: Source? get() = Source.fromValue(sourceRaw).firstOrNull()
}