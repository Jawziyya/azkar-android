package io.jawziyya.azkar.ui

import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.navigation.BottomSheetNavigator
import androidx.compose.material.navigation.ModalBottomSheetLayout
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.graphics.drawable.toDrawable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import io.jawziyya.azkar.data.helper.observeKey
import io.jawziyya.azkar.ui.azkarlist.AzkarListScreen
import io.jawziyya.azkar.ui.azkarlist.AzkarListScreenView
import io.jawziyya.azkar.ui.azkarpager.AzkarPagerScreen
import io.jawziyya.azkar.ui.azkarpager.AzkarPagerScreenView
import io.jawziyya.azkar.ui.core.Settings
import io.jawziyya.azkar.ui.core.findWindow
import io.jawziyya.azkar.ui.hadith.HadithScreen
import io.jawziyya.azkar.ui.hadith.HadithScreenView
import io.jawziyya.azkar.ui.main.MainScreen
import io.jawziyya.azkar.ui.main.MainScreenView
import io.jawziyya.azkar.ui.settings.DarkThemeOption
import io.jawziyya.azkar.ui.settings.SettingsDetailScreen
import io.jawziyya.azkar.ui.settings.SettingsDetailScreenView
import io.jawziyya.azkar.ui.settings.SettingsScreen
import io.jawziyya.azkar.ui.settings.SettingsScreenView
import io.jawziyya.azkar.ui.settings.reminder.ReminderSettingsScreen
import io.jawziyya.azkar.ui.settings.reminder.ReminderSettingsScreenView
import io.jawziyya.azkar.ui.theme.AppTheme
import kotlinx.coroutines.flow.map
import org.koin.compose.koinInject

@Composable
fun ComposableApp() {
    val window = findWindow()
    val sharedPreferences: SharedPreferences = koinInject()
    val systemDarkTheme = isSystemInDarkTheme()
    val darkThemeFlow = remember(systemDarkTheme) {
        sharedPreferences.observeKey(Settings.darkThemeKey, DarkThemeOption.SYSTEM.name)
            .map { name -> DarkThemeOption.valueOf(name) }.map { selectedOption ->
                return@map when (selectedOption) {
                    DarkThemeOption.DISABLED -> false
                    DarkThemeOption.ENABLED -> true
                    DarkThemeOption.SYSTEM -> systemDarkTheme
                }
            }
    }
    val darkTheme by darkThemeFlow.collectAsState(systemDarkTheme)

    LaunchedEffect(darkTheme) {
        val color = if (darkTheme) Color.BLACK else Color.WHITE
        window?.setBackgroundDrawable(color.toDrawable())
    }

    AppTheme(darkTheme = darkTheme) {
        val sheetState = rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true,
        )

        val bottomSheetNavigator = remember { BottomSheetNavigator(sheetState) }
        val navController = rememberNavController(bottomSheetNavigator)
        val graph = remember(navController) {
            navController.createGraph(startDestination = MainScreen) {
                composable<MainScreen> { navBackStackEntry ->
                    MainScreenView(
                        navController = navController,
                        navBackStackEntry = navBackStackEntry,
                    )
                }
                composable<AzkarListScreen> { navBackStackEntry ->
                    AzkarListScreenView(
                        navController = navController,
                        navBackStackEntry = navBackStackEntry,
                    )
                }

                composable<AzkarPagerScreen> { navBackStackEntry ->
                    AzkarPagerScreenView(
                        navController = navController,
                        navBackStackEntry = navBackStackEntry,
                    )
                }

                composable<HadithScreen> { navBackStackEntry ->
                    HadithScreenView(
                        navController = navController,
                        navBackStackEntry = navBackStackEntry,
                    )
                }

                composable<SettingsScreen> { navBackStackEntry ->
                    SettingsScreenView(
                        navController = navController,
                        navBackStackEntry = navBackStackEntry,
                    )
                }

                composable<SettingsDetailScreen> { navBackStackEntry ->
                    SettingsDetailScreenView(
                        navController = navController,
                        navBackStackEntry = navBackStackEntry,
                    )
                }

                composable<ReminderSettingsScreen> { navBackStackEntry ->
                    ReminderSettingsScreenView(
                        navController = navController,
                        navBackStackEntry = navBackStackEntry,
                    )
                }
            }
        }

        ModalBottomSheetLayout(
            bottomSheetNavigator = bottomSheetNavigator,
        ) {
            NavHost(
                navController = navController,
                graph = graph,
                enterTransition = { fadeIn(animationSpec = tween(250)) },
                exitTransition = { fadeOut(animationSpec = tween(250)) },
            )
        }
    }
}