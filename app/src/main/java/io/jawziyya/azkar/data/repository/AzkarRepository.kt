package io.jawziyya.azkar.data.repository

import io.jawziyya.azkar.data.datasource.AzkarDataSource
import io.jawziyya.azkar.data.mapper.AzkarResponseToModelMapper
import io.jawziyya.azkar.data.model.Zikr
import io.jawziyya.azkar.data.model.AzkarCategory
import io.jawziyya.azkar.data.network.response.AzkarResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Created by uvays on 06.06.2022.
 */

class AzkarRepository(
    private val azkarDataSource: AzkarDataSource,
    private val azkarResponseToModelMapper: AzkarResponseToModelMapper,
) {

    private val azkarMapState: MutableStateFlow<LinkedHashMap<Long, AzkarResponse>> =
        MutableStateFlow(LinkedHashMap())

    private suspend fun populate() {
        try {
            val value = azkarDataSource.get().associateByTo(LinkedHashMap()) { azkar -> azkar.id }
            azkarMapState.value = value
        } catch (e: Exception) {
            Timber.d(e)
        }
    }

    suspend fun getAzkar(
        azkarCategory: AzkarCategory
    ): Flow<List<Zikr>> = withContext(Dispatchers.IO) {
        if (azkarMapState.value.isEmpty()) {
            populate()
        }

        return@withContext azkarMapState.asStateFlow().map { map ->
                map.values
                    .asSequence()
                    .map(azkarResponseToModelMapper)
                    .filter { azkar -> azkar.category == azkarCategory }
                    .sortedBy { azkar -> azkar.order }
                    .toList()
            }
    }

    suspend fun getZikr(id: Long): Zikr? = withContext(Dispatchers.IO) {
        if (azkarMapState.value.isEmpty()) {
            populate()
        }

        val value = azkarMapState.value[id] ?: return@withContext null

        return@withContext azkarResponseToModelMapper(value)
    }
}