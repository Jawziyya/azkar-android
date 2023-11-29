package io.jawziyya.azkar.ui.azkarpager

import android.app.Application
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.core.net.toUri
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.ScopedServices
import io.jawziyya.azkar.data.helper.observeKey
import io.jawziyya.azkar.data.repository.AzkarCounterRepository
import io.jawziyya.azkar.data.repository.FileRepository
import io.jawziyya.azkar.database.DatabaseHelper
import io.jawziyya.azkar.database.model.Azkar
import io.jawziyya.azkar.database.model.AzkarCategory
import io.jawziyya.azkar.ui.core.BaseViewModel
import io.jawziyya.azkar.ui.core.MediaPlayerState
import io.jawziyya.azkar.ui.core.Settings
import io.jawziyya.azkar.ui.core.SimpleMediaPlayer
import io.jawziyya.azkar.ui.core.intervalFlow
import io.jawziyya.azkar.ui.hadith.HadithScreenKey
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by uvays on 07.06.2022.
 */

class AzkarPagerViewModel(
    private val screenKey: AzkarPagerScreenKey,
    private val backstack: Backstack,
    private val application: Application,
    private val databaseHelper: DatabaseHelper,
    private val fileRepository: FileRepository,
    private val simpleMediaPlayer: SimpleMediaPlayer,
    private val sharedPreferences: SharedPreferences,
    private val azkarCounterRepository: AzkarCounterRepository,
) : BaseViewModel(), ScopedServices.Activated {

    val azkarCategoryFlow: MutableStateFlow<AzkarCategory> =
        MutableStateFlow(screenKey.azkarCategory)
    val azkarIndexFlow: MutableStateFlow<Int> = MutableStateFlow(screenKey.azkarIndex)

    private val _azkarListFlow: MutableStateFlow<List<Azkar>> = MutableStateFlow(emptyList())
    val azkarListFlow: StateFlow<List<Azkar>>
        get() = combine(_azkarListFlow, azkarCounterRepository.stateFlow) { azkarList, counter ->
            return@combine azkarList.map { azkar ->
                val repetitions = (counter.map[azkar.azkarCategoryId] ?: 0)
                return@map azkar.copy(repeatsLeft = azkar.repeats - repetitions)
            }
        }
            .stateIn(coroutineScope, SharingStarted.WhileSubscribed(), emptyList())

    val translationVisibleFlow: Flow<Boolean>
        get() = sharedPreferences.observeKey(Settings.translationVisibleKey, true)

    val transliterationVisibleFlow: Flow<Boolean>
        get() = sharedPreferences.observeKey(Settings.transliterationVisibleKey, true)

    val playerStateFlow: MutableStateFlow<AzkarPlayerState> = MutableStateFlow(AzkarPlayerState())

    val audioPlaybackSpeedFlow: MutableStateFlow<AudioPlaybackSpeed> =
        MutableStateFlow(AudioPlaybackSpeed.DEFAULT)

    private var getAudioFileJob: Job? = null
    private var timerJob: Job? = null

    private val audioPlaybackSpeedArray = AudioPlaybackSpeed.values()

    override fun onServiceRegistered() {
        coroutineScope.launch {
            _azkarListFlow.value = databaseHelper.getAzkarList(screenKey.azkarCategory)
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

    fun onPlayClick(azkar: Azkar) {
        val timestamp = playerStateFlow.value.timestamp
        when (simpleMediaPlayer.getState()) {
            MediaPlayerState.IDLE -> play(azkar = azkar, timestamp = timestamp)
            MediaPlayerState.RESET -> {}
            MediaPlayerState.INIT -> {}
            MediaPlayerState.PREPARING -> {}
            MediaPlayerState.PREPARED -> simpleMediaPlayer.play()
            MediaPlayerState.STARTED -> simpleMediaPlayer.pause()
            MediaPlayerState.PAUSED -> simpleMediaPlayer.play(timestamp = timestamp)
            MediaPlayerState.STOPPED -> play(azkar = azkar, timestamp = timestamp)
            MediaPlayerState.COMPLETED -> play(azkar = azkar, timestamp = timestamp)
            MediaPlayerState.ERROR -> play(azkar = azkar, timestamp = timestamp)
            MediaPlayerState.END -> {}
        }
    }

    fun onAudioPlaybackSpeedChangeClick(azkar: Azkar) {
        val currentIndex = audioPlaybackSpeedArray.indexOf(audioPlaybackSpeedFlow.value)
        val index = (currentIndex + 1) % audioPlaybackSpeedArray.size
        val value = audioPlaybackSpeedArray[index]

        audioPlaybackSpeedFlow.value = value
        simpleMediaPlayer.setPlaybackSpeed(value.value)
    }

    fun onReplayClick(azkar: Azkar) {
        play(azkar, 0L)
    }

    private fun play(azkar: Azkar, timestamp: Long) {
        val playerState = playerStateFlow.value

        when {
            playerState.azkarId != azkar.id -> play(azkar)
            playerState.uri == null -> play(azkar)
            else -> coroutineScope.launch {
                simpleMediaPlayer.play(
                    uri = playerState.uri,
                    timestamp = timestamp,
                    speed = audioPlaybackSpeedFlow.value.value,
                )
            }
        }
    }

    private fun play(azkar: Azkar) {
        getAudioFileJob?.cancel()
        getAudioFileJob = coroutineScope.launch {
            try {
                playerStateFlow.value = playerStateFlow.value.copy(
                    azkarId = azkar.id,
                    duration = 0L,
                    loading = true,
                )

                val uri = fileRepository.getAudioFile(azkar)?.toUri()

                if (uri == null) {
                    playerStateFlow.value = playerStateFlow.value.copy(
                        azkarId = azkar.id,
                        loading = false,
                        timestamp = 0L,
                        duration = 0L,
                    )
                } else {
                    simpleMediaPlayer.play(uri = uri, speed = audioPlaybackSpeedFlow.value.value)
                    playerStateFlow.value = playerStateFlow.value.copy(
                        azkarId = azkar.id,
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

    override fun onServiceActive() {}

    override fun onServiceInactive() {
        simpleMediaPlayer.pause()
    }

    override fun onServiceUnregistered() {
        super.onServiceUnregistered()
        simpleMediaPlayer.destroy()
    }

    fun onCounterClick(azkar: Azkar) {
        azkarCounterRepository.onEvent(azkar)
    }
}