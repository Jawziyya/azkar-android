package io.jawziyya.azkar.ui.azkarlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.zhuinden.simplestackcomposeintegration.core.LocalBackstack
import com.zhuinden.simplestackcomposeintegration.services.rememberService
import io.jawziyya.azkar.database.DatabaseHelper
import io.jawziyya.azkar.database.model.Azkar
import io.jawziyya.azkar.database.model.AzkarCategory
import io.jawziyya.azkar.ui.azkarpager.AzkarPagerScreenKey
import io.jawziyya.azkar.ui.core.navigation.ComposeKey
import kotlinx.parcelize.Parcelize

/**
 * Created by uvays on 03.02.2023.
 */

@Parcelize
data class AzkarListScreenKey(private val azkarCategoryName: String) : ComposeKey() {
    constructor(azkarCategory: AzkarCategory) : this(azkarCategoryName = azkarCategory.name)

    val azkarCategory: AzkarCategory get() = AzkarCategory.valueOf(azkarCategoryName)

    @Composable
    override fun ScreenComposable(modifier: Modifier) {
        val backstack = LocalBackstack.current
        val databaseHelper = rememberService<DatabaseHelper>()
        var azkarList by remember { mutableStateOf(emptyList<Azkar>()) }

        LaunchedEffect(Unit) {
            azkarList = databaseHelper.getAzkarList(azkarCategory)
        }

        AzkarListScreen(
            onBackClick = backstack::goBack,
            title = stringResource(azkarCategory.titleRes),
            items = azkarList,
            onItemClick = remember {
                { index, azkar ->
                    backstack.goTo(
                        AzkarPagerScreenKey(
                            azkarCategory = azkar.category,
                            azkarIndex = index,
                        )
                    )
                }
            },
        )
    }
}
