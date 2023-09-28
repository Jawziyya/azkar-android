package io.jawziyya.azkar.ui.hadith

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.zhuinden.simplestackextensions.fragmentsktx.lookup
import io.jawziyya.azkar.ui.core.BaseFragment

/**
 * Created by uvays on 23.02.2023.
 */

class HadithFragment : BaseFragment() {

    private val viewModel by lazy { lookup<HadithViewModel>() }

    @Composable
    override fun Content() {
        val screenKey = remember { getKey<HadithScreenKey>() }
        val hadith by viewModel.hadithFlow.collectAsState()

        HadithScreen(
            title = screenKey.title,
            onBackClick = requireActivity()::onBackPressed,
            hadith = hadith,
        )
    }
}