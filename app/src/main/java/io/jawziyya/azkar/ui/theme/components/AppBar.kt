package io.jawziyya.azkar.ui.theme.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    statusBarsPadding: Boolean = true,
    onBackClick: (() -> Unit)? = null,
    @DrawableRes backDrawableRes: Int = R.drawable.ic_arrow_back_24,
    onLeadingIconClick: (() -> Unit)? = null,
    @DrawableRes leadingDrawableRes: Int? = null,
) {
    val statusBarsPaddingModifier =
        if (statusBarsPadding) Modifier.statusBarsPadding() else Modifier

    Column {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(AppTheme.colors.background)
                .then(statusBarsPaddingModifier),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onBackClick != null) {
                Image(
                    modifier = Modifier
                        .clip(CircleShape)
                        .rippleClickable(onBackClick)
                        .padding(16.dp),
                    painter = painterResource(backDrawableRes),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(AppTheme.colors.icon),
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

            if (onLeadingIconClick == null || leadingDrawableRes == null) {
                Spacer(modifier = Modifier.size(56.dp))
            } else {
                Image(
                    modifier = Modifier
                        .clip(CircleShape)
                        .rippleClickable(onLeadingIconClick)
                        .padding(16.dp),
                    painter = painterResource(leadingDrawableRes),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(AppTheme.colors.icon),
                )
            }
        }
        HorizontalDivider(
            color = AppTheme.colors.divider,
        )
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