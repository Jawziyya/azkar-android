package io.jawziyya.azkar.ui.settings

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import io.jawziyya.azkar.R
import io.jawziyya.azkar.ui.core.Settings
import io.jawziyya.azkar.ui.core.rippleClickable
import io.jawziyya.azkar.ui.settings.reminder.ReminderSettingsScreen
import io.jawziyya.azkar.ui.theme.AppTheme
import io.jawziyya.azkar.ui.theme.components.AppBar
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data object SettingsScreen

@Composable
fun SettingsScreenView(
    navController: NavHostController,
    navBackStackEntry: NavBackStackEntry,
) {
    val resources = LocalContext.current.resources
    val viewModel: SettingsViewModel = koinViewModel()
    val items by viewModel.itemsFlow.collectAsState()

    View(
        items = items,
        onBackClick = navController::popBackStack,
        onItemClick = { type ->
            when (type) {
                SettingsType.DARK_THEME -> {
                    navController.navigate(
                        SettingsDetailScreen(
                            sharedPreferencesKey = Settings.darkThemeKey,
                            title = resources.getString(R.string.settings_type_dark_theme),
                            titles = DarkThemeOption.entries.map { value ->
                                resources.getString(value.title)
                            },
                            values = DarkThemeOption.entries.map { value -> value.name },
                            defaultValueIndex = DarkThemeOption.SYSTEM.ordinal,
                        ),
                    )
                }

                SettingsType.LANGUAGE -> {
                    navController.navigate(
                        SettingsDetailScreen(
                            sharedPreferencesKey = Settings.languageKey,
                            title = resources.getString(R.string.settings_type_language),
                            titles = LanguageOption.entries.map { value ->
                                resources.getString(value.title)
                            },
                            values = LanguageOption.entries.map { value -> value.name },
                            defaultValueIndex = LanguageOption.getFallback().ordinal,
                        ),
                    )
                }

                SettingsType.REMINDER -> {
                    navController.navigate(ReminderSettingsScreen)
                }
            }
        },
    )
}

@Composable
private fun View(
    items: List<Pair<SettingsType, String>>,
    onBackClick: () -> Unit,
    onItemClick: (SettingsType) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        AppBar(
            title = stringResource(R.string.settings_title),
            onBackClick = onBackClick,
        )

        Crossfade(targetState = items.isEmpty()) { empty ->
            if (empty) {
                Box(modifier = Modifier.fillMaxSize())
            } else {
                Content(
                    modifier = Modifier,
                    items = items,
                    onItemClick = onItemClick,
                )
            }
        }
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    items: List<Pair<SettingsType, String>>,
    onItemClick: (SettingsType) -> Unit
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
    ) {
        repeat(items.size) { index ->
            val (type, value) = items[index]

            ListItem(
                modifier = Modifier,
                type = type,
                value = value,
                onClick = onItemClick,
            )
        }
    }
}

@Composable
private fun ListItem(
    modifier: Modifier,
    type: SettingsType,
    value: String,
    onClick: (SettingsType) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .rippleClickable { onClick(type) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier,
            style = AppTheme.typography.subtitle,
            text = stringResource(type.title),
            textAlign = TextAlign.Start,
            color = AppTheme.colors.text,
        )

        Spacer(
            modifier = Modifier.weight(1f),
        )

        Text(
            modifier = Modifier,
            style = AppTheme.typography.subtitle,
            text = value,
            textAlign = TextAlign.Start,
            color = AppTheme.colors.tertiaryText,
        )

        Image(
            modifier = Modifier.padding(start = 8.dp),
            painter = painterResource(R.drawable.ic_chevron_right_24),
            contentDescription = null,
            colorFilter = ColorFilter.tint(AppTheme.colors.tertiaryText),
        )
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    View(
        items = emptyList(),
        onBackClick = {},
        onItemClick = {},
    )
}