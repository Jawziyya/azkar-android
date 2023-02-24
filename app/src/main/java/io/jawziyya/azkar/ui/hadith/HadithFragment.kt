package io.jawziyya.azkar.ui.hadith

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import com.zhuinden.simplestackextensions.fragmentsktx.lookup
import io.jawziyya.azkar.ui.core.BaseFragment

/**
 * Created by uvays on 23.02.2023.
 */

class HadithFragment : BaseFragment() {

    private val viewModel by lazy { lookup<HadithViewModel>() }

    override fun setContent(view: ComposeView) {
        val screenKey = getKey<HadithScreenKey>()

        view.setContent {
            val hadith by viewModel.hadithFlow.collectAsState()

            HadithScreen(
                title = screenKey.title,
                onBackClick = requireActivity()::onBackPressed,
                hadith = hadith,
            )
        }
    }
}