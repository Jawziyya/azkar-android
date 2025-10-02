package io.jawziyya.azkar.ui.hadith

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import dev.jeziellago.compose.markdowntext.MarkdownText
import io.jawziyya.azkar.R
import io.jawziyya.azkar.database.model.Hadith
import io.jawziyya.azkar.ui.theme.AppTheme
import io.jawziyya.azkar.ui.theme.components.AppBar
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Created by uvays on 23.02.2023.
 */

@Serializable
data class HadithScreen(
    val id: Long,
)

@Composable
fun HadithScreenView(
    navController: NavHostController,
    navBackStackEntry: NavBackStackEntry,
) {
    val args = navBackStackEntry.toRoute<HadithScreen>()
    val viewModel: HadithViewModel = koinViewModel(parameters = { parametersOf(args.id) })
    val hadith by viewModel.hadith.collectAsState()

    View(
        hadith = hadith,
        onBackClick = navController::popBackStack,
    )
}

@Composable
private fun View(
    hadith: Hadith?,
    onBackClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background),
    ) {
        AppBar(
            title = stringResource(R.string.hadith_title),
            onBackClick = onBackClick,
        )

        Crossfade(hadith, label = "") { value ->
            if (value != null) {
                Content(value)
            }
        }
    }
}

@Composable
private fun Content(hadith: Hadith) {
    val text = remember(hadith.text) { hadith.text.replace("*", "") }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        MarkdownText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            markdown = text,
            style = AppTheme.typography.arabic.copy(
                color = AppTheme.colors.text,
                textAlign = TextAlign.Start,
            ),
        )

        if (hadith.translation != null) {
            MarkdownText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                markdown = hadith.translation,
                style = AppTheme.typography.translation.copy(
                    color = AppTheme.colors.text,
                    textAlign = TextAlign.Start,
                ),
            )
        }

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(R.string.hadith_source_title).uppercase(),
            style = AppTheme.typography.sectionHeader,
            textAlign = TextAlign.Start,
            color = AppTheme.colors.tertiaryText,
        )

        val source = hadith.sources.firstOrNull()
        val sourceTitle = if (source == null) "" else stringResource(source.titleRes).uppercase()
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = "$sourceTitle, ${hadith.sourceExt}",
            style = AppTheme.typography.tip,
            textAlign = TextAlign.Start,
            color = AppTheme.colors.text,
        )
    }
}

@Preview
@Composable
private fun HadithScreenPreview() {
    View(
        hadith = null,
        onBackClick = remember { {} },
    )
}