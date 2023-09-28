package io.jawziyya.azkar.ui.settings.theme

import androidx.fragment.app.Fragment
import io.jawziyya.azkar.ui.core.navigation.FragmentKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class ThemeSettingsScreenKey(val placeholder: String = "") : FragmentKey() {
    override fun instantiateFragment(): Fragment = ThemeSettingsFragment()
}