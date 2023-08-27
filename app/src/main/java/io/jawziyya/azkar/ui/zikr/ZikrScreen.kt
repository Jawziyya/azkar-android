package io.jawziyya.azkar.ui.zikr

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import io.jawziyya.azkar.R
import io.jawziyya.azkar.data.model.Zikr
import io.jawziyya.azkar.data.model.AzkarCategory
import io.jawziyya.azkar.data.model.Source
import io.jawziyya.azkar.ui.core.noRippleClickable
import io.jawziyya.azkar.ui.core.quantityStringResource
import io.jawziyya.azkar.ui.core.rippleClickable
import io.jawziyya.azkar.ui.core.toSp
import io.jawziyya.azkar.ui.theme.*
import io.jawziyya.azkar.ui.theme.component.AppBar
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Created by uvays on 07.06.2022.
 */

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ZikrScreen(
    onBackClick: () -> Unit,
    azkarCategory: AzkarCategory,
    azkarIndex: Int,
    zikrList: List<Zikr>,
    translationVisible: Boolean,
    onTranslationVisibilityChange: (Boolean) -> Unit,
    transliterationVisible: Boolean,
    onTransliterationVisibilityChange: (Boolean) -> Unit,
    zikrPlayerState: ZikrPlayerState,
    onReplay: (Zikr) -> Unit,
    onPlayClick: (Zikr) -> Unit,
    onAudioPlaybackSpeedChange: (Zikr) -> Unit,
    audioPlaybackSpeed: AudioPlaybackSpeed,
    onPageChange: () -> Unit,
    onHadithClick: (Long, String) -> Unit,
) {
    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppTheme.colors.background),
        ) {
            AppBar(
                title = stringResource(azkarCategory.titleRes),
                onBackClick = onBackClick,
            )
            val pagerState = rememberPagerState(azkarIndex)

            LaunchedEffect(pagerState.currentPage) {
                onPageChange()
            }

            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                count = zikrList.size,
                state = pagerState,
            ) { page ->
                val azkar = zikrList[page]
                val playerState = if (azkar.id == zikrPlayerState.azkarId) zikrPlayerState
                else ZikrPlayerState()

                Content(
                    modifier = Modifier,
                    zikr = azkar,
                    translationVisible = translationVisible,
                    onTranslationVisibilityChange = onTranslationVisibilityChange,
                    transliterationVisible = transliterationVisible,
                    onTransliterationVisibilityChange = onTransliterationVisibilityChange,
                    playerState = playerState,
                    onReplay = onReplay,
                    onPlayClick = onPlayClick,
                    onAudioPlaybackSpeedChange = onAudioPlaybackSpeedChange,
                    audioPlaybackSpeed = audioPlaybackSpeed,
                    onHadithClick = onHadithClick,
                )
            }
        }
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    zikr: Zikr,
    translationVisible: Boolean,
    onTranslationVisibilityChange: (Boolean) -> Unit,
    transliterationVisible: Boolean,
    onTransliterationVisibilityChange: (Boolean) -> Unit,
    playerState: ZikrPlayerState,
    onReplay: (Zikr) -> Unit,
    onPlayClick: (Zikr) -> Unit,
    onAudioPlaybackSpeedChange: (Zikr) -> Unit,
    audioPlaybackSpeed: AudioPlaybackSpeed,
    onHadithClick: (Long, String) -> Unit,
) {
    val text = zikr.text
    val translation = zikr.translation
    val transliteration = zikr.transliteration

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp),
            text = zikr.title,
            style = AppTheme.typography.title,
            textAlign = TextAlign.Center,
            color = AppTheme.colors.secondaryText,
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp),
            text = text,
            style = AppTheme.typography.arabic,
            textAlign = TextAlign.Start,
            color = AppTheme.colors.text,
        )
        Player(
            modifier = Modifier.padding(top = 16.dp),
            zikr = zikr,
            playerState = playerState,
            onReplay = onReplay,
            onPlayClick = onPlayClick,
            onAudioPlaybackSpeedChange = onAudioPlaybackSpeedChange,
            audioPlaybackSpeed = audioPlaybackSpeed,
        )

        if (!translation.isNullOrBlank()) {
            TitleTextSection(
                modifier = Modifier,
                title = "Перевод".uppercase(),
                text = translation,
                visible = translationVisible,
                onVisibilityChange = onTranslationVisibilityChange,
            )
        }

        if (!transliteration.isNullOrBlank()) {
            TitleTextSection(
                modifier = Modifier,
                title = "Транскрипция".uppercase(),
                text = transliteration,
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
                        zikr.repeats,
                        zikr.repeats,
                    ),
                    style = AppTheme.typography.tip,
                    textAlign = TextAlign.Start,
                    color = AppTheme.colors.text,
                )
            }

            val (clickableModifier, textDecoration) =
                if (zikr.hadith == null) Pair(
                    Modifier,
                    TextDecoration.None,
                )
                else Pair(
                    Modifier.noRippleClickable { onHadithClick(zikr.hadith, "Источник") },
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
                val sourceRes = zikr.source.firstOrNull()?.titleRes
                val sourceText =
                    if (sourceRes != null) stringResource(sourceRes).uppercase()
                    else ""

                Text(
                    modifier = Modifier,
                    text = sourceText,
                    style = AppTheme.typography.tip,
                    textAlign = TextAlign.Start,
                    color = AppTheme.colors.text,
                    textDecoration = textDecoration,
                )
            }
        }

