package io.jawziyya.azkar.ui.azkarlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.jawziyya.azkar.database.DatabaseHelper
import io.jawziyya.azkar.database.model.Azkar
import io.jawziyya.azkar.database.model.AzkarCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AzkarListViewModel(
    private val azkarCategory: AzkarCategory,
    private val databaseHelper: DatabaseHelper,
) : ViewModel() {

    private val _azkarListFlow: MutableStateFlow<List<Azkar>> = MutableStateFlow(emptyList())
    val azkarListFlow: StateFlow<List<Azkar>> get() = _azkarListFlow.asStateFlow()

    init {
        viewModelScope.launch {
            _azkarListFlow.value = databaseHelper.getAzkarList(azkarCategory)
        }
    }
}