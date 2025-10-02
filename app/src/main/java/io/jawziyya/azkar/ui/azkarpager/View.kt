package io.jawziyya.azkar.ui.azkarpager

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import io.jawziyya.azkar.R
import io.jawziyya.azkar.database.model.Azkar
import io.jawziyya.azkar.database.model.AzkarCategory
import io.jawziyya.azkar.database.model.Source
import io.jawziyya.azkar.ui.core.noRippleClickable
import io.jawziyya.azkar.ui.core.quantityStringResource
import io.jawziyya.azkar.ui.core.rippleClickable
import io.jawziyya.azkar.ui.core.toSp
import io.jawziyya.azkar.ui.hadith.HadithScreen
import io.jawziyya.azkar.ui.theme.AppTheme
import io.jawziyya.azkar.ui.theme.components.AppBar
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.concurrent.TimeUnit

/**
 * Created by uvays on 07.06.2022.
 */

@Serializable
data class AzkarPagerScreen(
    val categoryName: String,
    val index: Int = 0,
)

@Composable
fun AzkarPagerScreenView(
    navController: NavHostController,
    navBackStackEntry: NavBackStackEntry,
) {
    val args = navBackStackEntry.toRoute<AzkarPagerScreen>()
    val azkarCategory = AzkarCategory.valueOf(args.categoryName)
    val azkarIndex = args.index

    val viewModel: AzkarPagerViewModel = koinViewModel(
        parameters = { parametersOf(azkarCategory, azkarIndex) },
    )

    val azkarList by viewModel.azkarListFlow.collectAsState()
    val translationVisible by viewModel.translationVisibleFlow.collectAsState(true)
    val transliterationVisible by viewModel.transliterationVisibleFlow.collectAsState(true)
    val playerState by viewModel.playerStateFlow.collectAsState()
    val audioPlaybackSpeed by viewModel.audioPlaybackSpeedFlow.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.onDispose()
        }
    }

    View(
        azkarCategory = azkarCategory,
        azkarIndex = azkarIndex,
        azkarList = azkarList,
        translationVisible = translationVisible,
        transliterationVisible = transliterationVisible,
        playerState = playerState,
        audioPlaybackSpeed = audioPlaybackSpeed,
        onBackClick = navController::popBackStack,
        onHadithClick = { id -> navController.navigate(HadithScreen(id = id)) },
        onPageChange = viewModel::onPageChange,
        onTranslationVisibilityChange = viewModel::onTranslationVisibilityChange,
        onTransliterationVisibilityChange = viewModel::onTransliterationVisibilityChange,
        onReplayClick = viewModel::onReplayClick,
        onPlayClick = viewModel::onPlayClick,
        onAudioPlaybackSpeedChangeClick = viewModel::onAudioPlaybackSpeedChangeClick,
        onCounterClick = viewModel::onCounterClick,
    )
}

@Composable
private fun View(
    azkarCategory: AzkarCategory,
    azkarIndex: Int,
    azkarList: List<Azkar>,
    translationVisible: Boolean,
    transliterationVisible: Boolean,
    playerState: AzkarPlayerState,
    audioPlaybackSpeed: AudioPlaybackSpeed,
    onBackClick: () -> Unit,
    onHadithClick: (Long) -> Unit,
    onPageChange: () -> Unit,
    onTranslationVisibilityChange: (Boolean) -> Unit,
    onTransliterationVisibilityChange: (Boolean) -> Unit,
    onReplayClick: (Azkar) -> Unit,
    onPlayClick: (Azkar) -> Unit,
    onAudioPlaybackSpeedChangeClick: (Azkar) -> Unit,
    onCounterClick: (Azkar) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background),
    ) {
        AppBar(
            title = stringResource(azkarCategory.titleRes),
            onBackClick = onBackClick,
        )

        Crossfade(azkarList.isEmpty(), label = "") { isEmpty ->
            if (isEmpty) return@Crossfade

            val pagerState = rememberPagerState(
                initialPage = azkarIndex,
                pageCount = remember(azkarList.size) { { azkarList.size } },
            )

            LaunchedEffect(pagerState.currentPage) {
                onPageChange()
            }

            Box {
                HorizontalPager(
                    modifier = Modifier.fillMaxSize(),
                    state = pagerState,
                    beyondViewportPageCount = 1,
                    key = { index -> azkarList[index].id }) { page ->
                    val azkar = azkarList[page]
                    val playerStateValue = if (azkar.id == playerState.azkarId) playerState
                    else AzkarPlayerState()

                    Content(
                        modifier = Modifier,
                        azkar = azkar,
                        translationVisible = translationVisible,
                        onTranslationVisibilityChange = onTranslationVisibilityChange,
                        transliterationVisible = transliterationVisible,
                        onTransliterationVisibilityChange = onTransliterationVisibilityChange,
                        playerState = playerStateValue,
                        onReplay = onReplayClick,
                        onPlayClick = onPlayClick,
                        onAudioPlaybackSpeedChange = onAudioPlaybackSpeedChangeClick,
                        audioPlaybackSpeed = audioPlaybackSpeed,
                        onHadithClick = onHadithClick,
                    )
                }

                if (azkarCategory.counter) {
                    Counter(
                        azkarList = azkarList,
                        pagerState = pagerState,
                        onClick = onCounterClick,
                    )
                }
            }

        }
    }
}