//        if (zikr.benefit != null) {
//            Text(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 16.dp)
//                    .padding(horizontal = 16.dp),
//                fontSize = 14.sp,
//                fontWeight = FontWeight.Medium,
//                lineHeight = 20.sp,
//                text = zikr.benefit,
//                textAlign = TextAlign.Start,
//                color = AppTheme.colors.text,
//            )
//        }
    }
}

@Composable
private fun Player(
    modifier: Modifier,
    zikr: Zikr,
    playerState: ZikrPlayerState,
    onReplay: (Zikr) -> Unit,
    onPlayClick: (Zikr) -> Unit,
    onAudioPlaybackSpeedChange: (Zikr) -> Unit,
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
                style = AppTheme.typography.time,
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
                Crossfade(
                    targetState = playerState.playing,
                    label = "",
                ) { value ->
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
                style = AppTheme.typography.time,
                fontSize = 14.dp.toSp(),
                lineHeight = 16.dp.toSp(),
                color = AppTheme.colors.alternativeAccent,
            )
            Text(
                modifier = Modifier,
                text = millisToTimeText(playerState.duration - playerState.timestamp),
                style = AppTheme.typography.time,
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
    val value by animateFloatAsState(valueRaw)
    Timber.d("valueRaw=$valueRaw")
    Timber.d("value=$value")

    Crossfade(targetState = loading) { loadingValue ->
        if (loadingValue) {
            LinearProgressIndicator(
                modifier = modifier.fillMaxWidth(),
                backgroundColor = colorGray,
                color = AppTheme.colors.alternativeAccent,
            )
        } else {
            LinearProgressIndicator(
                progress = value,
                modifier = modifier.fillMaxWidth(),
                backgroundColor = colorGray,
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
        val chevronRotateDegree by animateFloatAsState(if (visible) 180f else 0f, label = "")

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
                style = AppTheme.typography.body,
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
        Zikr(
            id = 31,
            category = AzkarCategory.OTHER,
            source = listOf(Source.AHMAD, Source.ABUDAUD),
            title = "При облачении в новую одежду",
            text = "اللَّهُـمَّ لَـكَ الْحَـمْـدُ أَنْـتَ كَسَـوْتَنِيهِ، أَسْأَلُـكَ مِـنْ خَـيْرِهِ وَخَـيْرِ مَا صُنِعَ لَـهُ، وَأَعُوذُ بِكَ مِـنْ شَـرِّهِ وَشَـرِّ مَا صُنِعَ لَـه",
            translation = "О Аллах, хвала Тебе! Ты одел меня в эту (одежду), и я прошу Тебя о благе её и благе того, для чего она была изготовлена, и прибегаю к Тебе от зла её и зла того, для чего она была изготовлена.",
            transliteration = "Аллахумма ля-кя-ль-хамду! Анта кясаута-ни-хи ас'алю-кя мин хайри-хи ва хайри ма суни'а ля-ху ва а'узу би-кя мин шарри-хи ва шарри ма суни'а ля-ху",
            order = 1,
            audioFileUrl = "https://azkar.ru/uploads/files/1219677d91fd00fa935ba1be08ea5b24.mp3",
            audioFileName = null,
            repeats = 1,
            hadith = null,
            benefit = "Кто произнесёт эти слова днём, будучи убеждённым в них, и умрёт в тот же день до наступления вечера, окажется среди обитателей Рая."
        )
    )

    ZikrScreen(
        onBackClick = {},
        azkarCategory = AzkarCategory.OTHER,
        azkarIndex = 0,
        zikrList = zikrList,
        translationVisible = true,
        onTranslationVisibilityChange = {},
        transliterationVisible = true,
        onTransliterationVisibilityChange = {},
        zikrPlayerState = ZikrPlayerState(),
        onPlayClick = {},
        onReplay = {},
        onAudioPlaybackSpeedChange = {},
        audioPlaybackSpeed = AudioPlaybackSpeed.DEFAULT,
        onPageChange = {},
        onHadithClick = { _, _ -> },
    )
}