@file:OptIn(ExperimentalMaterial3Api::class)

package io.jawziyya.azkar.ui.share

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import io.jawziyya.azkar.ui.core.rippleClickable
import io.jawziyya.azkar.ui.theme.AppTheme
import kotlinx.coroutines.launch

@Composable
fun SingleOptionModalBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    items: List<String>,
    onItemClick: (Int) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(visible) {
        if (visible) {
            bottomSheetState.expand()
        } else {
            bottomSheetState.hide()
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            scope.launch {
                bottomSheetState.hide()
                onDismissRequest()
            }
        },
        sheetState = bottomSheetState,
        shape = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            items.fastForEachIndexed { index, item ->
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .rippleClickable { onItemClick(index) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    text = item,
                    style = AppTheme.typography.body,
                    color = AppTheme.colors.text,
                )
            }
        }
    }
}

@Preview
@Composable
private fun ShareOptionsBottomSheetPreview() {
    SingleOptionModalBottomSheet(
        visible = true,
        onDismissRequest = {},
        items = ShareOption.entries.map { stringResource(it.titleRes) },
        onItemClick = {},
    )
}
