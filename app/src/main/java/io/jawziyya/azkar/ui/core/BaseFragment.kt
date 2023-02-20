package io.jawziyya.azkar.ui.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import com.zhuinden.simplestackextensions.fragments.KeyedFragment

/**
 * Created by uvays on 05.06.2022.
 */

abstract class BaseFragment : KeyedFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = createComposeView(requireContext()).also { setContent(it) }

    abstract fun setContent(view: ComposeView)
}