package io.jawziyya.azkar.ui.hadith

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.zhuinden.simplestackcomposeintegration.core.LocalBackstack
import com.zhuinden.simplestackcomposeintegration.services.rememberService
import io.jawziyya.azkar.database.DatabaseHelper
import io.jawziyya.azkar.database.model.Hadith
import io.jawziyya.azkar.ui.core.navigation.ComposeKey
import kotlinx.parcelize.Parcelize

/**
 * Created by uvays on 23.02.2023.
 */

@Parcelize
data class HadithScreenKey(val id: Long, val title: String) : ComposeKey() {
    @Composable
    override fun ScreenComposable(modifier: Modifier) {
        val backstack = LocalBackstack.current
        val databaseHelper = rememberService<DatabaseHelper>()
        var hadith by remember { mutableStateOf<Hadith?>(null) }

        LaunchedEffect(Unit) {
            hadith = databaseHelper.getHadith(id)
        }

        HadithScreen(
            title = title,
            onBackClick = backstack::goBack,
            hadith = hadith,
        )
    }
}
