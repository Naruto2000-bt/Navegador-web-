package com.example.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
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
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.entity.ExtensionEntity
import com.example.data.entity.SavedCredentialEntity
import com.example.extensions.AuraTranslateBridge
import com.example.extensions.BuiltInExtensions
import com.example.extensions.ExtensionEngine
import com.example.extensions.TranslationEngine
import com.example.model.BrowserTab
import com.example.model.PageTranslationState
import com.example.model.PendingCredentialSave
import com.example.ui.components.AuraAuthBridge
import com.example.ui.components.AutofillSuggestionChip
import com.example.ui.components.FindInPageBar
import com.example.ui.components.OmnibarSearch
import com.example.ui.components.PageTranslationBar
import com.example.ui.components.SavePasswordPromptBanner
import com.example.ui.components.autofillLoginFields
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
    translationState: PageTranslationState = PageTranslationState(),
    pendingCredentialSave: PendingCredentialSave? = null,
    activeAutofillSuggestion: SavedCredentialEntity? = null,
    onSavePendingCredential: (PendingCredentialSave) -> Unit = {},
    onDismissPendingCredential: () -> Unit = {},
    onAutofillCredential: (SavedCredentialEntity) -> Unit = {},
    onDismissAutofillSuggestion: () -> Unit = {},
    onCredentialsDetected: (domain: String, url: String, user: String, pass: String) -> Unit = { _, _, _, _ -> },
    onCheckForAutofill: (url: String) -> Unit = {},
    onFindQueryChange: (String) -> Unit,
    onCloseFindInPage: () -> Unit,
    onTranslate: (targetLang: String) -> Unit = {},
    onRevertTranslation: () -> Unit = {},
    onSelectSourceLang: (String) -> Unit = {},
    onSelectTargetLang: (String) -> Unit = {},
    onDismissTranslation: () -> Unit = {},
    onLanguageDetected: (String, String) -> Unit = { _, _ -> },
    onWebPageStarted: (String) -> Unit = {},
    onTranslationFinished: () -> Unit = {},
    onUpdateTab: (url: String?, title: String?, faviconUrl: String?, isLoading: Boolean?, progress: Float?, canGoBack: Boolean?, canGoForward: Boolean?, isSecure: Boolean?) -> Unit,
    onNavigate: (String) -> Unit,
    onRecordHistory: (String, String) -> Unit,
    onClearWebViewAction: () -> Unit,
    onOpenExtensionPopup: () -> Unit,
    onOpenPrivacyInfo: () -> Unit = {},
    onToggleExtension: (ExtensionEntity) -> Unit = {},
    onAdBlocked: () -> Unit
) {
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var dismissedRecommendationForUrl by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    val recommendedExtension = remember(currentTab.url, extensions, dismissedRecommendationForUrl) {
        if (currentTab.url == dismissedRecommendationForUrl) null
        else BuiltInExtensions.getRecommendedExtensionForUrl(currentTab.url, extensions)
    }

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
            is WebViewAction.TranslatePage -> {
                val script = TranslationEngine.getFastTranslateScript(webViewAction.sourceLang, webViewAction.targetLang)
                wv.evaluateJavascript(script) {
                    onTranslationFinished()
                }
            }
            is WebViewAction.RevertTranslation -> {
                val script = TranslationEngine.getRevertScript()
                wv.evaluateJavascript(script, null)
            }
            is WebViewAction.AutofillLogin -> {
                wv.autofillLoginFields(webViewAction.user, webViewAction.pass)
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
            // Top Omnibar Capsule for Web View (42dp compact height)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                OmnibarSearch(
                    currentTab = currentTab,
                    activeExtensionsCount = extensions.count { it.isEnabled && ExtensionEngine.shouldExecuteOnUrl(it, currentTab.url) },
                    onNavigate = onNavigate,
                    onReload = { webViewInstance?.reload() },
                    onOpenExtensionPopup = onOpenExtensionPopup,
                    onOpenPrivacyInfo = onOpenPrivacyInfo,
                    isHomeHero = false
                )
            }

            // In-Page Automatic & Interactive Translation Bar
            PageTranslationBar(
                translationState = translationState,
                onTranslate = onTranslate,
                onRevertOriginal = onRevertTranslation,
                onSelectSourceLang = onSelectSourceLang,
                onSelectTargetLang = onSelectTargetLang,
                onDismiss = onDismissTranslation
            )

            // Save Password Notification Prompt Banner
            SavePasswordPromptBanner(
                pendingCredential = pendingCredentialSave,
                onSave = onSavePendingCredential,
                onDismiss = onDismissPendingCredential
            )

            // Automatic Extension Detection Banner
            AnimatedVisibility(
                visible = recommendedExtension != null,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                recommendedExtension?.let { ext ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E1B4B))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFFA78BFA),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sugerida: ${ext.name}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Button(
                                onClick = {
                                    onToggleExtension(ext)
                                    dismissedRecommendationForUrl = currentTab.url
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Activar en 1 clic", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            IconButton(
                                onClick = { dismissedRecommendationForUrl = currentTab.url },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Descartar",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
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
                        .height(2.5.dp),
                    color = if (currentTab.isIncognito) Color(0xFFA78BFA) else Color(0xFF818CF8),
                    trackColor = Color.Transparent
                )
            }

            // WebView Engine Container
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )

                            setLayerType(View.LAYER_TYPE_HARDWARE, null)

                            // Translate Bridge
                            addJavascriptInterface(
                                AuraTranslateBridge(this, coroutineScope) { success ->
                                    onTranslationFinished()
                                },
                                "auraBridge"
                            )

                            // Login / Credential Detection Bridge (Disabled in Incognito)
                            if (!currentTab.isIncognito) {
                                addJavascriptInterface(
                                    AuraAuthBridge(onCredentialsDetected),
                                    "AuraAuthBridge"
                                )
                            }

                            val cookieMgr = CookieManager.getInstance()
                            if (currentTab.isIncognito) {
                                cookieMgr.setAcceptCookie(false)
                            } else {
                                cookieMgr.setAcceptCookie(true)
                                cookieMgr.setAcceptThirdPartyCookies(this, true)
                            }

                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = !currentTab.isIncognito
                                databaseEnabled = !currentTab.isIncognito
                                loadWithOverviewMode = true
                                useWideViewPort = true
                                builtInZoomControls = true
                                displayZoomControls = false
                                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                cacheMode = if (currentTab.isIncognito) WebSettings.LOAD_NO_CACHE else WebSettings.LOAD_DEFAULT
                                mediaPlaybackRequiresUserGesture = false
                                allowFileAccess = false
                                allowContentAccess = true
                                setSupportMultipleWindows(false)
                                javaScriptCanOpenWindowsAutomatically = true

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

                                    if (newProgress in 35..60) {
                                        val curUrl = view?.url ?: ""
                                        if (curUrl.startsWith("http://") || curUrl.startsWith("https://")) {
                                            view?.evaluateJavascript(TranslationEngine.DETECT_LANGUAGE_JS) { result ->
                                                val cleanLang = result?.replace("\"", "")?.trim() ?: ""
                                                if (cleanLang.isNotBlank() && cleanLang != "null" && cleanLang != "undefined") {
                                                    onLanguageDetected(cleanLang, curUrl)
                                                }
                                            }
                                        }
                                    }
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
                                        onWebPageStarted(it)
                                        onUpdateTab(it, null, null, true, 0.15f, view?.canGoBack(), view?.canGoForward(), isSec)
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

                                        // Check for saved credentials for this site to suggest autofill
                                        onCheckForAutofill(it)

                                        view?.let { wv ->
                                            ExtensionEngine.injectExtensions(wv, extensions, it, isDocumentStart = false)

                                            // Inject Credential & Form Interception Script
                                            if (!currentTab.isIncognito && it.startsWith("http", ignoreCase = true)) {
                                                val authScript = """
                                                    (function() {
                                                        if (window.__auraAuthAttached) return;
                                                        window.__auraAuthAttached = true;
                                                        
                                                        function checkAndReport(scope) {
                                                            try {
                                                                var container = scope || document;
                                                                var pFields = container.querySelectorAll('input[type="password"]');
                                                                if (!pFields || pFields.length === 0) return;
                                                                
                                                                var pass = pFields[0].value;
                                                                if (!pass || pass.length === 0) return;
                                                                
                                                                var uField = container.querySelector(
                                                                    'input[type="email"], input[name*="user"], input[name*="login"], input[name*="email"], input[id*="user"], input[id*="login"], input[id*="email"], input[autocomplete="username"], input[autocomplete="email"], input[type="text"]'
                                                                );
                                                                
                                                                var user = uField ? uField.value : "";
                                                                if (user && pass && window.AuraAuthBridge) {
                                                                    window.AuraAuthBridge.onCredentialsDetected(window.location.href, user, pass);
                                                                }
                                                            } catch(e) {}
                                                        }
                                                        
                                                        document.addEventListener('submit', function(e) {
                                                            checkAndReport(e.target);
                                                        }, true);
                                                        
                                                        document.addEventListener('click', function(e) {
                                                            var target = e.target.closest('button, input[type="submit"], [role="button"]');
                                                            if (target) {
                                                                var form = target.closest('form');
                                                                setTimeout(function() {
                                                                    checkAndReport(form);
                                                                }, 80);
                                                            }
                                                        }, true);
                                                    })();
                                                """.trimIndent()
                                                wv.evaluateJavascript(authScript, null)
                                            }

                                            // Automatic Language Detection for Web Translation
                                            wv.evaluateJavascript(TranslationEngine.DETECT_LANGUAGE_JS) { result ->
                                                val cleanLang = result?.replace("\"", "")?.trim() ?: ""
                                                if (cleanLang.isNotBlank() && cleanLang != "null" && cleanLang != "undefined") {
                                                    onLanguageDetected(cleanLang, it)
                                                }
                                            }
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
                        .fillMaxSize()
                        .testTag("browser_webview")
                )

                // Autofill Suggestion Floating Bar at Bottom of Web Page
                AutofillSuggestionChip(
                    credential = activeAutofillSuggestion,
                    onAutofill = onAutofillCredential,
                    onDismiss = onDismissAutofillSuggestion,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 64.dp)
                )
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            webViewInstance?.stopLoading()
        }
    }
}
