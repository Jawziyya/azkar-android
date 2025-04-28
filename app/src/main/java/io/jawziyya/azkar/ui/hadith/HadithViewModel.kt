package io.jawziyya.azkar.ui.hadith

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.jawziyya.azkar.database.DatabaseHelper
import io.jawziyya.azkar.database.model.Hadith
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HadithViewModel(
    private val hadithId: Long,
    private val databaseHelper: DatabaseHelper,
) : ViewModel() {

    private val _hadithFlow: MutableStateFlow<Hadith?> = MutableStateFlow(null)
    val hadith: StateFlow<Hadith?> get() = _hadithFlow.asStateFlow()

    init {
        viewModelScope.launch {
            _hadithFlow.value = databaseHelper.getHadith(hadithId)
        }
    }
}