package io.jawziyya.azkar.ui.core.navigation

import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider

/**
 * Created by uvays on 02.09.2021.
 */

abstract class FragmentKey : DefaultFragmentKey(), DefaultServiceProvider.HasServices {
    override fun getFragmentTag(): String = toString()
    override fun getScopeTag(): String = fragmentTag
    override fun bindServices(serviceBinder: ServiceBinder) {}
    open val bottomSheet: Boolean = false
}