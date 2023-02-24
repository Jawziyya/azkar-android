package io.jawziyya.azkar.ui.main

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsWithImePadding
import io.jawziyya.azkar.R
import io.jawziyya.azkar.data.model.AzkarCategory
import io.jawziyya.azkar.data.model.Fudul
import io.jawziyya.azkar.ui.core.rippleClickable
import io.jawziyya.azkar.ui.theme.AppTheme
import io.jawziyya.azkar.ui.theme.colorSystemTeal
import io.jawziyya.azkar.ui.theme.component.AppBar

/**
 * Created by uvays on 05.06.2022.
 */

@Composable
fun MainScreen(
    onAzkarCategoryClick: (AzkarCategory) -> Unit,
    fudul: Fudul?,
) {
    AppTheme {
        ProvideWindowInsets {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppTheme.colors.background),
            ) {
                AppBar(
                    title = stringResource(R.string.app_name),
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(state = rememberScrollState())
                        .navigationBarsWithImePadding(),
                ) {
                    DayAzkarSection(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 16.dp, end = 16.dp),
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
                            imageRes = R.drawable.ic_mosque_32,
                            title = stringResource(R.string.azkar_category_after_salah),
                            onClick = remember {
                                { onAzkarCategoryClick(AzkarCategory.AFTER_SALAH) }
                            },
                        )
                        ListItem(
                            modifier = Modifier.fillMaxWidth(),
                            imageRes = R.drawable.ic_round_layers_32,
                            colorFilter = ColorFilter.tint(colorSystemTeal),
                            title = stringResource(R.string.azkar_category_other),
                            onClick = remember {
                                { onAzkarCategoryClick(AzkarCategory.OTHER) }
                            },
                        )
                    }

                    Text(
                        modifier = Modifier.weight(1f),
                        style = AppTheme.typography.subtitle,
                        text = "",
                    )
                }
            }
        }
    }
}

@Composable
private fun DayAzkarSection(
    modifier: Modifier = Modifier,
    onAzkarCategoryClick: (AzkarCategory) -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DayAzkarItem(
            modifier = Modifier.weight(1f),
            lottieRes = R.raw.sun,
            title = stringResource(R.string.azkar_category_morning),
            onClick = remember {
                { onAzkarCategoryClick(AzkarCategory.MORNING) }
            },
        )
        DayAzkarItem(
            modifier = Modifier.weight(1f),
            lottieRes = R.raw.moon2,
            title = stringResource(R.string.azkar_category_evening),
            onClick = remember {
                { onAzkarCategoryClick(AzkarCategory.EVENING) }
            },
        )
    }
}

@Composable
private fun DayAzkarItem(
    modifier: Modifier = Modifier, @RawRes lottieRes: Int, title: String, onClick: () -> Unit
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottieRes))

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(AppTheme.colors.contentBackground)
            .rippleClickable(onClick)
            .padding(8.dp)
    ) {
        LottieAnimation(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.CenterHorizontally),
            composition = composition,
            iterations = LottieConstants.IterateForever,
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            style = AppTheme.typography.subtitle,
            text = title,
            textAlign = TextAlign.Center,
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
            painter = painterResource(imageRes),
            contentDescription = null,
            colorFilter = colorFilter,
        )
        Text(
            modifier = Modifier.weight(1f),
            style = AppTheme.typography.subtitle,
            text = title,
        )
        Image(
            painter = painterResource(R.drawable.ic_chevron_right_24),
            contentDescription = null,
            colorFilter = ColorFilter.tint(AppTheme.colors.tertiaryText),
        )
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    MainScreen(
        onAzkarCategoryClick = remember { { } },
        fudul = null,
    )
}