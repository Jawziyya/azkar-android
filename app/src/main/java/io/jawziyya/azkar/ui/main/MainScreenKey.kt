package io.jawziyya.azkar.ui.main

import androidx.fragment.app.Fragment
import io.jawziyya.azkar.ui.core.navigation.FragmentKey
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.lookup
import kotlinx.parcelize.Parcelize

/**
 * Created by uvays on 03.02.2023.
 */

@Parcelize
data class MainScreenKey(val placeholder: String = "") : FragmentKey() {
    override fun instantiateFragment(): Fragment = MainFragment()
    override fun bindServices(serviceBinder: ServiceBinder) = with(serviceBinder) {
        add(
            MainViewModel(
                screenKey = this@MainScreenKey,
                backstack = backstack,
                fudulRepository = lookup(),
            )
        )
    }
}
