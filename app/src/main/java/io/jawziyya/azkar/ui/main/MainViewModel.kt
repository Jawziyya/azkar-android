package io.jawziyya.azkar.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.jawziyya.azkar.data.helper.MoonPhase
import io.jawziyya.azkar.data.repository.MoonPhaseRepository
import io.jawziyya.azkar.database.DatabaseHelper
import io.jawziyya.azkar.database.model.Fadail
import io.jawziyya.azkar.ui.core.intervalFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class MainViewModel(
    private val databaseHelper: DatabaseHelper,
    private val moonPhaseRepository: MoonPhaseRepository,
) : ViewModel() {

    private val random = Random(System.currentTimeMillis())
    private val fadailListFlow: MutableStateFlow<List<Fadail>> = MutableStateFlow(emptyList())

    private val _fadailFlow: MutableStateFlow<Fadail?> = MutableStateFlow(null)
    val fadailFlow: Flow<Fadail?> get() = _fadailFlow.asStateFlow()

    val moonPhase: StateFlow<MoonPhase> get() = moonPhaseRepository.stateFlow

    init {
        viewModelScope.launch(Dispatchers.IO) {
            fadailListFlow.value = databaseHelper.getFadailList()

            intervalFlow(0L, TimeUnit.SECONDS.toMillis(30))
                .combine(fadailListFlow) { _, list -> list }
                .filter { list -> list.isNotEmpty() }
                .mapNotNull { list -> list.random(random) }
                .collect { value -> _fadailFlow.value = value }
        }
    }
}