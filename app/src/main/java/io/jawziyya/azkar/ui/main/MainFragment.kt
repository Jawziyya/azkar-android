package io.jawziyya.azkar.ui.main

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
            MainScreen(
                onAzkarCategoryClick = viewModel::onAzkarCategoryClick,
            )
        }
    }
}