package io.jawziyya.azkar.ui.zikr

import android.app.Application
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.core.net.toUri
import io.jawziyya.azkar.data.helper.intervalFlow
import io.jawziyya.azkar.data.model.Zikr
import io.jawziyya.azkar.data.repository.AzkarRepository
import io.jawziyya.azkar.data.repository.FileRepository
import io.jawziyya.azkar.ui.core.MediaPlayerState
import io.jawziyya.azkar.ui.core.SimpleMediaPlayer
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.ScopedServices
import io.jawziyya.azkar.data.helper.observeKey
import io.jawziyya.azkar.ui.core.Settings
import io.jawziyya.azkar.ui.hadith.HadithScreenKey
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

/**
 * Created by uvays on 07.06.2022.
 */

class ZikrViewModel(
    private val screenKey: ZikrScreenKey,
    private val backstack: Backstack,
    private val application: Application,
    private val azkarRepository: AzkarRepository,
    private val fileRepository: FileRepository,
    private val simpleMediaPlayer: SimpleMediaPlayer,
    private val sharedPreferences: SharedPreferences,
) : ScopedServices.Registered {

    private val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Main
    private val coroutineScope = CoroutineScope(coroutineContext)

    val zikrFlow: MutableStateFlow<List<Zikr>> = MutableStateFlow(emptyList())

    val translationVisibleFlow: Flow<Boolean>
        get() = sharedPreferences.observeKey(Settings.translationVisibleKey, true)

    val transliterationVisibleFlow: Flow<Boolean>
        get() = sharedPreferences.observeKey(Settings.transliterationVisibleKey, true)

    val playerStateFlow: MutableStateFlow<ZikrPlayerState> = MutableStateFlow(ZikrPlayerState())

    val audioPlaybackSpeedFlow: MutableStateFlow<AudioPlaybackSpeed> =
        MutableStateFlow(AudioPlaybackSpeed.DEFAULT)

    private var getAudioFileJob: Job? = null
    private var timerJob: Job? = null

    private val audioPlaybackSpeedArray = AudioPlaybackSpeed.values()

    override fun onServiceRegistered() {
        coroutineScope.launch {
            azkarRepository.getAzkar(screenKey.azkarCategory).catch { e -> Timber.e(e) }
                .collect { value ->
                    zikrFlow.value = value
                }
        }

        coroutineScope.launch {
            simpleMediaPlayer.getStateFlow().collect { state ->
                when (state) {
                    MediaPlayerState.IDLE -> {}
                    MediaPlayerState.RESET -> {}
                    MediaPlayerState.INIT -> {}
                    MediaPlayerState.PREPARING -> {}
                    MediaPlayerState.PREPARED -> {}
                    MediaPlayerState.STARTED -> {
                        playerStateFlow.value = playerStateFlow.value.copy(
                            playing = true,
                        )
                        startTimer()
                    }
                    MediaPlayerState.PAUSED -> {
                        timerJob?.cancel()
                        playerStateFlow.value = playerStateFlow.value.copy(
                            playing = false,
                        )
                    }
                    MediaPlayerState.STOPPED -> {
                        timerJob?.cancel()
                        playerStateFlow.value = playerStateFlow.value.copy(
                            timestamp = 0L,
                            playing = false,
                        )
                    }
                    MediaPlayerState.COMPLETED -> {
                        timerJob?.cancel()
                        playerStateFlow.value = playerStateFlow.value.copy(
                            timestamp = 0L,
                            playing = false,
                        )
                    }
                    MediaPlayerState.ERROR -> {}
                    MediaPlayerState.END -> {}
                }
            }
        }
    }

    fun onPlayClick(zikr: Zikr) {
        val timestamp = playerStateFlow.value.timestamp
        when (simpleMediaPlayer.getState()) {
            MediaPlayerState.IDLE -> play(zikr = zikr, timestamp = timestamp)
            MediaPlayerState.RESET -> {}
            MediaPlayerState.INIT -> {}
            MediaPlayerState.PREPARING -> {}
            MediaPlayerState.PREPARED -> simpleMediaPlayer.play()
            MediaPlayerState.STARTED -> simpleMediaPlayer.pause()
            MediaPlayerState.PAUSED -> simpleMediaPlayer.play(timestamp = timestamp)
            MediaPlayerState.STOPPED -> play(zikr = zikr, timestamp = timestamp)
            MediaPlayerState.COMPLETED -> play(zikr = zikr, timestamp = timestamp)
            MediaPlayerState.ERROR -> play(zikr = zikr, timestamp = timestamp)
            MediaPlayerState.END -> {}
        }
    }

    fun onAudioPlaybackSpeedChangeClick(zikr: Zikr) {
        val currentIndex = audioPlaybackSpeedArray.indexOf(audioPlaybackSpeedFlow.value)
        val index = (currentIndex + 1) % audioPlaybackSpeedArray.size
        val value = audioPlaybackSpeedArray[index]

        audioPlaybackSpeedFlow.value = value
        simpleMediaPlayer.setPlaybackSpeed(value.value)
    }

    fun onReplayClick(zikr: Zikr) {
        play(zikr, 0L)
    }

    private fun play(zikr: Zikr, timestamp: Long) {
        val playerState = playerStateFlow.value

        when {
            playerState.azkarId != zikr.id -> play(zikr)
            playerState.uri == null -> play(zikr)
            else -> coroutineScope.launch {
                simpleMediaPlayer.play(
                    uri = playerState.uri,
                    timestamp = timestamp,
                    speed = audioPlaybackSpeedFlow.value.value,
                )
            }
        }
    }

    private fun play(zikr: Zikr) {
        getAudioFileJob?.cancel()
        getAudioFileJob = coroutineScope.launch {
            try {
                playerStateFlow.value = playerStateFlow.value.copy(
                    azkarId = zikr.id,
                    duration = 0L,
                    loading = true,
                )

                val uri = fileRepository.getAudioFile(zikr)?.toUri()

                if (uri == null) {
                    playerStateFlow.value = playerStateFlow.value.copy(
                        azkarId = zikr.id,
                        loading = false,
                        timestamp = 0L,
                        duration = 0L,
                    )
                } else {
                    simpleMediaPlayer.play(uri = uri, speed = audioPlaybackSpeedFlow.value.value)
                    playerStateFlow.value = playerStateFlow.value.copy(
                        azkarId = zikr.id,
                        loading = false,
                        uri = uri,
                        timestamp = 0L,
                        duration = simpleMediaPlayer.getDuration(),
                    )
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = coroutineScope.launch {
            intervalFlow(0L, 50L).catch { Timber.e(it) }.collect {
                val playerState = playerStateFlow.value

                if (playerState.uri == simpleMediaPlayer.uri) {
                    playerStateFlow.value = playerState.copy(
                        timestamp = simpleMediaPlayer.getCurrentPosition(),
                    )
                }
            }
        }
    }

    fun onPageChange() {
        simpleMediaPlayer.stop()
        getAudioFileJob?.cancel()
        playerStateFlow.value = playerStateFlow.value.copy(
            loading = false,
        )
    }

    fun onTranslationVisibilityChange(value: Boolean) {
        sharedPreferences.edit { putBoolean(Settings.translationVisibleKey, !value) }
    }

    fun onTransliterationVisibilityChange(value: Boolean) {
        sharedPreferences.edit { putBoolean(Settings.transliterationVisibleKey, !value) }
    }

    fun onHadithClick(id: Long, title: String) {
        backstack.goTo(HadithScreenKey(id, title))
    }

    override fun onServiceUnregistered() {
        coroutineContext.cancelChildren()
        simpleMediaPlayer.destroy()
    }
}