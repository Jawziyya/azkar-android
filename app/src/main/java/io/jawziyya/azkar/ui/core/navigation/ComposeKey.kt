package io.jawziyya.azkar.ui.core.navigation

import android.os.Parcelable
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackcomposeintegration.core.DefaultComposeKey
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider

abstract class ComposeKey : DefaultComposeKey(), Parcelable, DefaultServiceProvider.HasServices {
    override val saveableStateProviderKey: Any = this
    override fun getScopeTag(): String = toString()
    override fun bindServices(serviceBinder: ServiceBinder) {}
}
