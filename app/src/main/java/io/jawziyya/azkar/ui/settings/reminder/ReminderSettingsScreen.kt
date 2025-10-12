@file:OptIn(ExperimentalPermissionsApi::class)

package io.jawziyya.azkar.ui.settings.reminder

import android.content.Intent
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.commandiron.wheel_picker_compose.WheelTimePicker
import com.commandiron.wheel_picker_compose.core.WheelPickerDefaults
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import io.jawziyya.azkar.R
import io.jawziyya.azkar.ui.core.rippleClickable
import io.jawziyya.azkar.ui.theme.AppTheme
import io.jawziyya.azkar.ui.theme.components.AppBar
import io.jawziyya.azkar.ui.theme.components.AppSwitch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Serializable
data object ReminderSettingsScreen

@Composable
fun ReminderSettingsScreenView(
    navController: NavHostController,
    navBackStackEntry: NavBackStackEntry,
) {
    val viewModel: ReminderSettingsViewModel = koinViewModel()

    val enabled by viewModel.enabledFlow.collectAsState(false)
    val dailyEnabled by viewModel.dailyEnabledFlow.collectAsState(false)
    val morningTime by viewModel.morningTimeFlow.collectAsState(LocalTime.ofSecondOfDay(0))
    val eveningTime by viewModel.eveningTimeFlow.collectAsState(LocalTime.ofSecondOfDay(0))
    val djumaEnabled by viewModel.djumaEnabledFlow.collectAsState(false)
    val djumaTime by viewModel.djumaTimeFlow.collectAsState(LocalTime.ofSecondOfDay(0))

    HandleCommand(
        launchPermissionSettingsFlow = viewModel.launchPermissionSettingsFlow,
    )

    View(
        onBackClick = { navController.popBackStack() },
        enabled = enabled,
        onEnabledChange = viewModel::onEnabledChange,
        dailyEnabled = dailyEnabled,
        onDailyEnabledChange = viewModel::onDailyEnabledChange,
        morningTime = morningTime,
        onMorningTimeChange = viewModel::onMorningTimeChange,
        eveningTime = eveningTime,
        onEveningTimeChange = viewModel::onEveningTimeChange,
        djumaEnabled = djumaEnabled,
        onDjumaEnabledChange = viewModel::onDjumaEnabledChange,
        djumaTime = djumaTime,
        onDjumaTimeChange = viewModel::onDjumaTimeChange,
    )
}

@Composable
private fun HandleCommand(launchPermissionSettingsFlow: Flow<Unit>) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            launchPermissionSettingsFlow
                .onEach {
                    context.startActivity(
                        Intent().apply {
                            action = android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                            data = "package:${context.packageName}".toUri()
                        }
                    )
                }
                .collect()
        }
    }
}

