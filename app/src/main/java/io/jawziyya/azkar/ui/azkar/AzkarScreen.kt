package io.jawziyya.azkar.ui.azkar

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import io.jawziyya.azkar.data.model.Zikr
import io.jawziyya.azkar.ui.core.rippleClickable
import io.jawziyya.azkar.ui.theme.AppTheme
import io.jawziyya.azkar.ui.theme.colorGray
import io.jawziyya.azkar.ui.theme.component.AppBar

/**
 * Created by uvays on 07.06.2022.
 */

@Composable
fun AzkarScreen(
    title: String,
    zikrList: List<Zikr>,
    onZikrClick: (Int, Zikr) -> Unit,
    onBackClick: () -> Unit,
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
                Crossfade(targetState = zikrList.isEmpty()) { isEmpty ->
                    if (isEmpty) {
                        Box(modifier = Modifier.fillMaxSize())
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = rememberInsetsPaddingValues(
                                insets = LocalWindowInsets.current.navigationBars
                            ),
                        ) {
                            itemsIndexed(zikrList) { index, item ->
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .rippleClickable { onZikrClick(index, item) }
                                        .padding(16.dp),
                                    text = item.title,
                                    style = AppTheme.typography.title,
                                )

                                if (index != zikrList.lastIndex) {
                                    Spacer(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(1.dp)
                                            .background(colorGray),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun AzkarScreenPreview() {
    AzkarScreen(
        title = "????????????????",
        zikrList = emptyList(),
        onZikrClick = { _, _ -> },
        onBackClick = {},
    )
}