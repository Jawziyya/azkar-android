package io.jawziyya.azkar.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.zhuinden.simplestackextensions.fragmentsktx.lookup
import io.jawziyya.azkar.ui.core.BaseFragment

class SettingsFragment : BaseFragment() {

    private val viewModel: SettingsViewModel by lazy { lookup() }

    @Composable
    override fun Content() {
        val items by viewModel.itemsFlow.collectAsState()

        SettingsScreen(
            onBackClick = requireActivity()::onBackPressed,
            items = items,
            onItemClick = viewModel::onItemClick,
        )
    }
}