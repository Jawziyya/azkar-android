package io.jawziyya.azkar.ui.settings.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.edit
import io.jawziyya.azkar.R
import io.jawziyya.azkar.data.helper.observeKey
import io.jawziyya.azkar.ui.components.RadioGroup
import io.jawziyya.azkar.ui.core.BaseFragment
import io.jawziyya.azkar.ui.core.Settings
import io.jawziyya.azkar.ui.theme.AppTheme
import io.jawziyya.azkar.ui.theme.component.AppBar
import kotlinx.coroutines.flow.map

class ThemeSettingsFragment : BaseFragment() {

    private val optionsFlow by lazy {
        sharedPreferences
            .observeKey(Settings.darkThemeKey, DarkThemeOption.SYSTEM.name)
            .map { name -> DarkThemeOption.valueOf(name) }
            .map { selectedOption ->
                DarkThemeOption.values()
                    .map { option -> Pair(option, option == selectedOption) }
                    .toTypedArray()
            }
    }

    @Composable
    override fun Content() {
        val options by optionsFlow.collectAsState(emptyArray())

        SettingsDetailScreen(
            onBackClick = requireActivity()::onBackPressed,
            options = options,
            onOptionClick = ::onOptionClick,
        )
    }

    private fun onOptionClick(option: DarkThemeOption) {
        sharedPreferences.edit { putString(Settings.darkThemeKey, option.name) }
    }
}

@Composable
fun SettingsDetailScreen(
    onBackClick: () -> Unit,
    options: Array<Pair<DarkThemeOption, Boolean>>,
    onOptionClick: (DarkThemeOption) -> Unit,
) {
    Column(
        modifier = Modifier.background(AppTheme.colors.background)
    ) {
        AppBar(
            title = stringResource(R.string.theme_settings_title),
            onBackClick = onBackClick,
        )

        val resources = LocalContext.current.resources
        val radioButtons = remember(options) {
            options
                .map { (option, value) -> resources.getString(option.title) to value }
                .toTypedArray()
        }

        RadioGroup(
            radioButtons = radioButtons,
            onClick = { index -> onOptionClick(options[index].first) },
        )
    }
}

@Preview
@Composable
private fun SettingsDetailScreenPreview() {
    SettingsDetailScreen(
        onBackClick = {},
        options = DarkThemeOption.values().map { option -> option to false }.toTypedArray(),
        onOptionClick = {},
    )
}