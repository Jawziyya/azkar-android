package io.jawziyya.azkar.ui.theme.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.jawziyya.azkar.R
import io.jawziyya.azkar.ui.core.rippleClickable
import io.jawziyya.azkar.ui.theme.AppTheme

/**
 * Created by uvays on 22.01.2023.
 */

@Composable
fun AppBar(
    modifier: Modifier = Modifier,
    title: String,
    onBackClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp)
            .background(AppTheme.colors.background)
            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBackClick != null) {
            val iconColor = if (isSystemInDarkTheme()) Color.White else Color.Black
            Image(
                modifier = Modifier
                    .clip(CircleShape)
                    .rippleClickable(onBackClick)
                    .padding(16.dp),
                painter = painterResource(R.drawable.ic_arrow_back_24),
                contentDescription = null,
                colorFilter = ColorFilter.tint(iconColor),
            )
        } else {
            Spacer(modifier = Modifier.size(56.dp))
        }

        Text(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 16.dp),
            style = AppTheme.typography.header,
            text = title,
            maxLines = 1,
            textAlign = TextAlign.Center,
            color = AppTheme.colors.text,
        )

        Spacer(modifier = Modifier.size(56.dp))
    }
}

@Preview
@Composable
fun AppBarPreview() {
    AppBar(
        title = stringResource(R.string.app_name),
    )
}

@Preview
@Composable
fun AppBarBackButtonPreview() {
    AppBar(
        title = stringResource(R.string.app_name),
        onBackClick = {}
    )
}