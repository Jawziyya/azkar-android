package io.jawziyya.azkar.ui.core

import android.app.Application
import android.media.MediaPlayer
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Created by uvays on 11.06.2022.
 */

enum class MediaPlayerState {
    IDLE,
    RESET,
    INIT,
    PREPARING,
    PREPARED,
    STARTED,
    PAUSED,
    STOPPED,
    COMPLETED,
    ERROR,
    END,
}

class SimpleMediaPlayer(private val application: Application) {

    private val mediaPlayer: MediaPlayer = MediaPlayer()
    private val stateFlow: MutableStateFlow<MediaPlayerState> =
        MutableStateFlow(MediaPlayerState.IDLE)

    var uri: Uri? = null
        private set

    init {
//        mediaPlayer.setOnPreparedListener { stateFlow.tryEmit(PlayerState.PREPARED) }
        mediaPlayer.setOnErrorListener { _, _, _ -> stateFlow.tryEmit(MediaPlayerState.ERROR) }
        mediaPlayer.setOnCompletionListener { stateFlow.tryEmit(MediaPlayerState.COMPLETED) }
    }

    fun getState(): MediaPlayerState = stateFlow.value
    fun getStateFlow(): StateFlow<MediaPlayerState> = stateFlow.asStateFlow()

    fun play(timestamp: Long = 0L) {
        val validState = isValidState(
            MediaPlayerState.PREPARED,
            MediaPlayerState.STARTED,
            MediaPlayerState.PAUSED,
            MediaPlayerState.COMPLETED,
        )

        if (!validState) return

        mediaPlayer.seekTo(timestamp, MediaPlayer.SEEK_PREVIOUS_SYNC)
        mediaPlayer.start()
        stateFlow.tryEmit(MediaPlayerState.STARTED)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun play(
        uri: Uri,
        timestamp: Long = 0L,
        speed: Float = 1f
    ) = withContext(Dispatchers.IO) {
        val validState = isValidState(
            MediaPlayerState.IDLE,
            MediaPlayerState.RESET,
            MediaPlayerState.INIT,
            MediaPlayerState.PREPARED,
            MediaPlayerState.STARTED,
            MediaPlayerState.PAUSED,
            MediaPlayerState.STOPPED,
            MediaPlayerState.COMPLETED,
            MediaPlayerState.ERROR,
        )

        if (!validState) return@withContext

        mediaPlayer.reset()
        stateFlow.tryEmit(MediaPlayerState.RESET)

        try {
            this@SimpleMediaPlayer.uri = uri

            mediaPlayer.setDataSource(application, uri)
            mediaPlayer.prepare()
            mediaPlayer.playbackParams = mediaPlayer.playbackParams.setSpeed(speed)
            stateFlow.tryEmit(MediaPlayerState.PREPARED)

            mediaPlayer.seekTo(timestamp, MediaPlayer.SEEK_PREVIOUS_SYNC)
            mediaPlayer.start()
            stateFlow.tryEmit(MediaPlayerState.STARTED)
        } catch (e: Exception) {
            Timber.e(e)
            stateFlow.tryEmit(MediaPlayerState.ERROR)
        }
    }

    fun pause() {
        val validState = isValidState(
            MediaPlayerState.STARTED,
            MediaPlayerState.PAUSED,
            MediaPlayerState.COMPLETED,
        )

        if (!validState) return

        mediaPlayer.pause()
        stateFlow.tryEmit(MediaPlayerState.PAUSED)
    }

    fun stop() {
        val validState = isValidState(
            MediaPlayerState.PREPARED,
            MediaPlayerState.STARTED,
            MediaPlayerState.STOPPED,
            MediaPlayerState.PAUSED,
            MediaPlayerState.COMPLETED,
        )

        if (!validState) return

        mediaPlayer.stop()
        stateFlow.tryEmit(MediaPlayerState.STOPPED)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun replay() = withContext(Dispatchers.IO) {
        val validState = isValidState(
            MediaPlayerState.PREPARED,
            MediaPlayerState.STARTED,
            MediaPlayerState.STOPPED,
            MediaPlayerState.PAUSED,
            MediaPlayerState.COMPLETED,
        )

        if (!validState) return@withContext

        mediaPlayer.stop()
        mediaPlayer.prepare()
        mediaPlayer.start()
        stateFlow.tryEmit(MediaPlayerState.STARTED)
    }

    fun destroy() {
        mediaPlayer.release()
        stateFlow.tryEmit(MediaPlayerState.END)
    }

    fun setPlaybackSpeed(value: Float) {
        val validState = isValidState(
            MediaPlayerState.INIT,
            MediaPlayerState.PREPARED,
            MediaPlayerState.STARTED,
            MediaPlayerState.PAUSED,
            MediaPlayerState.COMPLETED,
            MediaPlayerState.ERROR,
        )

        if (!validState) return

        mediaPlayer.playbackParams = mediaPlayer.playbackParams.setSpeed(value)
    }

    fun getCurrentPosition(): Long {
        val validState = isValidState(
            MediaPlayerState.IDLE,
            MediaPlayerState.INIT,
            MediaPlayerState.PREPARED,
            MediaPlayerState.STARTED,
            MediaPlayerState.PAUSED,
            MediaPlayerState.STOPPED,
            MediaPlayerState.COMPLETED,
        )

        if (!validState) return 0L

        return mediaPlayer.currentPosition.toLong()
    }

    fun getDuration(): Long {
        val validState = isValidState(
            MediaPlayerState.PREPARED,
            MediaPlayerState.STARTED,
            MediaPlayerState.PAUSED,
            MediaPlayerState.STOPPED,
            MediaPlayerState.COMPLETED,
        )

        if (!validState) return 0L

        return mediaPlayer.duration.toLong()
    }

    private fun isValidState(vararg stateArgs: MediaPlayerState): Boolean {
        val currentState = stateFlow.value
        return stateArgs.find { state -> state == currentState } != null
    }
}