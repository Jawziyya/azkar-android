package io.jawziyya.azkar.data.repository

data class CounterData(
    val timestamp: Long,
    val map: HashMap<Long, Int>,
)