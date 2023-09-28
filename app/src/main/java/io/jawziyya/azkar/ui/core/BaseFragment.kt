package io.jawziyya.azkar.ui.core

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensions.fragmentsktx.lookup
import io.jawziyya.azkar.ui.theme.AppTheme

/**
 * Created by uvays on 05.06.2022.
 */

abstract class BaseFragment : KeyedFragment() {

    protected val sharedPreferences: SharedPreferences by lazy { lookup() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = createComposeView(requireContext()).also { view ->
        view.setContent {
            AppTheme {
                Content()
            }
        }
    }

    @Composable
    abstract fun Content()
}