package io.jawziyya.azkar.ui.hadith

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import io.jawziyya.azkar.data.model.Hadith
import io.jawziyya.azkar.ui.theme.AppTheme
import io.jawziyya.azkar.ui.theme.component.AppBar

/**
 * Created by uvays on 23.02.2023.
 */

@Composable
fun HadithScreen(
    title: String, onBackClick: () -> Unit, hadith: Hadith?
) {
    AppTheme {
        ProvideWindowInsets {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppTheme.colors.background),
            ) {
                AppBar(
                    title = title,
                    onBackClick = onBackClick,
                )
                Crossfade(hadith) { value ->
                    if (value == null) {
                        Box(modifier = Modifier.fillMaxSize())
                    } else {
                        Content(value)
                    }
                }
            }
        }
    }
}

@Composable
private fun Content(hadith: Hadith) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding(),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .padding(16.dp),
            text = hadith.text ?: "",
            style = AppTheme.typography.arabic,
            fontSize = 28.sp,
            textAlign = TextAlign.Start,
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            text = hadith.translation ?: "",
            style = AppTheme.typography.body,
            textAlign = TextAlign.Start,
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
            translationEn = "The Prophet (ﷺ) said:\n\n*\"The most superior way of asking for forgiveness from Allah is:*\n\n**O Allaah You are my Lord, there is none worthy of worship in truth except You, You created me and I am Your slave and I am abiding to Your covenant and promise as best as I can, I seek refuge in You from the evil that I have committed, I profess to you my sins and I acknowledge Your favour upon me, so forgive me verily no one forgives sins except you.**\n\n*The Prophet (ﷺ) added. \"If somebody recites it during the day with firm faith in it, and dies on the same day before the evening, he will be from the people of Paradise; and if somebody recites it at night with firm faith in it, and dies before the morning, he will be from the people of Paradise.*\"",
            sourceRaw = "Bukhari",
            sourceExt = "6306 (80/3)",
        )
    )
}