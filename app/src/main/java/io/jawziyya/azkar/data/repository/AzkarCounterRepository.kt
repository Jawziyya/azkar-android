package io.jawziyya.azkar.data.repository

import io.jawziyya.azkar.database.model.Azkar
import kotlinx.coroutines.flow.MutableStateFlow

class AzkarCounterRepository {

    val stateFlow: MutableStateFlow<HashMap<Long, Int>> = MutableStateFlow(HashMap())

    fun onEvent(azkar: Azkar) {
        val map = HashMap(stateFlow.value)
        val oldValue = map[azkar.azkarCategoryId] ?: 0
        map[azkar.azkarCategoryId] = (oldValue + 1).coerceAtMost(azkar.repeats)
        stateFlow.value = map
    }

    fun reset() {
        stateFlow.value = HashMap()
    }
}