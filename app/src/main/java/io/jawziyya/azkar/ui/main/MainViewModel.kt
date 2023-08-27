package io.jawziyya.azkar.ui.main

import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.ScopedServices
import io.jawziyya.azkar.data.model.AzkarCategory
import io.jawziyya.azkar.data.model.Fadail
import io.jawziyya.azkar.data.repository.FadailRepository
import io.jawziyya.azkar.ui.azkar.AzkarScreenKey
import io.jawziyya.azkar.ui.settings.SettingsScreenKey
import io.jawziyya.azkar.ui.zikr.ZikrScreenKey
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

/**
 * Created by uvays on 06.06.2022.
 */

class MainViewModel(
    private val screenKey: MainScreenKey,
    private val backstack: Backstack,
    private val fadailRepository: FadailRepository,
) : ScopedServices.Registered {

    private val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Main
    private val coroutineScope = CoroutineScope(coroutineContext)

    val fadailFlow: MutableStateFlow<Fadail?> = MutableStateFlow(null)

    override fun onServiceRegistered() {
        coroutineScope.launch {
            fadailRepository
                .getRandom()
                .catch { Timber.e(it) }
                .collect { value -> fadailFlow.value = value }
        }
    }

    override fun onServiceUnregistered() {
        coroutineContext.cancelChildren()
    }

    fun onAzkarCategoryClick(azkarCategory: AzkarCategory) {
        if (azkarCategory.main) {
            backstack.goTo(ZikrScreenKey(azkarCategory = azkarCategory, azkarIndex = 0))
        } else {
            backstack.goTo(AzkarScreenKey(azkarCategory = azkarCategory))
        }
    }

    fun onSettingsClick() {
        backstack.goTo(SettingsScreenKey())
    }
}