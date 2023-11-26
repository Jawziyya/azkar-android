package io.jawziyya.azkar.ui.azkarpager

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackcomposeintegration.core.LocalBackstack
import com.zhuinden.simplestackcomposeintegration.services.rememberService
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.lookup
import io.jawziyya.azkar.data.repository.FileRepository
import io.jawziyya.azkar.database.model.AzkarCategory
import io.jawziyya.azkar.ui.core.SimpleMediaPlayer
import io.jawziyya.azkar.ui.core.navigation.ComposeKey
import kotlinx.parcelize.Parcelize

/**
 * Created by uvays on 04.02.2023.
 */

@Immutable
@Parcelize
data class AzkarPagerScreenKey(
    private val azkarCategoryName: String,
    val azkarIndex: Int,
) : ComposeKey() {
    constructor(
        azkarCategory: AzkarCategory,
        azkarIndex: Int,
    ) : this(
        azkarCategoryName = azkarCategory.name,
        azkarIndex = azkarIndex,
    )

    val azkarCategory: AzkarCategory get() = AzkarCategory.valueOf(azkarCategoryName)

    override fun bindServices(serviceBinder: ServiceBinder) {
        super.bindServices(serviceBinder)
        with(serviceBinder) {
            val application = lookup<Application>()

            add(
                AzkarPagerViewModel(
                    screenKey = this@AzkarPagerScreenKey,
                    backstack = backstack,
                    application = application,
                    databaseHelper = lookup(),
                    fileRepository = FileRepository(application = application),
                    simpleMediaPlayer = SimpleMediaPlayer(application),
                    sharedPreferences = lookup(),
                    azkarCounterRepository = lookup(),
                )
            )
        }
    }

    @Composable
    override fun ScreenComposable(modifier: Modifier) {
        val backstack = LocalBackstack.current
        val viewModel = rememberService<AzkarPagerViewModel>()
        val azkarCategory by viewModel.azkarCategoryFlow.collectAsState()
        val azkarIndex by viewModel.azkarIndexFlow.collectAsState()
        val azkarList by viewModel.azkarListFlow.collectAsState()
        val translationVisible by viewModel.translationVisibleFlow.collectAsState(true)
        val transliterationVisible by viewModel.transliterationVisibleFlow.collectAsState(true)
        val playerState by viewModel.playerStateFlow.collectAsState()
        val audioPlaybackSpeed by viewModel.audioPlaybackSpeedFlow.collectAsState()

        AzkarPagerScreen(
            onBackClick = backstack::goBack,
            azkarCategory = azkarCategory,
            azkarIndex = azkarIndex,
            azkarList = azkarList,
            translationVisible = translationVisible,
            onTranslationVisibilityChange = viewModel::onTranslationVisibilityChange,
            transliterationVisible = transliterationVisible,
            onTransliterationVisibilityChange = viewModel::onTransliterationVisibilityChange,
            azkarPlayerState = playerState,
            onReplay = viewModel::onReplayClick,
            onPlayClick = viewModel::onPlayClick,
            onAudioPlaybackSpeedChange = viewModel::onAudioPlaybackSpeedChangeClick,
            audioPlaybackSpeed = audioPlaybackSpeed,
            onPageChange = viewModel::onPageChange,
            onHadithClick = viewModel::onHadithClick,
            onCounterClick = viewModel::onCounterClick,
        )
    }
}
