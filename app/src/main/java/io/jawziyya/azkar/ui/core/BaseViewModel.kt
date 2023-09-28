package io.jawziyya.azkar.ui.core

import com.zhuinden.simplestack.ScopedServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren

abstract class BaseViewModel: ScopedServices.Registered {

    protected val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onServiceUnregistered() {
        coroutineScope.coroutineContext.cancelChildren()
    }
}