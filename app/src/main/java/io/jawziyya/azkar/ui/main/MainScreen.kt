package io.jawziyya.azkar.ui.main

import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import io.jawziyya.azkar.R
import io.jawziyya.azkar.data.helper.MoonPhase
import io.jawziyya.azkar.database.model.AzkarCategory
import io.jawziyya.azkar.database.model.Fadail
import io.jawziyya.azkar.ui.azkarlist.AzkarListScreen
import io.jawziyya.azkar.ui.azkarpager.AzkarPagerScreen
import io.jawziyya.azkar.ui.core.rippleClickable
import io.jawziyya.azkar.ui.settings.SettingsScreen
import io.jawziyya.azkar.ui.theme.AppTheme
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import kotlin.random.Random

/**
 * Created by uvays on 05.06.2022.
 */

@Serializable
data object MainScreen

@Composable
fun MainScreenView(
    navController: NavHostController,
    navBackStackEntry: NavBackStackEntry,
) {
    val viewModel: MainViewModel = koinViewModel()
    val moonPhase by viewModel.moonPhase.collectAsState()
    val fadail by viewModel.fadailFlow.collectAsState(null)

    View(
        moonPhase = moonPhase,
        fadail = fadail,
        onAzkarCategoryClick = { category ->
            if (category.main) {
                navController.navigate(
                    AzkarPagerScreen(categoryName = category.name),
                )
            } else {
                navController.navigate(
                    AzkarListScreen(categoryName = category.name),
                )
            }
        },
        onSettingsClick = { navController.navigate(SettingsScreen) },
    )
}

@Composable
private fun View(
    moonPhase: MoonPhase,
    fadail: Fadail?,
    onAzkarCategoryClick: (AzkarCategory) -> Unit,
    onSettingsClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            val emojiArray = stringArrayResource(R.array.app_name_emoji)
            val random = remember { Random(System.currentTimeMillis()) }
            val emoji = rememberSaveable { emojiArray.random(random) }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .padding(horizontal = 16.dp),
                style = AppTheme.typography.header,
                fontSize = 24.sp,
                text = "${stringResource(R.string.app_name)} $emoji",
                maxLines = 1,
                color = AppTheme.colors.text,
            )
            DayAzkarSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp),
                moonPhase = moonPhase,
                onAzkarCategoryClick = onAzkarCategoryClick,
            )
            Column(
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppTheme.colors.contentBackground),
            ) {
                ListItem(
                    modifier = Modifier.fillMaxWidth(),
                    imageRes = R.drawable.ic_category_night,
                    title = stringResource(R.string.azkar_category_night),
                    onClick = remember {
                        { onAzkarCategoryClick(AzkarCategory.Night) }
                    },
                )
                ListItem(
                    modifier = Modifier.fillMaxWidth(),
                    imageRes = R.drawable.ic_category_after_salah,
                    title = stringResource(R.string.azkar_category_after_salah),
                    onClick = remember {
                        { onAzkarCategoryClick(AzkarCategory.AfterSalah) }
                    },
                )
                ListItem(
                    modifier = Modifier.fillMaxWidth(),
                    imageRes = R.drawable.ic_category_important,
                    title = stringResource(R.string.azkar_category_other),
                    onClick = remember {
                        { onAzkarCategoryClick(AzkarCategory.Other) }
                    },
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppTheme.colors.contentBackground),
            ) {
                ListItem(
                    modifier = Modifier.fillMaxWidth(),
                    imageRes = R.drawable.ic_settings_32,
                    colorFilter = ColorFilter.tint(AppTheme.colors.tertiaryText),
                    title = stringResource(R.string.main_settings),
                    onClick = onSettingsClick,
                )
            }

            Crossfade(
                targetState = fadail,
                animationSpec = tween(durationMillis = 600),
                label = "",
            ) { value ->
                if (value == null) return@Crossfade

                FudulSection(
                    modifier = Modifier.padding(top = 32.dp),
                    fadail = value,
                )
            }
        }
    }
}

@Composable
private fun DayAzkarSection(
    modifier: Modifier = Modifier,
    moonPhase: MoonPhase,
    onAzkarCategoryClick: (AzkarCategory) -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DayAzkarContainer(
            modifier = Modifier.weight(1f),
            title = stringResource(R.string.azkar_category_morning),
            imageContent = {
                Image(
                    modifier = Modifier.size(50.dp),
                    painter = painterResource(R.drawable.ic_category_morning),
                    contentDescription = null,
                )
            },
            onClick = remember {
                { onAzkarCategoryClick(AzkarCategory.Morning) }
            },
        )
        DayAzkarContainer(
            modifier = Modifier.weight(1f),
            title = stringResource(R.string.azkar_category_evening),
            imageContent = {
                Crossfade(moonPhase) { value ->
                    Image(
                        modifier = Modifier
                            .size(50.dp)
                            .alpha(.5f),
                        painter = painterResource(R.drawable.ic_moonphase_new_moon),
                        contentDescription = null,
                    )
                    Image(
                        modifier = Modifier.size(50.dp),
                        painter = painterResource(value.drawableRes),
                        contentDescription = null,
                    )
                }
            },
            onClick = remember {
                { onAzkarCategoryClick(AzkarCategory.Evening) }
            },
        )
    }
}

@Composable
private fun DayAzkarContainer(
    modifier: Modifier = Modifier,
    title: String,
    imageContent: @Composable BoxScope.() -> Unit,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(AppTheme.colors.contentBackground)
            .rippleClickable(onClick)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center,
        ) {
            imageContent()
        }
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            style = AppTheme.typography.subtitle,
            text = title,
            textAlign = TextAlign.Center,
            color = AppTheme.colors.text,
        )
    }
}

@Composable
private fun ListItem(
    modifier: Modifier = Modifier,
    @DrawableRes imageRes: Int,
    colorFilter: ColorFilter? = null,
    title: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .rippleClickable(onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier.size(32.dp),
            painter = painterResource(imageRes),
            contentDescription = null,
            colorFilter = colorFilter,
        )

        Text(
            modifier = Modifier.weight(1f),
            style = AppTheme.typography.subtitle,
            text = title,
            color = AppTheme.colors.text,
        )

        Image(
            painter = painterResource(R.drawable.ic_chevron_right_24),
            contentDescription = null,
            colorFilter = ColorFilter.tint(AppTheme.colors.tertiaryText),
        )
    }
}

@Composable
private fun FudulSection(
    modifier: Modifier = Modifier,
    fadail: Fadail,
) {
    val source = fadail.source?.let { source -> stringResource(source.titleRes) }
    val sourceText = remember(fadail, source) {
        listOfNotNull(source, fadail.sourceExt).joinToString(separator = ", ")
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 20.sp,
            text = fadail.text ?: "",
            textAlign = TextAlign.Center,
            color = AppTheme.colors.text,
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            fontSize = 12.sp,
            lineHeight = 16.sp,
            textAlign = TextAlign.Center,
            text = sourceText,
            color = AppTheme.colors.secondaryText,
        )
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    View(
        moonPhase = MoonPhase.WaningGibbous,
        fadail = null,
        onAzkarCategoryClick = {},
        onSettingsClick = {},
    )
}