package io.jawziyya.azkar.ui.theme.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.jawziyya.azkar.ui.core.rippleClickable
import io.jawziyya.azkar.ui.theme.AppTheme

@Composable
fun AppSwitch(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(),
    text: String,
    checked: Boolean,
    onCheckedChange: () -> Unit,
) {
    Row(
        modifier = modifier
            .rippleClickable(onCheckedChange)
            .padding(padding),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = text,
            style = AppTheme.typography.title,
            color = AppTheme.colors.text,
        )
        Switch(
            checked = checked,
            onCheckedChange = remember { { onCheckedChange() } },
            colors = SwitchDefaults.colors(
                checkedThumbColor = AppTheme.colors.alternativeAccent,
            )
        )
    }
}