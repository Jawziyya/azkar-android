package io.jawziyya.azkar.ui.hadith

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jeziellago.compose.markdowntext.MarkdownText
import io.jawziyya.azkar.R
import io.jawziyya.azkar.database.model.Hadith
import io.jawziyya.azkar.database.model.Source
import io.jawziyya.azkar.ui.theme.AppTheme
import io.jawziyya.azkar.ui.theme.component.AppBar

/**
 * Created by uvays on 23.02.2023.
 */

@Composable
fun HadithScreen(
    title: String,
    onBackClick: () -> Unit,
    hadith: Hadith?,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background),
    ) {
        AppBar(
            title = title,
            onBackClick = onBackClick,
        )
        Crossfade(hadith, label = "") { value ->
            if (value == null) return@Crossfade

            Content(value)
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
            .navigationBarsPadding(),
    ) {
        MarkdownText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .padding(16.dp),
            markdown = text,
            style = AppTheme.typography.arabic,
            fontSize = 24.sp,
            textAlign = TextAlign.Start,
            color = AppTheme.colors.text,
        )

        if (hadith.translation != null) {
            MarkdownText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                markdown = hadith.translation,
                style = AppTheme.typography.body,
                textAlign = TextAlign.Start,
                color = AppTheme.colors.text,
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
    HadithScreen(
        title = "Аль-Бухари",
        onBackClick = remember { {} },
        hadith = Hadith(
            id = 1,
            text = "عَنْ شَدَّادُ بْنُ أَوْسٍ ـ رضى الله عنه ـ عَنِ النَّبِيِّ صلى الله عليه وسلم\n**«سَيِّدُ الاِسْتِغْفَارِ أَنْ تَقُولَ اللَّهُمَّ أَنْتَ رَبِّي، لاَ إِلَهَ إِلاَّ أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَىَّ وَأَبُوءُ لَكَ بِذَنْبِي، فَاغْفِرْ لِي، فَإِنَّهُ لاَ يَغْفِرُ الذُّنُوبَ إِلاَّ أَنْتَ**\". قَالَ \"**وَمَنْ قَالَهَا مِنَ النَّهَارِ مُوقِنًا بِهَا، فَمَاتَ مِنْ يَوْمِهِ قَبْلَ أَنْ يُمْسِيَ، فَهُوَ مِنْ أَهْلِ الْجَنَّةِ، وَمَنْ قَالَهَا مِنَ اللَّيْلِ وَهْوَ مُوقِنٌ بِهَا، فَمَاتَ قَبْلَ أَنْ يُصْبِحَ، فَهْوَ مِنْ أَهْلِ الْجَنَّةِ»**.",
            translation = "Передают со слов Шаддада ибн Ауса, да будет доволен им Аллах, что (однажды) Пророк, да благословит его Аллах и приветствует, (сказал):\n\n*«Господином обращений к Аллаху с мольбами о прощении являются (такие слова,) когда ты говоришь:* **\"О Аллах, Ты — Господь мой, и нет божества достойного поклонения, кроме Тебя. Ты создал меня, а я — Твой раб, и я буду хранить верность Тебе, пока у меня хватит сил. Прибегаю к Твоей защите от зла того, что я сделал, признаю милость, оказанную Тобой мне, и признаю грех свой, прости же меня, ибо, поистине, никто не прощает грехов, кроме Тебя!\"»**\n\n(Сказав же это, Пророк, да благословит его Аллах и приветствует,) добавил: *«Тот, кто станет повторять (эти слова) днём, будучи убеждённым (в том, что он говорит), и умрёт в тот же день до наступления вечера, окажется среди обитателей Рая, и тот, кто станет повторять (эти слова) ночью, будучи убеждённым (в том, что он говорит), и умрёт (в ту же ночь) до наступления утра, окажется среди обитателей Рая»*.",
            sources = listOf(Source.BUKHARI),
            sourceExt = "6306 (80/3)",
        )
    )
}