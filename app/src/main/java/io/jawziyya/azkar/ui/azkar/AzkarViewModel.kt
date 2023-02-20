package io.jawziyya.azkar.ui.azkar

import android.content.res.Resources
import androidx.lifecycle.MutableLiveData
import io.jawziyya.azkar.data.model.Zikr
import io.jawziyya.azkar.data.model.AzkarCategory
import io.jawziyya.azkar.data.repository.AzkarRepository
import io.jawziyya.azkar.ui.zikr.ZikrScreenKey
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.ScopedServices
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

/**
 * Created by uvays on 07.06.2022.
 */

class AzkarViewModel(
    private val screenKey: AzkarScreenKey,
    private val backstack: Backstack,
    private val resources: Resources,
    private val azkarRepository: AzkarRepository,
) : ScopedServices.Registered {

    private val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Main
    private val coroutineScope = CoroutineScope(coroutineContext)

    val titleLiveData: MutableLiveData<String> =
        MutableLiveData(resources.getString(screenKey.azkarCategory.titleRes))
    val zikrListLiveData: MutableLiveData<List<Zikr>> = MutableLiveData()

    override fun onServiceRegistered() {
        coroutineScope.launch {
            azkarRepository
                .getAzkar(screenKey.azkarCategory)
                .catch { Timber.e(it) }
                .collect { value -> zikrListLiveData.value = value }
        }
    }

    override fun onServiceUnregistered() {
        coroutineContext.cancelChildren()
    }

    fun onZikrClick(index: Int, zikr: Zikr) {
        val screenKey = ZikrScreenKey(
            azkarCategory = zikr.category ?: AzkarCategory.OTHER,
            azkarIndex = index,
        )

        backstack.goTo(screenKey)
    }
}