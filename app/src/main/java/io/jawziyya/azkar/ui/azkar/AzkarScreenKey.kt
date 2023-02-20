package io.jawziyya.azkar.ui.azkar

import androidx.fragment.app.Fragment
import io.jawziyya.azkar.data.model.AzkarCategory
import io.jawziyya.azkar.ui.core.navigation.FragmentKey
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.lookup
import kotlinx.parcelize.Parcelize

/**
 * Created by uvays on 03.02.2023.
 */

@Parcelize
data class AzkarScreenKey(val azkarCategory: AzkarCategory) : FragmentKey() {
    override fun instantiateFragment(): Fragment = AzkarFragment()
    override fun bindServices(serviceBinder: ServiceBinder) {
        super.bindServices(serviceBinder)
        with(serviceBinder) {
            add(
                AzkarViewModel(
                    screenKey = this@AzkarScreenKey,
                    backstack = backstack,
                    resources = lookup(),
                    azkarRepository = lookup(),
                )
            )
        }
    }
}
