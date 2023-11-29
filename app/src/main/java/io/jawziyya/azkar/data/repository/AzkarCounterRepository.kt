package io.jawziyya.azkar.data.repository

import com.zhuinden.simplestack.ScopedServices
import io.jawziyya.azkar.database.model.Azkar
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Calendar

class AzkarCounterRepository : ScopedServices.Activated {

    val stateFlow: MutableStateFlow<CounterData> = MutableStateFlow(
        CounterData(
            timestamp = System.currentTimeMillis(),
            map = HashMap(),
        )
    )

    fun onEvent(azkar: Azkar) {
        val map = HashMap(stateFlow.value.map)
        val oldValue = map[azkar.azkarCategoryId] ?: 0
        map[azkar.azkarCategoryId] = (oldValue + 1).coerceAtMost(azkar.repeats)
        stateFlow.value = stateFlow.value.copy(map = map)
    }

    fun reset() {
        stateFlow.value = CounterData(
            timestamp = System.currentTimeMillis(),
            map = HashMap(),
        )
    }

    override fun onServiceActive() {
        val calendar = Calendar.getInstance().apply { timeInMillis = stateFlow.value.timestamp }
        val calendarNow = Calendar.getInstance()

        when {
            calendar.get(Calendar.YEAR) != calendarNow.get(Calendar.YEAR) -> reset()
            calendar.get(Calendar.DAY_OF_YEAR) != calendarNow.get(Calendar.DAY_OF_YEAR) -> reset()
        }
    }

    override fun onServiceInactive() {}
}