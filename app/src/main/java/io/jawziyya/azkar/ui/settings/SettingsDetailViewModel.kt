package io.jawziyya.azkar.ui.settings

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import io.jawziyya.azkar.data.helper.observeKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsDetailViewModel(
    private val sharedPreferencesKey: String,
    private val titles: List<String>,
    private val values: List<String>,
    private val defaultValueIndex: Int,
    private val sharedPreferences: SharedPreferences,
) : ViewModel() {

    val optionsFlow: Flow<Array<Pair<String, Boolean>>>
        get() = sharedPreferences.observeKey(sharedPreferencesKey, values[defaultValueIndex])
            .map { selectedValue ->
                values.mapIndexed { index, value -> Pair(titles[index], value == selectedValue) }
                    .toTypedArray()
            }

    fun onOptionClick(index: Int) {
        sharedPreferences.edit { putString(sharedPreferencesKey, values[index]) }
    }
}