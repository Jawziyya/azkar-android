package io.jawziyya.azkar.data.repository

import io.jawziyya.azkar.R
import io.jawziyya.azkar.data.helper.JsonParser
import io.jawziyya.azkar.data.model.Hadith
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Created by uvays on 23.02.2023.
 */

class HadithRepository(private val jsonParser: JsonParser) {

    private val mapStateFlow: MutableStateFlow<LinkedHashMap<Long, Hadith>> =
        MutableStateFlow(LinkedHashMap())

    private suspend fun populate() {
        try {
            mapStateFlow.value = jsonParser.parse<Hadith>(R.raw.ahadith)
                .associateByTo(LinkedHashMap()) { hadith -> hadith.id }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    suspend fun get(id: Long): Flow<Hadith> = withContext(Dispatchers.IO) {
        if (mapStateFlow.value.isEmpty()) {
            populate()
        }

        return@withContext mapStateFlow.mapNotNull { map -> map[id] }
    }
}