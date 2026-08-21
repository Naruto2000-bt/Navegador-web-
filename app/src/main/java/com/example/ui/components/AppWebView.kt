package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.entity.ExtensionEntity
import com.example.extensions.ExtensionEngine
import com.example.model.BrowserTab
import java.io.ByteArrayInputStream
import java.net.URLEncoder

/**
 * JavaScript bridge to capture login credential submissions safely from web forms.
 */
class AuraAuthBridge(
    private val onCredentialsDetected: (domain: String, url: String, user: String, pass: String) -> Unit
) {
    private val mainHandler = Handler(Looper.getMainLooper())

    @JavascriptInterface
    fun onCredentialsDetected(originUrl: String, username: String, password: String) {
        if (username.isBlank() || password.isBlank()) return
        val cleanUrl = originUrl.trim()
        val domain = try {
            val uri = Uri.parse(cleanUrl)
            uri.host ?: cleanUrl
        } catch (e: Exception) {
            cleanUrl
        }

        mainHandler.post {
            onCredentialsDetected(domain, cleanUrl, username.trim(), password)
        }
    }
}

/**
 * Production-grade Jetpack Compose WebView Component.
 * Supports:
 * - Smart Credential Detection (Form submit interception & Auto-save prompts)
 * - Autofill injection
 * - Content ad/tracker blocking
 * - Extension user-scripts injection
 * - Incognito session isolation & anti-leak rules
 * - Desktop mode user-agent toggling
 * - Dark mode / force dark web content support
 * - SSL security handling & progress reporting
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AppWebView(
    tab: BrowserTab,
    modifier: Modifier = Modifier,
    isAdBlockEnabled: Boolean = true,
    isDesktopMode: Boolean = false,
    fontSizePercent: Int = 100,
    extensions: List<ExtensionEntity> = emptyList(),
    onProgressChanged: (Int) -> Unit = {},
    onPageStarted: (String, Bitmap?) -> Unit = { _, _ -> },
    onPageFinished: (String) -> Unit = {},
    onTitleReceived: (String) -> Unit = {},
    onSecurityStatusChanged: (isSecure: Boolean, isSslError: Boolean) -> Unit = { _, _ -> },
    onTrackerBlocked: () -> Unit = {},
    onCredentialsDetected: (domain: String, url: String, user: String, pass: String) -> Unit = { _, _, _, _ -> },
    onUrlOverride: (String) -> Boolean = { false },
    onWebViewCreated: (WebView) -> Unit = {}
) {
    val context = LocalContext.current

    // Remember WebView instance per tab ID to avoid recreating unnecessarily
    val webView = remember(tab.id) {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = !tab.isIncognito
                databaseEnabled = !tab.isIncognito
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = true
                displayZoomControls = false
                setSupportZoom(true)
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                cacheMode = if (tab.isIncognito) WebSettings.LOAD_NO_CACHE else WebSettings.LOAD_DEFAULT
                mediaPlaybackRequiresUserGesture = false
                allowFileAccess = false
                allowContentAccess = true
                textZoom = fontSizePercent

                // User-Agent toggle for Desktop Mode
                if (isDesktopMode) {
                    userAgentString = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36"
                } else {
                    userAgentString = null // Default mobile user agent
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    forceDark = WebSettings.FORCE_DARK_AUTO
                }
            }

            if (tab.isIncognito) {
                CookieManager.getInstance().setAcceptCookie(false)
            } else {
                CookieManager.getInstance().setAcceptCookie(true)
            }

            // Register JS Bridge for Login Credentials if not in incognito
            if (!tab.isIncognito) {
                addJavascriptInterface(
                    AuraAuthBridge(onCredentialsDetected),
                    "AuraAuthBridge"
                )
            }

            onWebViewCreated(this)
        }
    }

    // Dynamic settings update on recomposition
    webView.settings.textZoom = fontSizePercent
    if (isDesktopMode) {
        webView.settings.userAgentString = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36"
    } else {
        webView.settings.userAgentString = null
    }

    DisposableEffect(tab.id) {
        onDispose {
            try {
                if (tab.isIncognito) {
                    webView.clearCache(true)
                    webView.clearFormData()
                    webView.clearHistory()
                }
                webView.stopLoading()
                webView.destroy()
            } catch (ignored: Exception) {}
        }
    }

    AndroidView(
        factory = {
            webView.apply {
                webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        super.onProgressChanged(view, newProgress)
                        onProgressChanged(newProgress)
                    }

                    override fun onReceivedTitle(view: WebView?, title: String?) {
                        super.onReceivedTitle(view, title)
                        if (!title.isNullOrBlank()) {
                            onTitleReceived(title)
                        }
                    }

                    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                        return super.onConsoleMessage(consoleMessage)
                    }

                    override fun onCreateWindow(
                        view: WebView?,
                        isDialog: Boolean,
                        isUserGesture: Boolean,
                        resultMsg: Message?
                    ): Boolean {
                        return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
                    }
                }

                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                        val requestUrl = request?.url?.toString() ?: return false
                        if (onUrlOverride(requestUrl)) {
                            return true
                        }
                        return super.shouldOverrideUrlLoading(view, request)
                    }

                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        val currentUrl = url ?: ""
                        val isHttps = currentUrl.startsWith("https://", ignoreCase = true)
                        onSecurityStatusChanged(isHttps, false)
                        onPageStarted(currentUrl, favicon)
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        val currentUrl = url ?: ""
                        val isHttps = currentUrl.startsWith("https://", ignoreCase = true)
                        onSecurityStatusChanged(isHttps, false)
                        onPageFinished(currentUrl)

                        // Inject Form Submit & Credential Detection Script if not incognito
                        if (!tab.isIncognito && currentUrl.startsWith("http", ignoreCase = true)) {
                            val credentialDetectorScript = """
                                (function() {
                                    if (window.__auraAuthAttached) return;
                                    window.__auraAuthAttached = true;
                                    
                                    function checkAndEmitCredentials(scope) {
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
                                        checkAndEmitCredentials(e.target);
                                    }, true);
                                    
                                    document.addEventListener('click', function(e) {
                                        var target = e.target.closest('button, input[type="submit"], [role="button"]');
                                        if (target) {
                                            var form = target.closest('form');
                                            setTimeout(function() {
                                                checkAndEmitCredentials(form);
                                            }, 80);
                                        }
                                    }, true);
                                })();
                            """.trimIndent()
                            view?.evaluateJavascript(credentialDetectorScript, null)
                        }

                        // Inject active browser extensions & custom user scripts
                        view?.let { wv ->
                            ExtensionEngine.injectExtensions(wv, extensions, currentUrl, isDocumentStart = false)
                        }
                    }

                    override fun shouldInterceptRequest(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): WebResourceResponse? {
                        if (isAdBlockEnabled && request != null) {
                            val reqUrl = request.url.toString()
                            if (isTrackerOrAd(reqUrl)) {
                                onTrackerBlocked()
                                return WebResourceResponse("text/plain", "UTF-8", ByteArrayInputStream("".toByteArray()))
                            }
                        }
                        return super.shouldInterceptRequest(view, request)
                    }

                    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                        onSecurityStatusChanged(false, true)
                        super.onReceivedSslError(view, handler, error)
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        super.onReceivedError(view, request, error)
                    }
                }
            }
        },
        update = { wv ->
            // Load new URL if the tab URL has changed and doesn't match current webview URL
            if (!tab.isHomePage && tab.url.isNotBlank()) {
                val currentWvUrl = wv.url ?: ""
                if (currentWvUrl.isBlank() || currentWvUrl != tab.url) {
                    wv.loadUrl(tab.url)
                }
            }
        },
        modifier = modifier
            .fillMaxSize()
            .testTag("app_webview_${tab.id}")
    )
}

/**
 * Utility function to autofill credentials directly into the active WebView form fields.
 */