@Composable
private fun View(
    onBackClick: () -> Unit,
    enabled: Boolean,
    onEnabledChange: () -> Unit,
    dailyEnabled: Boolean,
    onDailyEnabledChange: () -> Unit,
    morningTime: LocalTime,
    onMorningTimeChange: (LocalTime) -> Unit,
    eveningTime: LocalTime,
    onEveningTimeChange: (LocalTime) -> Unit,
    djumaEnabled: Boolean,
    onDjumaEnabledChange: () -> Unit,
    djumaTime: LocalTime,
    onDjumaTimeChange: (LocalTime) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        AppBar(
            title = stringResource(R.string.settings_type_reminder),
            onBackClick = onBackClick,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ListItem {
                AppSwitch(
                    modifier = Modifier.fillMaxWidth(),
                    padding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    text = "Уведомления",
                    checked = enabled,
                    onCheckedChange = onEnabledChange,
                )
            }

            AnimatedVisibility(enabled) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    ListItem {
                        AppSwitch(
                            modifier = Modifier.fillMaxWidth(),
                            padding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            text = "Напоминать об утренних и вечерних азкарах",
                            checked = dailyEnabled,
                            onCheckedChange = onDailyEnabledChange,
                        )
                        AnimatedVisibility(dailyEnabled) {
                            Column(
                                modifier = Modifier.padding(bottom = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                HorizontalDivider(Modifier.padding(horizontal = 16.dp))

                                TimeItem(
                                    text = "Утренние",
                                    time = morningTime,
                                    onTimeChange = onMorningTimeChange,
                                )

                                HorizontalDivider(Modifier.padding(horizontal = 16.dp))

                                TimeItem(
                                    text = "Вечерние",
                                    time = eveningTime,
                                    onTimeChange = onEveningTimeChange,
                                )
                            }
                        }
                    }

                    ListItem {
                        AppSwitch(
                            modifier = Modifier.fillMaxWidth(),
                            padding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            text = "Напоминать о дуа в день джума",
                            checked = djumaEnabled,
                            onCheckedChange = onDjumaEnabledChange,
                        )
                        AnimatedVisibility(djumaEnabled) {
                            Column(
                                modifier = Modifier.padding(bottom = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                HorizontalDivider(Modifier.padding(horizontal = 16.dp))

                                TimeItem(
                                    text = "Время",
                                    time = djumaTime,
                                    onTimeChange = onDjumaTimeChange,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ListItem(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AppTheme.colors.contentBackground),
    ) {
        content()
    }
}

@Composable
private fun TimeItem(
    modifier: Modifier = Modifier,
    text: String,
    time: LocalTime,
    onTimeChange: (LocalTime) -> Unit,
) {
    var picker by remember { mutableStateOf(false) }
    val format = remember { DateTimeFormatter.ofPattern("HH:mm"); }
    val timeText = remember(time) { time.format(format) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 0.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = text,
            style = AppTheme.typography.subtitle,
            color = AppTheme.colors.text,
        )
        Box {
            Text(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(AppTheme.colors.background)
                    .rippleClickable { picker = true }
                    .padding(8.dp),
                text = timeText,
                style = AppTheme.typography.subtitle.copy(
                    fontFeatureSettings = "tnum",
                ),
                color = AppTheme.colors.text,
            )

            TimePicker(
                visible = picker,
                onDismiss = { picker = false },
                value = time,
                onTimePicked = onTimeChange,
            )
        }
    }
}

@Composable
private fun TimePicker(
    visible: Boolean,
    onDismiss: () -> Unit,
    value: LocalTime,
    onTimePicked: (LocalTime) -> Unit,
) {
    var selectedValue by remember(value) { mutableStateOf(value) }

    // Material 3 doesn't support colors override in MaterialTheme
    // Using direct background color instead
    Box(
        modifier = Modifier.background(AppTheme.colors.contentBackground)
    ) {
        DropdownMenu(
            expanded = visible,
            onDismissRequest = {
                onDismiss()
                onTimePicked(selectedValue)
            },
            offset = DpOffset(x = 0.dp, y = 16.dp),
        ) {
            WheelTimePicker(
                startTime = value,
                textStyle = AppTheme.typography.title.copy(
                    fontFeatureSettings = "tnum",
                ),
                textColor = AppTheme.colors.text,
                selectorProperties = WheelPickerDefaults.selectorProperties(
                    enabled = true,
                    shape = RoundedCornerShape(0.dp),
                    color = AppTheme.colors.accent.copy(alpha = 0.4f),
                    border = BorderStroke(0.dp, Color.Unspecified),
                ),
                onSnappedTime = { value -> selectedValue = value },
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    View(
        onBackClick = {},
        enabled = true,
        onEnabledChange = {},
        dailyEnabled = true,
        onDailyEnabledChange = {},
        morningTime = LocalTime.ofSecondOfDay(18000),
        onMorningTimeChange = {},
        eveningTime = LocalTime.ofSecondOfDay(61200),
        onEveningTimeChange = {},
        djumaEnabled = true,
        onDjumaEnabledChange = {},
        djumaTime = LocalTime.ofSecondOfDay(54000),
        onDjumaTimeChange = {},
    )
}