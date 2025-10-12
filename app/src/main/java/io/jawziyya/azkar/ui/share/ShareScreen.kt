package io.jawziyya.azkar.ui.share

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import io.jawziyya.azkar.R
import io.jawziyya.azkar.ui.theme.AppTheme
import io.jawziyya.azkar.ui.theme.components.AppBar
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber


@Serializable
data class ShareScreen(
    val azkarId: Long,
)

@Composable
fun ShareScreenView(
    navController: NavHostController,
    navBackStackEntry: NavBackStackEntry,
) {
    val args = navBackStackEntry.toRoute<ShareScreen>()
    val viewModel: ShareViewModel = koinViewModel(
        parameters = {
            parametersOf(
                args.azkarId,
            )
        }
    )

    val state by viewModel.state.collectAsState()

    ShareScreenViewContent(
        state = state,
        onEvent = viewModel::onEvent,
        onBackClick = { navController.popBackStack() },
    )
}

@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalComposeApi::class,
    ExperimentalPermissionsApi::class
)
@Composable
private fun ShareScreenViewContent(
    state: ShareScreenState,
    onEvent: (ShareScreenEvent) -> Unit,
    onBackClick: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val captureController = rememberCaptureController()
    val context = LocalContext.current
    var singleOptionShown by remember { mutableStateOf(false) }
    val options = remember { ShareOption.entries.map { context.resources.getString(it.titleRes) } }

    BackHandler(enabled = singleOptionShown) {
        singleOptionShown = false
    }

    val storagePermissionState =
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            null
        } else {
            rememberPermissionState(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppTheme.colors.background)
        ) {
            AppBar(
                title = "Поделиться",
                onBackClick = onBackClick,
                backDrawableRes = R.drawable.ic_close_24,
                onLeadingIconClick = {
                    singleOptionShown = true
                },
                leadingDrawableRes = R.drawable.ic_share_24,
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
            ) {
                Crossfade(state.azkar) { azkar ->
                    if (azkar == null) {
                        return@Crossfade
                    }

                    Column(
                        modifier = Modifier
                            .capturable(captureController)
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(AppTheme.colors.alternativeAccent)
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                                .padding(horizontal = 16.dp),
                            text = azkar.text,
                            style = AppTheme.typography.arabic,
                            textAlign = TextAlign.Start,
                            color = AppTheme.colors.text,
                        )

                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            text = azkar.translation ?: "",
                            style = AppTheme.typography.translation,
                            textAlign = TextAlign.Start,
                            color = AppTheme.colors.text,
                        )

                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            text = azkar.transliteration ?: "",
                            style = AppTheme.typography.translation,
                            textAlign = TextAlign.Start,
                            color = AppTheme.colors.text,
                        )
                    }
                }
            }
        }

        SingleOptionModalBottomSheet(
            visible = singleOptionShown,
            onDismissRequest = {
                singleOptionShown = false
            },
            items = options,
            onItemClick = { index ->
                when (ShareOption.entries[index]) {
                    ShareOption.Default -> {
                        scope.launch {
                            try {
                                val bitmap =
                                    captureController.captureAsync().await().asAndroidBitmap()
                                onEvent(ShareScreenEvent.OnDefaultShare(bitmap))
                            } catch (e: Exception) {
                                Timber.e(e)
                            }
                        }
                    }

                    ShareOption.Gallery -> {
                        if (storagePermissionState?.status?.isGranted == false) {
                            storagePermissionState.launchPermissionRequest()
                        } else {
                            scope.launch {
                                try {
                                    val bitmap = captureController.captureAsync().await()
                                        .asAndroidBitmap()
                                    onEvent(ShareScreenEvent.OnDefaultShare(bitmap))
                                } catch (e: Exception) {
                                    Timber.e(e)
                                }
                            }
                        }
                    }
                }

                singleOptionShown = false
            },
        )
    }
}

@Preview
@Composable
private fun ShareScreenPreview() {
    ShareScreenViewContent(
        state = ShareScreenState(),
        onEvent = {},
        onBackClick = {},
    )
}