@Composable
private fun BoxScope.Counter(
    azkarList: List<Azkar>, pagerState: PagerState, onClick: (Azkar) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val repeatsLeft = azkarList[pagerState.currentPage].repeatsLeft
    var counterClicked by remember { mutableStateOf(false) }

    LaunchedEffect(repeatsLeft) {
        if (!counterClicked) {
            return@LaunchedEffect
        }

        if (repeatsLeft > 0) {
            return@LaunchedEffect
        }

        val page = pagerState.currentPage + 1

        if (page < pagerState.pageCount) {
            delay(300)
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            pagerState.scrollToPage(page)
            counterClicked = false
        }
    }

    Box(modifier = Modifier.Companion.align(Alignment.BottomEnd)) {
        Crossfade(repeatsLeft > 0, label = "") { visibleRepetitionButton ->
            if (visibleRepetitionButton) {
                FloatingActionButton(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(16.dp),
                    backgroundColor = AppTheme.colors.accent,
                    onClick = remember(pagerState.currentPage) {
                        {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            counterClicked = true
                            onClick(azkarList[pagerState.currentPage])
                        }
                    },
                ) {
                    Text(
                        text = if (repeatsLeft > 0) repeatsLeft.toString() else "",
                        style = AppTheme.typography.digits,
                        fontSize = 20.sp,
                        color = AppTheme.colors.textOnAccent,
                    )
                }
            }
        }
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    azkar: Azkar,
    translationVisible: Boolean,
    onTranslationVisibilityChange: (Boolean) -> Unit,
    transliterationVisible: Boolean,
    onTransliterationVisibilityChange: (Boolean) -> Unit,
    playerState: AzkarPlayerState,
    onReplay: (Azkar) -> Unit,
    onPlayClick: (Azkar) -> Unit,
    onAudioPlaybackSpeedChange: (Azkar) -> Unit,
    audioPlaybackSpeed: AudioPlaybackSpeed,
    onHadithClick: (Long) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(bottom = 88.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp),
            text = azkar.title,
            style = AppTheme.typography.title,
            textAlign = TextAlign.Center,
            color = AppTheme.colors.secondaryText,
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp),
            text = azkar.text,
            style = AppTheme.typography.arabic,
            textAlign = TextAlign.Start,
            color = AppTheme.colors.text,
        )

        if (azkar.audioName != null) {
            Player(
                modifier = Modifier.padding(top = 16.dp),
                zikr = azkar,
                playerState = playerState,
                onReplay = onReplay,
                onPlayClick = onPlayClick,
                onAudioPlaybackSpeedChange = onAudioPlaybackSpeedChange,
                audioPlaybackSpeed = audioPlaybackSpeed,
            )
        }

        if (!azkar.translation.isNullOrBlank()) {
            TitleTextSection(
                modifier = Modifier,
                title = "Перевод".uppercase(),
                text = azkar.translation,
                visible = translationVisible,
                onVisibilityChange = onTranslationVisibilityChange,
            )
        }

        if (!azkar.transliteration.isNullOrBlank()) {
            TitleTextSection(
                modifier = Modifier,
                title = "Транскрипция".uppercase(),
                text = azkar.transliteration,
                visible = transliterationVisible,
                onVisibilityChange = onTransliterationVisibilityChange,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(modifier = Modifier) {
                Text(
                    modifier = Modifier,
                    text = "Повторения".uppercase(),
                    style = AppTheme.typography.sectionHeader,
                    textAlign = TextAlign.Start,
                    color = AppTheme.colors.tertiaryText,
                )
                Text(
                    modifier = Modifier,
                    text = quantityStringResource(
                        R.plurals.zikr_repetition_count,
                        azkar.repeats,
                        azkar.repeats,
                    ),
                    style = AppTheme.typography.tip,
                    textAlign = TextAlign.Start,
                    color = AppTheme.colors.text,
                )
            }

            val source = azkar.source.firstOrNull()
            if (source != null) {
                val (clickableModifier, textDecoration) = if (azkar.hadith == null) Pair(
                    Modifier,
                    TextDecoration.None,
                )
                else Pair(
                    Modifier.noRippleClickable { onHadithClick(azkar.hadith) },
                    TextDecoration.Underline,
                )

                Column(
                    modifier = Modifier.then(clickableModifier)
                ) {
                    Text(
                        modifier = Modifier,
                        text = "Источник".uppercase(),
                        style = AppTheme.typography.sectionHeader,
                        textAlign = TextAlign.Start,
                        color = AppTheme.colors.tertiaryText,
                    )
                    Text(
                        modifier = Modifier,
                        text = stringResource(source.titleRes).uppercase(),
                        style = AppTheme.typography.tip,
                        textAlign = TextAlign.Start,
                        color = AppTheme.colors.text,
                        textDecoration = textDecoration,
                    )
                }
            }
        }

        if (azkar.benefits != null) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    modifier = Modifier.size(21.dp, 18.dp),
                    painter = painterResource(R.drawable.ic_tip),
                    contentDescription = null,
                )
                Text(
                    modifier = Modifier.weight(1f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp,
                    text = azkar.benefits,
                    textAlign = TextAlign.Start,
                    color = AppTheme.colors.text,
                )
            }
        }
    }
}

