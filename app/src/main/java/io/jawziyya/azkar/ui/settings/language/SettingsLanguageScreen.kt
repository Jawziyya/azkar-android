package io.jawziyya.azkar.ui.settings.language

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import io.jawziyya.azkar.R
import io.jawziyya.azkar.ui.settings.LanguageOption
import io.jawziyya.azkar.ui.theme.components.AppBar
import kotlinx.serialization.Serializable

@Serializable
data object SettingsLanguageScreen

@Composable
fun SettingsLanguageScreenView(
    navController: NavController,
    navBackStackEntry: NavBackStackEntry,
) {
//    val args = navBackStackEntry.toRoute<SettingsLanguageScreen>()
//    val viewModel: SettingsLanguageViewModel = koinViewModel()

}

@Composable
private fun View(
    onBackClick: () -> Unit,
    items: List<LanguageOption>,
    onItemSelected: LanguageOption,
) {
    Column {
        AppBar(
            onBackClick = onBackClick,
            title = stringResource(R.string.settings_type_language),
        )

//        RadioGroup(radioButtons = items.map { item -> item.title }.toTypedArray()) {
//
//        }
    }
}