package io.jawziyya.azkar.data.repository

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import io.jawziyya.azkar.data.helper.MoonPhase
import io.jawziyya.azkar.data.helper.MoonPhaseCalculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Calendar

class MoonPhaseRepository(private val coroutineScope: CoroutineScope) : LifecycleEventObserver {

    private val internalStateFlow: MutableStateFlow<MoonPhase> = MutableStateFlow(MoonPhase.NewMoon)
    val stateFlow: StateFlow<MoonPhase> get() = internalStateFlow.asStateFlow()

    fun reset() {
        onStart()
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        Timber.d("onStateChanged, event=$event")

        when (event) {
            Lifecycle.Event.ON_START -> onStart()
            else -> {}
        }
    }

    private fun onStart() {
        coroutineScope.launch(Dispatchers.Default) {
            internalStateFlow.update { MoonPhaseCalculator(Calendar.getInstance()) }
        }
    }
}