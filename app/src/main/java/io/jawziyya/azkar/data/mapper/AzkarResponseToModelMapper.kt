package io.jawziyya.azkar.data.mapper

import io.jawziyya.azkar.data.model.Zikr
import io.jawziyya.azkar.data.model.AzkarCategory
import io.jawziyya.azkar.data.model.AzkarSource
import io.jawziyya.azkar.data.network.response.AzkarResponse

/**
 * Created by uvays on 07.06.2022.
 */

class AzkarResponseToModelMapper : (AzkarResponse) -> Zikr {
    override fun invoke(response: AzkarResponse): Zikr {
        val category = AzkarCategory.fromValue(response.category)
        val fallbackAudioFileName = "${category?.value}${response.rowInCategory}.mp3"

        return Zikr(
            id = response.id,
            category = category,
            source = AzkarSource.fromValue(response.source),
            title = response.titleRu ?: "Зикр №${response.rowInCategory}",
            text = response.text ?: "",
            translation = response.translationRu ?: "",
            transliteration = response.transliterationRu ?: "",
            order = response.rowInCategory,
            audioFileUrl = response.audioUrl,
            repeats = response.repeats ?: 1,
            audioFileName = response.audioFileName ?: fallbackAudioFileName,
        )
    }
}