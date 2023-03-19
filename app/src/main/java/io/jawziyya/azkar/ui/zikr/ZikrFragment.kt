package io.jawziyya.azkar.ui.zikr

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import io.jawziyya.azkar.ui.core.BaseFragment
import com.zhuinden.simplestackextensions.fragmentsktx.lookup
import timber.log.Timber

/**
 * Created by uvays on 07.06.2022.
 */

class ZikrFragment : BaseFragment() {

    private val viewModel by lazy { lookup<ZikrViewModel>() }

    override fun setContent(view: ComposeView) {
        val screenKey = getKey<ZikrScreenKey>()

        view.setContent {
            val azkarList by viewModel.zikrFlow.collectAsState()
            val translationVisible by viewModel.translationVisibleFlow.collectAsState(true)
            val transliterationVisible by viewModel.transliterationVisibleFlow.collectAsState(true)
            val playerState by viewModel.playerStateFlow.collectAsState()
            val audioPlaybackSpeed by viewModel.audioPlaybackSpeedFlow.collectAsState()

            ZikrScreen(
                onBackClick = requireActivity()::onBackPressed,
                azkarCategory = screenKey.azkarCategory,
                azkarIndex = screenKey.azkarIndex,
                zikrList = azkarList,
                translationVisible = translationVisible,
                onTranslationVisibilityChange = viewModel::onTranslationVisibilityChange,
                transliterationVisible = transliterationVisible,
                onTransliterationVisibilityChange = viewModel::onTransliterationVisibilityChange,
                zikrPlayerState = playerState,
                onReplay = viewModel::onReplayClick,
                onPlayClick = viewModel::onPlayClick,
                onAudioPlaybackSpeedChange = viewModel::onAudioPlaybackSpeedChangeClick,
                audioPlaybackSpeed = audioPlaybackSpeed,
                onPageChange = viewModel::onPageChange,
                onHadithClick = viewModel::onHadithClick,
            )
        }
    }
}