package io.jawziyya.azkar.ui.core

import com.zhuinden.simplestack.ScopedServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel: ScopedServices.Registered {

    protected val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onServiceUnregistered() {
        coroutineScope.coroutineContext.cancelChildren()
    }
}