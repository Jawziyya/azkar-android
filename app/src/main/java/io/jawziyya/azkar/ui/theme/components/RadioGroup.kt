package io.jawziyya.azkar.ui.theme.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.ContentAlpha
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.jawziyya.azkar.ui.core.rippleClickable
import io.jawziyya.azkar.ui.theme.AppTheme

@Composable
fun RadioGroup(
    modifier: Modifier = Modifier,
    radioButtons: Array<Pair<String, Boolean>>,
    onClick: (Int) -> Unit,
) {
    Column(
        modifier = modifier.selectableGroup(),
    ) {
        repeat(radioButtons.size) { index ->
            val (title, selected) = radioButtons[index]

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .rippleClickable { onClick(index) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    style = AppTheme.typography.subtitle,
                    text = title,
                    textAlign = TextAlign.Start,
                    color = AppTheme.colors.text,
                )

                RadioButton(
                    selected = selected,
                    onClick = null,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = AppTheme.colors.accent,
                        unselectedColor = AppTheme.colors.accent.copy(alpha = 0.6f),
                        disabledColor = AppTheme.colors.accent.copy(alpha = ContentAlpha.disabled)
                    ),
                )
            }
        }
    }
}

@Preview
@Composable
private fun RadioGroupPreview() {
    RadioGroup(
        radioButtons = arrayOf(
            "Выкл" to false,
            "Вкл" to false,
            "Авто" to true,
        ),
        onClick = {}
    )
}