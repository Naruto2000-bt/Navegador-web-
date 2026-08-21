package com.example.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.entity.ExtensionEntity
import com.example.extensions.ExtensionEngine
import com.example.model.BrowserTab
import com.example.ui.components.FindInPageBar
import com.example.ui.components.OmnibarSearch
import com.example.viewmodel.WebViewAction

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserScreen(
    modifier: Modifier = Modifier,
    currentTab: BrowserTab,
    extensions: List<ExtensionEntity>,
    webViewAction: WebViewAction?,
    showFindInPage: Boolean,
    findQuery: String,
    onFindQueryChange: (String) -> Unit,
    onCloseFindInPage: () -> Unit,
    onUpdateTab: (url: String?, title: String?, faviconUrl: String?, isLoading: Boolean?, progress: Float?, canGoBack: Boolean?, canGoForward: Boolean?, isSecure: Boolean?) -> Unit,
    onNavigate: (String) -> Unit,
    onRecordHistory: (String, String) -> Unit,
    onClearWebViewAction: () -> Unit,
    onOpenExtensionPopup: () -> Unit,
    onAdBlocked: () -> Unit
) {
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    // Intercept back button to navigate history within webview if possible
    BackHandler(enabled = currentTab.canGoBack && !currentTab.isHomePage) {
        webViewInstance?.goBack()
    }

    // Handle WebViewActions
    LaunchedEffect(webViewAction) {
        val wv = webViewInstance ?: return@LaunchedEffect
        when (webViewAction) {
            is WebViewAction.GoBack -> if (wv.canGoBack()) wv.goBack()
            is WebViewAction.GoForward -> if (wv.canGoForward()) wv.goForward()
            is WebViewAction.Reload -> wv.reload()
            is WebViewAction.Stop -> wv.stopLoading()
            is WebViewAction.TriggerReaderMode -> ExtensionEngine.triggerReaderMode(wv)
            is WebViewAction.TriggerPiP -> ExtensionEngine.togglePictureInPicture(wv)
            is WebViewAction.SetVideoSpeed -> ExtensionEngine.setVideoSpeed(wv, webViewAction.speed)
            is WebViewAction.ToggleDesktopMode -> {
                val settings = wv.settings
                if (webViewAction.isDesktop) {
                    settings.userAgentString = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                    settings.useWideViewPort = true
                    settings.loadWithOverviewMode = true
                } else {
                    settings.userAgentString = null
                }
                wv.reload()
            }
            is WebViewAction.ToggleExtension -> {
                if (webViewAction.extension.isEnabled) {
                    ExtensionEngine.injectSingleExtension(wv, webViewAction.extension)
                } else {
                    ExtensionEngine.removeExtensionEffects(wv, webViewAction.extension)
                }
            }
            is WebViewAction.FindInPage -> {
                if (webViewAction.forward) {
                    wv.findNext(true)
                } else {
                    wv.findNext(false)
                }
            }
            is WebViewAction.RunScript -> {
                wv.evaluateJavascript(webViewAction.script, null)
            }
            null -> {}
        }
        if (webViewAction != null) {
            onClearWebViewAction()
        }
    }

    // Handle Find in Page live query
    LaunchedEffect(findQuery) {
        webViewInstance?.findAllAsync(findQuery)
    }

    Box(modifier = modifier.fillMaxSize().background(Color(0xFF0F172A))) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Omnibar Capsule for Web View
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                OmnibarSearch(
                    currentTab = currentTab,
                    activeExtensionsCount = extensions.count { it.isEnabled && ExtensionEngine.shouldExecuteOnUrl(it, currentTab.url) },
                    onNavigate = onNavigate,
                    onReload = { webViewInstance?.reload() },
                    onOpenExtensionPopup = onOpenExtensionPopup,
                    isHomeHero = false
                )
            }

            // Find In Page Toolbar
            AnimatedVisibility(visible = showFindInPage) {
                FindInPageBar(
                    query = findQuery,
                    onQueryChange = onFindQueryChange,
                    onFindNext = { webViewInstance?.findNext(true) },
                    onFindPrevious = { webViewInstance?.findNext(false) },
                    onClose = {
                        webViewInstance?.clearMatches()
                        onCloseFindInPage()
                    }
                )
            }

            // Loading Progress Bar
            AnimatedVisibility(
                visible = currentTab.isLoading && currentTab.progress < 1f,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LinearProgressIndicator(
                    progress = { currentTab.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = Color(0xFF818CF8),
                    trackColor = Color.Transparent
                )
            }

            // WebView Engine
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )

                        setLayerType(View.LAYER_TYPE_HARDWARE, null)

                        val cookieMgr = CookieManager.getInstance()
                        cookieMgr.setAcceptCookie(true)
                        cookieMgr.setAcceptThirdPartyCookies(this, true)

                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            databaseEnabled = true
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            builtInZoomControls = true
                            displayZoomControls = false
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            cacheMode = WebSettings.LOAD_DEFAULT
                            mediaPlaybackRequiresUserGesture = false
                            allowFileAccess = false
                            allowContentAccess = true
                            setSupportMultipleWindows(false)
                            javaScriptCanOpenWindowsAutomatically = true

                            // Clean User-Agent: Removes standard WebView tokens (Version/x.x, ; wv) so Google and YouTube
                            // treat it as a full Chrome browser, enabling unrestricted video playback & optional Google login.
                            val defaultUa = userAgentString
                            userAgentString = defaultUa
                                .replace("; wv", "")
                                .replace(";wv", "")
                                .replace(Regex("Version/[0-9.]+\\s*"), "")
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                val progressFloat = (newProgress / 100f).coerceIn(0f, 1f)
                                onUpdateTab(
                                    null, null, null,
                                    newProgress < 100,
                                    progressFloat,
                                    view?.canGoBack(),
                                    view?.canGoForward(),
                                    null
                                )
                            }

                            override fun onReceivedTitle(view: WebView?, title: String?) {
                                if (!title.isNullOrBlank() && title != "about:blank") {
                                    val currentUrl = view?.url ?: ""
                                    onUpdateTab(currentUrl, title, null, null, null, view?.canGoBack(), view?.canGoForward(), null)
                                    onRecordHistory(currentUrl, title)
                                }
                            }
                        }

                        val spanishHeaders = mapOf(
                            "Accept-Language" to "es-ES,es;q=0.9,en-US;q=0.8,en;q=0.7"
                        )

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                url?.let {
                                    val isSec = it.startsWith("https://")
                                    onUpdateTab(it, null, null, true, 0.15f, view?.canGoBack(), view?.canGoForward(), isSec)
                                    // Inject Spanish language override into JavaScript navigator
                                    view?.evaluateJavascript(
                                        """
                                        (function() {
                                            try {
                                                Object.defineProperty(navigator, 'language', { get: function() { return 'es-ES'; }, configurable: true });
                                                Object.defineProperty(navigator, 'languages', { get: function() { return ['es-ES', 'es', 'en-US', 'en']; }, configurable: true });
                                            } catch(e) {}
                                        })();
                                        """.trimIndent(),
                                        null
                                    )
                                    // Inject DOCUMENT_START extensions (Adblock CSS/JS, Darkmode)
                                    view?.let { wv ->
                                        ExtensionEngine.injectExtensions(wv, extensions, it, isDocumentStart = true)
                                    }
                                }
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                url?.let {
                                    val isSec = it.startsWith("https://")
                                    val title = view?.title ?: it
                                    onUpdateTab(it, title, null, false, 1f, view?.canGoBack(), view?.canGoForward(), isSec)
                                    onRecordHistory(it, title)

                                    // Inject DOCUMENT_END extensions (Cookie killer, Reader, Custom scripts)
                                    view?.let { wv ->
                                        ExtensionEngine.injectExtensions(wv, extensions, it, isDocumentStart = false)
                                    }
                                }
                            }

                            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                val targetUrl = request?.url?.toString() ?: return false
                                if (targetUrl.startsWith("http://") || targetUrl.startsWith("https://")) {
                                    return false
                                }
                                return true
                            }

                            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                                onUpdateTab(null, null, null, null, null, null, null, false)
                                handler?.proceed()
                            }
                        }

                        webViewInstance = this
                        if (currentTab.url.isNotBlank() && !currentTab.isHomePage) {
                            loadUrl(currentTab.url, spanishHeaders)
                        }
                    }
                },
                update = { webView ->
                    val spanishHeaders = mapOf(
                        "Accept-Language" to "es-ES,es;q=0.9,en-US;q=0.8,en;q=0.7"
                    )
                    if (!currentTab.isHomePage && currentTab.url.isNotBlank()) {
                        val currentWvUrl = webView.url ?: ""
                        if (currentWvUrl.isEmpty() || (currentWvUrl != currentTab.url && !currentWvUrl.startsWith(currentTab.url.substringBefore("?")))) {
                            webView.loadUrl(currentTab.url, spanishHeaders)
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("browser_webview")
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            webViewInstance?.stopLoading()
        }
    }
}
