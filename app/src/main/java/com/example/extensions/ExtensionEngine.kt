package com.example.extensions

import android.net.Uri
import android.util.Base64
import android.webkit.WebView
import com.example.data.entity.ExtensionEntity

object ExtensionEngine {

    fun shouldExecuteOnUrl(extension: ExtensionEntity, currentUrl: String): Boolean {
        if (!extension.isEnabled) return false
        val pattern = extension.matchUrlPattern.trim()
        if (pattern == "*" || pattern == "*://*/*" || pattern.isEmpty()) return true

        return try {
            val uri = Uri.parse(currentUrl)
            val host = uri.host ?: ""
            if (pattern.startsWith("*.")) {
                val cleanDomain = pattern.removePrefix("*.")
                host.endsWith(cleanDomain) || host == cleanDomain
            } else {
                currentUrl.contains(pattern, ignoreCase = true) || host.contains(pattern, ignoreCase = true)
            }
        } catch (e: Exception) {
            true
        }
    }

    fun injectExtensions(
        webView: WebView,
        extensions: List<ExtensionEntity>,
        currentUrl: String,
        isDocumentStart: Boolean
    ) {
        val targetTiming = if (isDocumentStart) "DOCUMENT_START" else "DOCUMENT_END"

        val relevantExtensions = extensions.filter { ext ->
            ext.isEnabled && ext.runAt.equals(targetTiming, ignoreCase = true) && shouldExecuteOnUrl(ext, currentUrl)
        }

        for (ext in relevantExtensions) {
            injectSingleExtension(webView, ext)
        }
    }

    fun injectSingleExtension(webView: WebView, extension: ExtensionEntity) {
        // Inject CSS if present
        if (extension.customCss.isNotBlank()) {
            val cssEncoded = Base64.encodeToString(extension.customCss.toByteArray(), Base64.NO_WRAP)
            val cssInjectionCode = """
                (function() {
                    const styleId = 'aura-ext-css-${extension.identifier}';
                    let styleEl = document.getElementById(styleId);
                    if (!styleEl) {
                        styleEl = document.createElement('style');
                        styleEl.id = styleId;
                        styleEl.type = 'text/css';
                        styleEl.textContent = atob('$cssEncoded');
                        (document.head || document.documentElement).appendChild(styleEl);
                    }
                })();
            """.trimIndent()
            webView.evaluateJavascript(cssInjectionCode, null)
        }

        // Inject JS script
        if (extension.scriptJs.isNotBlank()) {
            val jsCode = """
                try {
                    ${extension.scriptJs}
                } catch(e) {
                    console.error('Aura Extension error (${extension.name}):', e);
                }
            """.trimIndent()
            webView.evaluateJavascript(jsCode, null)
        }
    }

    fun removeExtensionEffects(webView: WebView, extension: ExtensionEntity) {
        val cleanupJs = """
            (function() {
                const styleEl = document.getElementById('aura-ext-css-${extension.identifier}');
                if (styleEl) styleEl.remove();
                
                if ('${extension.identifier}' === 'darkmode_oled') {
                    const darkStyle = document.getElementById('aura-dark-mode-style');
                    if (darkStyle) darkStyle.remove();
                }
                if ('${extension.identifier}' === 'zen_reader_mode') {
                    const readerView = document.getElementById('aura-zen-reader-view');
                    if (readerView) readerView.remove();
                }
            })();
        """.trimIndent()
        webView.evaluateJavascript(cleanupJs, null)
    }

    fun triggerReaderMode(webView: WebView) {
        val js = """
            if (window.AuraReader && typeof window.AuraReader.activate === 'function') {
                window.AuraReader.activate();
            } else {
                (function() {
                    let title = document.title || 'Artículo';
                    let h1 = document.querySelector('h1');
                    if (h1) title = h1.innerText;
                    
                    let article = document.querySelector('article') || 
                                  document.querySelector('.article-body') || 
                                  document.querySelector('.post-content') ||
                                  document.querySelector('#content') ||
                                  document.querySelector('main');
                    
                    let contentHtml = article ? article.innerHTML : '';
                    if (!contentHtml) {
                        const ps = Array.from(document.querySelectorAll('p')).map(p => p.outerHTML).join('');
                        contentHtml = ps;
                    }

                    const readerContainer = document.createElement('div');
                    readerContainer.id = 'aura-zen-reader-view';
                    readerContainer.innerHTML = `
                        <div style="max-width: 680px; margin: 40px auto; padding: 24px; font-family: Georgia, serif; font-size: 19px; line-height: 1.7; color: #E8E6E3; background-color: #181A1B;">
                            <button id="aura-close-reader" style="background:#2C2E33; color:#FFF; border:none; padding:10px 18px; border-radius:20px; font-size:14px; margin-bottom:24px; cursor:pointer; font-weight:600;">← Cerrar Modo Lectura</button>
                            <h1 style="font-size: 32px; line-height: 1.25; margin-bottom: 20px; font-weight: 700; color: #FFF;">${'$'}{title}</h1>
                            <div class="aura-reader-content">${'$'}{contentHtml}</div>
                        </div>
                    `;
                    readerContainer.style.cssText = 'position:fixed; top:0; left:0; width:100%; height:100%; background:#181A1B; z-index:9999999; overflow-y:auto;';
                    document.body.appendChild(readerContainer);
                    
                    document.getElementById('aura-close-reader').onclick = function() {
                        readerContainer.remove();
                    };
                })();
            }
        """.trimIndent()
        webView.evaluateJavascript(js, null)
    }

    fun setVideoSpeed(webView: WebView, speed: Float) {
        val js = """
            (function() {
                document.querySelectorAll('video').forEach(v => {
                    v.playbackRate = $speed;
                });
            })();
        """.trimIndent()
        webView.evaluateJavascript(js, null)
    }

    fun togglePictureInPicture(webView: WebView) {
        val js = """
            (function() {
                const video = document.querySelector('video');
                if (video) {
                    if (document.pictureInPictureElement) {
                        document.exitPictureInPicture();
                    } else if (video.requestPictureInPicture) {
                        video.requestPictureInPicture();
                    }
                }
            })();
        """.trimIndent()
        webView.evaluateJavascript(js, null)
    }
}
