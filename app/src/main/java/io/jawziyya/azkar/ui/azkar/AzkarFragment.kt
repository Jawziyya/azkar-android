package io.jawziyya.azkar.ui.azkar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.zhuinden.simplestackextensions.fragmentsktx.lookup
import io.jawziyya.azkar.ui.core.BaseFragment

/**
 * Created by uvays on 07.06.2022.
 */

class AzkarFragment : BaseFragment() {

    private val viewModel by lazy { lookup<AzkarViewModel>() }

    @Composable
    override fun Content() {
        val title by viewModel.titleLiveData.observeAsState()
        val azkarList by viewModel.zikrListLiveData.observeAsState()

        AzkarScreen(
            title = title ?: "",
            zikrList = azkarList ?: emptyList(),
            onZikrClick = viewModel::onZikrClick,
            onBackClick = requireActivity()::onBackPressed,
        )
    }
}