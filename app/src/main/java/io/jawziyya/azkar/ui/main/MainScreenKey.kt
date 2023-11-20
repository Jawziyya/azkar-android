package io.jawziyya.azkar.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.zhuinden.simplestackcomposeintegration.core.LocalBackstack
import com.zhuinden.simplestackcomposeintegration.services.rememberService
import io.jawziyya.azkar.database.DatabaseHelper
import io.jawziyya.azkar.database.model.Fadail
import io.jawziyya.azkar.ui.azkarlist.AzkarListScreenKey
import io.jawziyya.azkar.ui.azkarpager.AzkarPagerScreenKey
import io.jawziyya.azkar.ui.core.intervalFlow
import io.jawziyya.azkar.ui.core.navigation.ComposeKey
import io.jawziyya.azkar.ui.settings.SettingsScreenKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * Created by uvays on 03.02.2023.
 */

@Immutable
@Parcelize
data class MainScreenKey(val placeholder: String = "") : ComposeKey() {

    @Composable
    override fun ScreenComposable(modifier: Modifier) {
        val backstack = LocalBackstack.current
        val fadail by getFadailFlow().collectAsState(null)

        MainScreen(
            onAzkarCategoryClick = remember {
                { category ->
                    if (category.main) {
                        backstack.goTo(AzkarPagerScreenKey(azkarCategory = category, azkarIndex = 0))
                    } else {
                        backstack.goTo(AzkarListScreenKey(azkarCategory = category))
                    }
                }
            },
            onSettingsClick = remember { { backstack.goTo(SettingsScreenKey()) } },
            fadail = fadail,
        )
    }

    @Composable
    private fun getFadailFlow(): Flow<Fadail> {
        val databaseHelper = rememberService<DatabaseHelper>()
        val coroutineScope = rememberCoroutineScope()

        val random = remember { Random(System.currentTimeMillis()) }
        val fadailListFlow = remember { MutableStateFlow<List<Fadail>>(emptyList()) }

        DisposableEffect(Unit) {
            val job = coroutineScope.launch {
                fadailListFlow.value = databaseHelper.getFadailList()
            }

            onDispose { job.cancel() }
        }

        return remember {
            intervalFlow(0L, TimeUnit.SECONDS.toMillis(30))
                .combine(fadailListFlow) { _, list -> list }
                .filter { list -> list.isNotEmpty() }
                .mapNotNull { list -> list.random(random) }
        }
    }
}
