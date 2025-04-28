package io.jawziyya.azkar.ui.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

abstract class BaseViewModel {

    protected val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

//    override fun onServiceRegistered() {}
//
//    override fun onServiceUnregistered() {
//        coroutineScope.coroutineContext.cancelChildren()
//    }
}