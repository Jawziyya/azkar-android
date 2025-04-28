package io.jawziyya.azkar.ui.azkarlist

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import io.jawziyya.azkar.database.model.Azkar
import io.jawziyya.azkar.database.model.AzkarCategory
import io.jawziyya.azkar.ui.azkarpager.AzkarPagerScreen
import io.jawziyya.azkar.ui.core.rippleClickable
import io.jawziyya.azkar.ui.theme.AppTheme
import io.jawziyya.azkar.ui.theme.components.AppBar
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Created by uvays on 07.06.2022.
 */

@Serializable
data class AzkarListScreen(
    val categoryName: String,
)

@Composable
fun AzkarListScreenView(
    navController: NavHostController,
    navBackStackEntry: NavBackStackEntry,
) {
    val args = navBackStackEntry.toRoute<AzkarListScreen>()

    AzkarListView(
        category = AzkarCategory.valueOf(args.categoryName),
        onBackClick = navController::popBackStack,
        onItemClick = remember {
            { index, azkar ->
                navController.navigate(
                    AzkarPagerScreen(
                        categoryName = azkar.category.name,
                        index = index,
                    ),
                )
            }
        },
    )
}

@Composable
fun AzkarListView(
    category: AzkarCategory,
    viewModel: AzkarListViewModel = koinViewModel(parameters = { parametersOf(category) }),
    onBackClick: () -> Unit,
    onItemClick: (Int, Azkar) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background),
    ) {
        AppBar(
            title = stringResource(category.titleRes),
            onBackClick = onBackClick,
        )

        val items by viewModel.azkarListFlow.collectAsState()

        Crossfade(targetState = items.isEmpty(), label = "") { empty ->
            if (empty) {
                Box(modifier = Modifier.fillMaxSize())
                return@Crossfade
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = WindowInsets.navigationBars.asPaddingValues(),
            ) {
                itemsIndexed(items) { index, item ->
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .rippleClickable { onItemClick(index, item) }
                            .padding(16.dp),
                        text = item.title,
                        style = AppTheme.typography.title,
                        color = AppTheme.colors.text,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun AzkarScreenPreview() {
    AzkarListView(
        category = AzkarCategory.AfterSalah,
        onItemClick = { _, _ -> },
        onBackClick = {},
    )
}