package io.jawziyya.azkar.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by uvays on 06.06.2022.
 */

@JsonClass(generateAdapter = true)
data class AzkarResponse(
    @Json(name = "id") val id: Long,
    @Json(name = "audio_url") val audioUrl: String?,
    @Json(name = "category") val category: String?,
    @Json(name = "notes_ru") val notes_ru: String?,
    @Json(name = "repeats") val repeats: Int?,
    @Json(name = "row_in_category") val rowInCategory: Int,
    @Json(name = "source") val source: String?,
    @Json(name = "text") val text: String?,
    @Json(name = "title_ru") val titleRu: String?,
    @Json(name = "translation_ru") val translationRu: String?,
    @Json(name = "transliteration_ru") val transliterationRu: String?,
    @Json(name = "hadith") val hadith: Long?,
    @Json(name = "benefit_ru") val benefitRu: String?,
    @Json(name = "audio_file_name") val audioFileName: String?,
    @Json(name = "translation_en") val translationEn: String?,
    @Json(name = "title_en") val titleEn: String?,
)