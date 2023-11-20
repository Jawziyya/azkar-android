package io.jawziyya.azkar.ui.settings

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.edit
import com.zhuinden.simplestackcomposeintegration.core.LocalBackstack
import com.zhuinden.simplestackextensions.servicesktx.lookup
import io.jawziyya.azkar.R
import io.jawziyya.azkar.data.helper.observeKey
import io.jawziyya.azkar.ui.components.RadioGroup
import io.jawziyya.azkar.ui.core.navigation.ComposeKey
import io.jawziyya.azkar.ui.theme.AppTheme
import io.jawziyya.azkar.ui.theme.component.AppBar
import kotlinx.coroutines.flow.map
import kotlinx.parcelize.Parcelize

@Parcelize
data class ThemeSettingsScreenKey(
    val sharedPreferencesKey: String,
    val title: String,
    val titles: List<String>,
    val values: List<String>,
    val defaultValueIndex: Int,
) : ComposeKey() {

    operator fun invoke() = this

    @Composable
    override fun ScreenComposable(modifier: Modifier) {
        val backstack = LocalBackstack.current
        val sharedPreferences = remember { backstack.lookup<SharedPreferences>() }

        val optionsFlow = remember {
            sharedPreferences
                .observeKey(sharedPreferencesKey, values[defaultValueIndex])
                .map { selectedValue ->
                    values
                        .mapIndexed { index, value -> Pair(titles[index], value == selectedValue) }
                        .toTypedArray()
                }
        }

        val options by optionsFlow.collectAsState(emptyArray())

        SettingsDetailScreen(
            onBackClick = backstack::goBack,
            title = title,
            options = options,
            onOptionClick = remember {
                { index ->
                    sharedPreferences.edit { putString(sharedPreferencesKey, values[index]) }
                }
            },
        )
    }
}

@Composable
fun SettingsDetailScreen(
    onBackClick: () -> Unit,
    title: String,
    options: Array<Pair<String, Boolean>>,
    onOptionClick: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        AppBar(
            title = title,
            onBackClick = onBackClick,
        )

        RadioGroup(
            radioButtons = options,
            onClick = onOptionClick,
        )
    }
}

@Preview
@Composable
private fun SettingsDetailScreenPreview() {
    val resources = LocalContext.current.resources

    val options = remember {
        DarkThemeOption.values()
            .map { option -> resources.getString(option.title) to false }
            .toTypedArray()
    }

    SettingsDetailScreen(
        onBackClick = {},
        title = stringResource(R.string.settings_type_dark_theme),
        options = options,
        onOptionClick = {},
    )
}