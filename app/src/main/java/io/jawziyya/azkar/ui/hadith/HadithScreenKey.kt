package io.jawziyya.azkar.ui.hadith

import androidx.fragment.app.Fragment
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.lookup
import io.jawziyya.azkar.ui.core.navigation.FragmentKey
import kotlinx.parcelize.Parcelize

/**
 * Created by uvays on 23.02.2023.
 */

@Parcelize
data class HadithScreenKey(val id: Long, val title: String) : FragmentKey() {
    override fun instantiateFragment(): Fragment = HadithFragment()
    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            add(
                HadithViewModel(
                    screenKey = this@HadithScreenKey,
                    backstack = backstack,
                    hadithRepository = lookup(),
                )
            )
        }
    }
}
