package io.jawziyya.azkar.data.helper

import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onCompletion

inline fun <reified T> SharedPreferences.observeKey(key: String, default: T): Flow<T> {
    val flow = MutableStateFlow(getItem(key, default))

    val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, k ->
        if (key == k) {
            flow.value = getItem(key, default)!!
        }
    }
    registerOnSharedPreferenceChangeListener(listener)

    return flow.onCompletion { unregisterOnSharedPreferenceChangeListener(listener) }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T> SharedPreferences.getItem(key: String, default: T): T = when (default) {
    is String -> getString(key, default) as T
    is Int -> getInt(key, default) as T
    is Long -> getLong(key, default) as T
    is Boolean -> getBoolean(key, default) as T
    is Float -> getFloat(key, default) as T
    is Set<*> -> getStringSet(key, default as Set<String>) as T
    else -> throw IllegalArgumentException("generic type not handle ${T::class.java.name}")
}