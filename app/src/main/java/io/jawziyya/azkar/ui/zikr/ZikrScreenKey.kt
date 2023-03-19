package io.jawziyya.azkar.ui.zikr

import android.app.Application
import androidx.fragment.app.Fragment
import io.jawziyya.azkar.data.model.AzkarCategory
import io.jawziyya.azkar.ui.core.SimpleMediaPlayer
import io.jawziyya.azkar.ui.core.navigation.FragmentKey
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.lookup
import kotlinx.parcelize.Parcelize

/**
 * Created by uvays on 04.02.2023.
 */

@Parcelize
data class ZikrScreenKey(
    val azkarCategory: AzkarCategory,
    val azkarIndex: Int,
) : FragmentKey() {
    override fun instantiateFragment(): Fragment = ZikrFragment()
    override fun bindServices(serviceBinder: ServiceBinder) {
        super.bindServices(serviceBinder)
        with(serviceBinder) {
            val application = lookup<Application>()

            add(
                ZikrViewModel(
                    screenKey = this@ZikrScreenKey,
                    backstack = backstack,
                    application = application,
                    azkarRepository = lookup(),
                    fileRepository = lookup(),
                    simpleMediaPlayer = SimpleMediaPlayer(application),
                    sharedPreferences = lookup(),
                )
            )
        }
    }
}