@Composable
private fun Player(
    modifier: Modifier,
    zikr: Azkar,
    playerState: AzkarPlayerState,
    onReplay: (Azkar) -> Unit,
    onPlayClick: (Azkar) -> Unit,
    onAudioPlaybackSpeedChange: (Azkar) -> Unit,
    audioPlaybackSpeed: AudioPlaybackSpeed,
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            Text(
                modifier = Modifier,
                text = millisToTimeText(playerState.timestamp),
                style = AppTheme.typography.digits,
                textAlign = TextAlign.Center,
                color = AppTheme.colors.tertiaryText,
            )
            IconButton(
                modifier = Modifier,
                onClick = {
                    if (!playerState.loading) {
                        onReplay(zikr)
                    }
                },
            ) {
                Icon(
                    imageVector = Icons.Rounded.Replay,
                    contentDescription = null,
                    tint = AppTheme.colors.alternativeAccent,
                )
            }
            IconButton(
                modifier = Modifier,
                onClick = {
                    if (!playerState.loading) {
                        onPlayClick(zikr)
                    }
                },
            ) {
                Crossfade(targetState = playerState.playing, label = "") { value ->
                    Icon(
                        imageVector = if (value) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        tint = AppTheme.colors.alternativeAccent,
                    )
                }
            }
            Text(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .rippleClickable { onAudioPlaybackSpeedChange(zikr) }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                text = "${audioPlaybackSpeed.value}x",
                style = AppTheme.typography.digits,
                fontSize = 14.dp.toSp(),
                lineHeight = 16.dp.toSp(),
                color = AppTheme.colors.alternativeAccent,
            )
            Text(
                modifier = Modifier,
                text = millisToTimeText(playerState.duration - playerState.timestamp),
                style = AppTheme.typography.digits,
                textAlign = TextAlign.Center,
                color = AppTheme.colors.tertiaryText,
            )
        }
        PlayerProgress(
            modifier = Modifier,
            duration = playerState.duration,
            timestamp = playerState.timestamp,
            loading = playerState.loading,
        )
    }
}

@Composable
private fun PlayerProgress(
    modifier: Modifier,
    duration: Long,
    timestamp: Long,
    loading: Boolean,
) {
    val valueRaw = calculateProgress(timestamp, duration)
    val value by animateFloatAsState(valueRaw, label = "")

    Crossfade(targetState = loading, label = "") { loadingValue ->
        if (loadingValue) {
            LinearProgressIndicator(
                modifier = modifier.fillMaxWidth(),
                backgroundColor = AppTheme.colors.progressBackground,
                color = AppTheme.colors.alternativeAccent,
            )
        } else {
            LinearProgressIndicator(
                progress = value,
                modifier = modifier.fillMaxWidth(),
                backgroundColor = AppTheme.colors.progressBackground,
                color = AppTheme.colors.alternativeAccent,
            )
        }
    }
}

