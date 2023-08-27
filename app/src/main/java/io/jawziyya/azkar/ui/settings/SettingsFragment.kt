package io.jawziyya.azkar.ui.settings

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import com.zhuinden.simplestackextensions.fragmentsktx.lookup
import io.jawziyya.azkar.ui.core.BaseFragment

class SettingsFragment : BaseFragment() {

    private val viewModel: SettingsViewModel by lazy { lookup() }

    override fun setContent(view: ComposeView) {
        view.setContent {
            val items by viewModel.itemsFlow.collectAsState()

//            SettingsScreen(
//                onBackClick = requireActivity()::onBackPressed,
//                items = items,
//                onItemClick = viewModel::onItemClick,
//            )

            WebScreen(
                url = "https://www.google.com",
                onCloseClick = requireActivity()::onBackPressed
            )
        }
    }
}