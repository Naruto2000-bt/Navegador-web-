package com.example.model

import java.util.UUID

data class BrowserTab(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Nueva pestaña",
    val url: String = "aura://home",
    val faviconUrl: String? = null,
    val isIncognito: Boolean = false,
    val isLoading: Boolean = false,
    val progress: Float = 0f,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isSecure: Boolean = false,
    val isDesktopMode: Boolean = false,
    val activeExtensionCount: Int = 0
) {
    val isHomePage: Boolean
        get() = url == "aura://home" || url == "about:blank" || url.isEmpty()

    val displayHost: String
        get() {
            if (isHomePage) return "Inicio"
            return try {
                val uri = android.net.Uri.parse(url)
                uri.host ?: url
            } catch (e: Exception) {
                url
            }
        }
}
