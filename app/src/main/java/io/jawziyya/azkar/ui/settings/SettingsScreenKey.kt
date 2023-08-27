package io.jawziyya.azkar.ui.settings

import android.app.Application
import androidx.fragment.app.Fragment
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.lookup
import io.jawziyya.azkar.ui.core.navigation.FragmentKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class SettingsScreenKey(val placeholder: String = "") : FragmentKey() {
    override fun instantiateFragment(): Fragment = SettingsFragment()
    override fun bindServices(serviceBinder: ServiceBinder) {
        super.bindServices(serviceBinder)
        with(serviceBinder) {
            val application = lookup<Application>()

            add(
                SettingsViewModel(
                    screenKey = this@SettingsScreenKey,
                    backstack = backstack,
                    resources = lookup(),
                    application = application,
                    sharedPreferences = lookup(),
                )
            )
        }
    }
}
