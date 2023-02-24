package io.jawziyya.azkar.ui.hadith

import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.ScopedServices
import io.jawziyya.azkar.data.model.Hadith
import io.jawziyya.azkar.data.repository.HadithRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

/**
 * Created by uvays on 23.02.2023.
 */

class HadithViewModel(
    private val screenKey: HadithScreenKey,
    private val backstack: Backstack,
    private val hadithRepository: HadithRepository,
) : ScopedServices.Registered {
    private val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Main
    private val coroutineScope = CoroutineScope(coroutineContext)

    val hadithFlow: MutableStateFlow<Hadith?> = MutableStateFlow(null)

    override fun onServiceRegistered() {
        coroutineScope.launch {
            hadithRepository
                .get(screenKey.id)
                .catch { Timber.e(it) }
                .collect { value -> hadithFlow.value = value }
        }
    }

    override fun onServiceUnregistered() {
        coroutineContext.cancelChildren()
    }
}