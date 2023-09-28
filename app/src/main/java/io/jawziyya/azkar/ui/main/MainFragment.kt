package io.jawziyya.azkar.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.zhuinden.simplestackextensions.fragmentsktx.lookup
import io.jawziyya.azkar.ui.core.BaseFragment

/**
 * Created by uvays on 05.06.2022.
 */

class MainFragment : BaseFragment() {

    private val viewModel by lazy { lookup<MainViewModel>() }

    @Composable
    override fun Content() {
        val fudul by viewModel.fadailFlow.collectAsState()

        MainScreen(
            onAzkarCategoryClick = viewModel::onAzkarCategoryClick,
            onSettingsClick = viewModel::onSettingsClick,
            fadail = fudul,
        )
    }
}