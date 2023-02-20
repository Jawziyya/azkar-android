package io.jawziyya.azkar.data.helper

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

/**
 * Created by uvays on 09.06.2022.
 */

fun intervalFlow(initialDelay: Long, period: Long) = flow {
    delay(initialDelay)
    while (true) {
        emit(Unit)
        delay(period)
    }
}