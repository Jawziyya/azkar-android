package io.jawziyya.azkar.data.repository

import io.jawziyya.azkar.R
import io.jawziyya.azkar.data.helper.JsonParser
import io.jawziyya.azkar.data.model.Fadail
import io.jawziyya.azkar.ui.core.intervalFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * Created by uvays on 23.02.2023.
 */

class FadailRepository(private val jsonParser: JsonParser) {

    private val random = Random(System.currentTimeMillis())

    private val mapStateFlow: MutableStateFlow<LinkedHashMap<Long, Fadail>> =
        MutableStateFlow(LinkedHashMap())

    private suspend fun populate() {
        try {
            mapStateFlow.value = jsonParser.parse<Fadail>(R.raw.fadail)
                .associateByTo(LinkedHashMap()) { fudul -> fudul.id }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    suspend fun getRandom(): Flow<Fadail> = withContext(Dispatchers.IO) {
        if (mapStateFlow.value.isEmpty()) {
            populate()
        }

        return@withContext intervalFlow(0L, TimeUnit.SECONDS.toMillis(30))
            .combine(mapStateFlow) { _, mapFlow -> mapFlow }
            .mapNotNull { map -> map[map.keys.random(random)] }

    }
}