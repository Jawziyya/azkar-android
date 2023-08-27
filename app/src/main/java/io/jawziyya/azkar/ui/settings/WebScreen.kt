package io.jawziyya.azkar.ui.settings

import android.net.Uri
import android.net.http.SslError
import android.view.ViewGroup
import android.webkit.*
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.statusBarsPadding
import io.jawziyya.azkar.ui.theme.AppTheme

/**
 * Created by uvays on 20.07.2022.
 */

@Composable
fun WebScreen(
    url: String?,
    withCookies: Boolean = true,
    ignoreSslError: Boolean = false,
    onCloseClick: () -> Unit,
    onHandleUrlLoad: (uri: Uri?) -> Boolean = { false },
) {
    AppTheme {
        ProvideWindowInsets {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
                topBar = {
                    TopAppBar(
                        title = {},
                        backgroundColor = AppTheme.colors.contentBackground,
                        navigationIcon = {
                            IconButton(onClick = onCloseClick) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = null,
                                    tint = AppTheme.colors.tertiaryText,
                                )
                            }
                        }
                    )
                },
            ) { padding ->
                if (url != null) {
                    Content(
                        modifier = Modifier.padding(padding),
                        withCookies = withCookies,
                        ignoreSslError = ignoreSslError,
                        url = url,
                        onHandleUrlLoad = onHandleUrlLoad,
                    )
                }
            }
        }
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    withCookies: Boolean,
    ignoreSslError: Boolean,
    url: String,
    onHandleUrlLoad: (uri: Uri?) -> Boolean,
) {
    var progress by remember { mutableStateOf(0) }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                val webView = WebView(context)

                webView.clipToOutline = true
                webView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                val initCallback: (Boolean) -> Unit = {
                    webView.settings.javaScriptEnabled = true
                    webView.webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            val handled = onHandleUrlLoad(request?.url)
                            return handled || super.shouldOverrideUrlLoading(
                                view,
                                request
                            )
                        }

                        override fun onReceivedSslError(
                            view: WebView?,
                            handler: SslErrorHandler?,
                            error: SslError?
                        ) {
                            if (ignoreSslError) {
                                handler?.proceed()
                            } else {
                                super.onReceivedSslError(view, handler, error)
                            }
                        }
                    }
                    webView.webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(
                            view: WebView?,
                            newProgress: Int
                        ) {
                            super.onProgressChanged(view, newProgress)
                            progress = newProgress
                        }
                    }

                    webView.loadUrl(url)
                }

                if (withCookies) {
                    initCallback(true)
                } else {
                    CookieSyncManager.createInstance(context)
                    CookieManager.getInstance().removeAllCookies(initCallback)
                }

                return@AndroidView webView
            }
        )
        Crossfade(targetState = progress < 100, label = "") { visible ->
            if (visible) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    progress = progress / 100f,
                    color = AppTheme.colors.accent,
                )
            }
        }
    }
}

@Preview
@Composable
private fun WebRedirectScreenPreview() {
    WebScreen(
        url = "https://google.com",
        onCloseClick = {},
        onHandleUrlLoad = { false },
    )
}