private fun calculateProgress(timestamp: Long, duration: Long): Float {
    if (timestamp <= 0 && duration <= 0) return 0f
    return 1f * timestamp / duration
}

private fun millisToTimeText(value: Long): String {
    var difference = value

    difference %= TimeUnit.DAYS.toMillis(1)

    difference %= TimeUnit.HOURS.toMillis(1)
    val elapsedMinutes = TimeUnit.MILLISECONDS.toMinutes(difference)

    difference %= TimeUnit.MINUTES.toMillis(1)
    val elapsedSeconds = TimeUnit.MILLISECONDS.toSeconds(difference)

    return "${formattedTimeValue(elapsedMinutes)}:${formattedTimeValue(elapsedSeconds)}"
}

private fun formattedTimeValue(value: Long): String {
    return if (value < 10) "0$value" else value.toString()
}

@Composable
private fun TitleTextSection(
    modifier: Modifier = Modifier,
    title: String,
    text: String,
    visible: Boolean,
    onVisibilityChange: (Boolean) -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val chevronRotateDegree by animateFloatAsState(
            targetValue = if (visible) 180f else 0f,
            label = "",
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .rippleClickable(remember(visible) { { onVisibilityChange(visible) } })
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = title,
                style = AppTheme.typography.sectionHeader,
                textAlign = TextAlign.Start,
                color = AppTheme.colors.tertiaryText,
            )
            Image(
                modifier = Modifier.rotate(chevronRotateDegree),
                painter = painterResource(R.drawable.ic_chevron_down_24),
                contentDescription = null,
                colorFilter = ColorFilter.tint(AppTheme.colors.alternativeAccent)
            )
        }

        AnimatedVisibility(visible) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                text = text,
                style = AppTheme.typography.translation,
                textAlign = TextAlign.Start,
                color = AppTheme.colors.text,
            )
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(AppTheme.colors.accent.copy(alpha = 0.1f)),
        )
    }
}

@Preview
@Composable
fun ZikrScreenPreview() {
    val zikrList = listOf(
        Azkar(
            id = 31,
            azkarCategoryId = 101,
            category = AzkarCategory.Other,
            source = listOf(Source.AHMAD, Source.ABUDAUD),
            title = "При облачении в новую одежду",
            text = "اللَّهُـمَّ لَـكَ الْحَـمْـدُ أَنْـتَ كَسَـوْتَنِيهِ، أَسْأَلُـكَ مِـنْ خَـيْرِهِ وَخَـيْرِ مَا صُنِعَ لَـهُ، وَأَعُوذُ بِكَ مِـنْ شَـرِّهِ وَشَـرِّ مَا صُنِعَ لَـه",
            translation = "О Аллах, хвала Тебе! Ты одел меня в эту (одежду), и я прошу Тебя о благе её и благе того, для чего она была изготовлена, и прибегаю к Тебе от зла её и зла того, для чего она была изготовлена.",
            transliteration = "Аллахумма ля-кя-ль-хамду! Анта кясаута-ни-хи ас'алю-кя мин хайри-хи ва хайри ма суни'а ля-ху ва а'узу би-кя мин шарри-хи ва шарри ма суни'а ля-ху",
            order = 1,
            audioName = null,
            repeats = 1,
            repeatsLeft = 1,
            hadith = null,
            notes = null,
            benefits = "Кто произнесёт эти слова днём, будучи убеждённым в них, и умрёт в тот же день до наступления вечера, окажется среди обитателей Рая.",
        )
    )

    View(
        azkarCategory = AzkarCategory.Other,
        azkarIndex = 0,
        azkarList = zikrList,
        translationVisible = true,
        transliterationVisible = true,
        playerState = AzkarPlayerState(),
        audioPlaybackSpeed = AudioPlaybackSpeed.DEFAULT,
        onBackClick = {},
        onHadithClick = {},
        onPageChange = {},
        onTranslationVisibilityChange = {},
        onTransliterationVisibilityChange = {},
        onReplayClick = {},
        onPlayClick = {},
        onAudioPlaybackSpeedChangeClick = {},
        onCounterClick = {},
    )
}