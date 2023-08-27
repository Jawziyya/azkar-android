package io.jawziyya.azkar.ui.main

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import io.jawziyya.azkar.ui.core.BaseFragment
import com.zhuinden.simplestackextensions.fragmentsktx.lookup

/**
 * Created by uvays on 05.06.2022.
 */

class MainFragment : BaseFragment() {

    private val viewModel by lazy { lookup<MainViewModel>() }

    override fun setContent(view: ComposeView) {
        view.setContent {
            val fudul by viewModel.fadailFlow.collectAsState()

            MainScreen(
                onAzkarCategoryClick = viewModel::onAzkarCategoryClick,
                onSettingsClick = viewModel::onSettingsClick,
                fadail = fudul,
            )
        }
    }
}