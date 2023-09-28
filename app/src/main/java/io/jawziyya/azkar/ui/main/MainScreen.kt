package io.jawziyya.azkar.ui.main

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import io.jawziyya.azkar.R
import io.jawziyya.azkar.data.model.AzkarCategory
import io.jawziyya.azkar.data.model.Fadail
import io.jawziyya.azkar.ui.core.rippleClickable
import io.jawziyya.azkar.ui.theme.AppTheme
import io.jawziyya.azkar.ui.theme.colorSystemTeal
import kotlin.random.Random

/**
 * Created by uvays on 05.06.2022.
 */

@Composable
fun MainScreen(
    onAzkarCategoryClick: (AzkarCategory) -> Unit,
    onSettingsClick: () -> Unit,
    fadail: Fadail?,
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
            val emoji = remember { emojiArray.random(random) }

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

//            Column(
//                modifier = Modifier
//                    .padding(start = 16.dp, top = 16.dp, end = 16.dp)
//                    .fillMaxWidth()
//                    .clip(RoundedCornerShape(12.dp))
//                    .background(AppTheme.colors.contentBackground),
//            ) {
//                ListItem(
//                    modifier = Modifier.fillMaxWidth(),
//                    imageRes = R.drawable.ic_settings_32,
//                    colorFilter = ColorFilter.tint(AppTheme.colors.tertiaryText),
//                    title = stringResource(R.string.main_settings),
//                    onClick = onSettingsClick,
//                )
//            }

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
            animationSpeed = .3f,
            title = stringResource(R.string.azkar_category_morning),
            onClick = remember {
                { onAzkarCategoryClick(AzkarCategory.MORNING) }
            },
        )
        DayAzkarItem(
            modifier = Modifier.weight(1f),
            lottieRes = if (isSystemInDarkTheme()) R.raw.moon else R.raw.moon2,
            animationSpeed = .2f,
            title = stringResource(R.string.azkar_category_evening),
            onClick = remember {
                { onAzkarCategoryClick(AzkarCategory.EVENING) }
            },
        )
    }
}

@Composable
private fun DayAzkarItem(
    modifier: Modifier = Modifier,
    @RawRes lottieRes: Int,
    animationSpeed: Float,
    title: String,
    onClick: () -> Unit,
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
            speed = animationSpeed,
        )
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
    MainScreen(
        onAzkarCategoryClick = remember { {} },
        onSettingsClick = remember { {} },
        fadail = null,
    )
}