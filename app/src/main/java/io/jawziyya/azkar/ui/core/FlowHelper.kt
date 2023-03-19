package io.jawziyya.azkar.ui.core

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

/**
 * Created by uvays on 25.02.2023.
 */

fun intervalFlow(initialDelay: Long, period: Long) = flow {
    delay(initialDelay)
    while (true) {
        emit(Unit)
        delay(period)
    }
}