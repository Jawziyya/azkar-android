package io.jawziyya.azkar.ui.azkar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import io.jawziyya.azkar.ui.core.BaseFragment
import com.zhuinden.simplestackextensions.fragmentsktx.lookup

/**
 * Created by uvays on 07.06.2022.
 */

class AzkarFragment : BaseFragment() {

    private val viewModel by lazy { lookup<AzkarViewModel>() }

    override fun setContent(view: ComposeView) {
        view.setContent {
            val title by viewModel.titleLiveData.observeAsState()
            val azkarList by viewModel.zikrListLiveData.observeAsState()

            AzkarScreen(
                title = title ?: "",
                zikrList = azkarList ?: emptyList(),
                onZikrClick = viewModel::onZikrClick,
                onBackClick = requireActivity()::onBackPressed
            )
        }
    }
}