package io.jawziyya.azkar.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import io.jawziyya.azkar.R
import io.jawziyya.azkar.ui.theme.components.RadioGroup
import io.jawziyya.azkar.ui.theme.AppTheme
import io.jawziyya.azkar.ui.theme.components.AppBar
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data class SettingsDetailScreen(
    val sharedPreferencesKey: String,
    val title: String,
    val titles: List<String>,
    val values: List<String>,
    val defaultValueIndex: Int,
)

@Composable
fun SettingsDetailScreenView(
    navController: NavHostController,
    navBackStackEntry: NavBackStackEntry,
) {
    val args = navBackStackEntry.toRoute<SettingsDetailScreen>()

    val viewModel: SettingsDetailViewModel = koinViewModel(
        parameters = {
            parametersOf(
                args.sharedPreferencesKey,
                args.titles,
                args.values,
                args.defaultValueIndex,
            )
        },
    )
    val options by viewModel.optionsFlow.collectAsState(emptyArray())

    View(
        title = args.title,
        onBackClick = { navController.popBackStack() },
        options = options,
        onOptionClick = viewModel::onOptionClick,
    )
}

@Composable
private fun View(
    title: String,
    onBackClick: () -> Unit,
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
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding(),
            radioButtons = options,
            onClick = onOptionClick,
        )
    }
}

@Preview
@Composable
private fun SettingsDetailScreenPreview() {
    val resources = LocalContext.current.resources

    View(
        onBackClick = {},
        title = stringResource(R.string.settings_type_dark_theme),
        options = emptyArray(),
        onOptionClick = { },
    )
}