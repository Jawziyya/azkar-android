package io.jawziyya.azkar.ui.zikr

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import io.jawziyya.azkar.R
import io.jawziyya.azkar.data.model.Zikr
import io.jawziyya.azkar.data.model.AzkarCategory
import io.jawziyya.azkar.data.model.AzkarSource
import io.jawziyya.azkar.ui.core.quantityStringResource
import io.jawziyya.azkar.ui.core.rippleClickable
import io.jawziyya.azkar.ui.core.toSp
import io.jawziyya.azkar.ui.theme.*
import io.jawziyya.azkar.ui.theme.component.AppBar
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
    zikrPlayerState: ZikrPlayerState,
    onReplay: (Zikr) -> Unit,
    onPlayClick: (Zikr) -> Unit,
    onAudioPlaybackSpeedChange: (Zikr) -> Unit,
    audioPlaybackSpeed: AudioPlaybackSpeed,
    onPageChange: () -> Unit,
) {
    AppTheme {
        ProvideWindowInsets {
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
                    val playerState =
                        if (azkar.id == zikrPlayerState.azkarId) zikrPlayerState
                        else ZikrPlayerState()

                    Content(
                        modifier = Modifier,
                        zikr = azkar,
                        playerState = playerState,
                        onReplay = onReplay,
                        onPlayClick = onPlayClick,
                        onAudioPlaybackSpeedChange = onAudioPlaybackSpeedChange,
                        audioPlaybackSpeed = audioPlaybackSpeed,
                    )
                }
            }
        }
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    zikr: Zikr,
    playerState: ZikrPlayerState,
    onReplay: (Zikr) -> Unit,
    onPlayClick: (Zikr) -> Unit,
    onAudioPlaybackSpeedChange: (Zikr) -> Unit,
    audioPlaybackSpeed: AudioPlaybackSpeed,
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
                .padding(16.dp),
            text = zikr.title,
            style = AppTheme.typography.title,
            textAlign = TextAlign.Center,
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .padding(16.dp),
            text = text,
            style = AppTheme.typography.arabic,
            textAlign = TextAlign.End,
        )
        Player(
            modifier = Modifier,
            zikr = zikr,
            playerState = playerState,
            onReplay = onReplay,
            onPlayClick = onPlayClick,
            onAudioPlaybackSpeedChange = onAudioPlaybackSpeedChange,
            audioPlaybackSpeed = audioPlaybackSpeed,
        )

        if (!translation.isNullOrBlank()) {
            TitleTextSection(
                modifier = Modifier.padding(top = 16.dp),
                title = "Перевод".uppercase(),
                text = translation,
            )
        }

        if (!transliteration.isNullOrBlank()) {
            TitleTextSection(
                modifier = Modifier.padding(top = 16.dp),
                title = "Транскрипция".uppercase(),
                text = transliteration,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 16.dp)
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
                    textAlign = TextAlign.Start
                )
            }
            Column(modifier = Modifier) {
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
                    textAlign = TextAlign.Start
                )
            }
        }
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
    Column(modifier = modifier.fillMaxWidth()) {
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
                Crossfade(targetState = playerState.playing) { value ->
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
    val valueRaw = 1f * timestamp / duration

    val value by animateFloatAsState(valueRaw)

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

private fun millisToTimeText(value: Long): String {
    var difference = value
    val elapsedDays = TimeUnit.MILLISECONDS.toDays(value)

    difference %= TimeUnit.DAYS.toMillis(1)
    val elapsedHours = TimeUnit.MILLISECONDS.toHours(difference)

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
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = title,
            style = AppTheme.typography.sectionHeader,
            textAlign = TextAlign.Start,
            color = AppTheme.colors.tertiaryText,
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = text,
            style = AppTheme.typography.body,
            textAlign = TextAlign.Start,
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
            source = listOf(AzkarSource.AHMAD, AzkarSource.ABUDAUD),
            title = "При облачении в новую одежду",
            text = "اللَّهُـمَّ لَـكَ الْحَـمْـدُ أَنْـتَ كَسَـوْتَنِيهِ، أَسْأَلُـكَ مِـنْ خَـيْرِهِ وَخَـيْرِ مَا صُنِعَ لَـهُ، وَأَعُوذُ بِكَ مِـنْ شَـرِّهِ وَشَـرِّ مَا صُنِعَ لَـه",
            translation = "О Аллах, хвала Тебе! Ты одел меня в эту (одежду), и я прошу Тебя о благе её и благе того, для чего она была изготовлена, и прибегаю к Тебе от зла её и зла того, для чего она была изготовлена.",
            transliteration = "Аллахумма ля-кя-ль-хамду! Анта кясаута-ни-хи ас'алю-кя мин хайри-хи ва хайри ма суни'а ля-ху ва а'узу би-кя мин шарри-хи ва шарри ма суни'а ля-ху",
            order = 1,
            audioFileUrl = "https://azkar.ru/uploads/files/1219677d91fd00fa935ba1be08ea5b24.mp3",
            audioFileName = null,
            repeats = 1,
        )
    )

    ZikrScreen(
        onBackClick = {},
        azkarCategory = AzkarCategory.OTHER,
        azkarIndex = 0,
        zikrList = zikrList,
        zikrPlayerState = ZikrPlayerState(),
        onPlayClick = {},
        onReplay = {},
        onAudioPlaybackSpeedChange = {},
        audioPlaybackSpeed = AudioPlaybackSpeed.DEFAULT,
        onPageChange = {},
    )
}