fun WebView.autofillLoginFields(username: String, password: String) {
    val escapedUser = URLEncoder.encode(username, "UTF-8")
    val escapedPass = URLEncoder.encode(password, "UTF-8")
    val autofillJs = """
        (function() {
            var decodedUser = decodeURIComponent('$escapedUser');
            var decodedPass = decodeURIComponent('$escapedPass');
            
            var uField = document.querySelector(
                'input[type="email"], input[name*="user"], input[name*="login"], input[name*="email"], input[id*="user"], input[id*="login"], input[id*="email"], input[autocomplete="username"], input[autocomplete="email"], input[type="text"]'
            );
            var pField = document.querySelector('input[type="password"]');
            
            if (uField) {
                uField.focus();
                uField.value = decodedUser;
                uField.dispatchEvent(new Event('input', { bubbles: true }));
                uField.dispatchEvent(new Event('change', { bubbles: true }));
            }
            if (pField) {
                pField.focus();
                pField.value = decodedPass;
                pField.dispatchEvent(new Event('input', { bubbles: true }));
                pField.dispatchEvent(new Event('change', { bubbles: true }));
            }
        })();
    """.trimIndent()
    evaluateJavascript(autofillJs, null)
}

/**
 * Checks if a requested resource URL matches known ad or tracker patterns.
 */
private fun isTrackerOrAd(url: String): Boolean {
    val lower = url.lowercase()
    return lower.contains("doubleclick.net") ||
            lower.contains("google-analytics.com") ||
            lower.contains("googlesyndication.com") ||
            lower.contains("adservice.google") ||
            lower.contains("facebook.com/tr") ||
            lower.contains("adnxs.com") ||
            lower.contains("criteo.com") ||
            lower.contains("outbrain.com") ||
            lower.contains("taboola.com") ||
            lower.contains("scorecardresearch.com") ||
            lower.contains("/ads/") ||
            lower.contains("/advertisement/") ||
            lower.contains("pagead2")
}
