package com.example.extensions

import com.example.data.entity.ExtensionEntity

object BuiltInExtensions {
    val defaultList = listOf(
        ExtensionEntity(
            id = 1,
            identifier = "adblock_core",
            name = "Bloqueador de Anuncios y Rastreadores",
            description = "Elimina anuncios intrusivos, banners, popups y scripts de rastreo cosméticos.",
            version = "2.5.0",
            author = "Aura Shield Core",
            iconCategory = "shield",
            isBuiltIn = true,
            isEnabled = true,
            matchUrlPattern = "*",
            runAt = "DOCUMENT_START",
            scriptJs = """
                (function() {
                    const adSelectors = [
                        "ins.adsbygoogle", "div[id*='google_ads']", "div[id*='ad-banner']",
                        "div[class*='ad-box']", "div[class*='ad-container']", "div[class*='sponsored-post']",
                        ".adsbygoogle", ".ad-wrapper", ".ad-slot", ".advertisement", ".ad-unit",
                        "iframe[src*='doubleclick']", "iframe[src*='googlesyndication']", "iframe[src*='adnxs']",
                        "aside[aria-label*='advertisement' i]", "[data-ad-slot]", "[data-ad-unit]"
                    ];
                    
                    function purgeAds() {
                        const selector = adSelectors.join(',');
                        document.querySelectorAll(selector).forEach(el => {
                            el.style.setProperty('display', 'none', 'important');
                            el.style.setProperty('height', '0px', 'important');
                            el.style.setProperty('visibility', 'hidden', 'important');
                        });
                    }

                    if (document.readyState === 'loading') {
                        document.addEventListener('DOMContentLoaded', purgeAds);
                    } else {
                        purgeAds();
                    }

                    const observer = new MutationObserver(function() {
                        purgeAds();
                    });
                    
                    if (document.body) {
                        observer.observe(document.body, { childList: true, subtree: true });
                    } else {
                        document.addEventListener('DOMContentLoaded', () => {
                            if (document.body) observer.observe(document.body, { childList: true, subtree: true });
                        });
                    }
                })();
            """.trimIndent(),
            customCss = """
                ins.adsbygoogle, div[id*='google_ads'], div[class*='ad-banner'],
                .ad-wrapper, .ad-container, .sponsored-ad, .advertisement {
                    display: none !important;
                    height: 0 !important;
                    opacity: 0 !important;
                    pointer-events: none !important;
                }
            """.trimIndent()
        ),
        ExtensionEntity(
            id = 2,
            identifier = "darkmode_oled",
            name = "Modo Oscuro Universal",
            description = "Fuerza un elegante tema oscuro en cualquier sitio web manteniendo fotos y videos intactos.",
            version = "3.2.0",
            author = "Aura Night Engine",
            iconCategory = "moon",
            isBuiltIn = true,
            isEnabled = false,
            matchUrlPattern = "*",
            runAt = "DOCUMENT_START",
            scriptJs = """
                (function() {
                    const styleId = 'aura-dark-mode-style';
                    if (!document.getElementById(styleId)) {
                        const style = document.createElement('style');
                        style.id = styleId;
                        style.textContent = `
                            html {
                                filter: invert(90%) hue-rotate(180deg) !important;
                                background-color: #121212 !important;
                            }
                            img, video, canvas, picture, svg, iframe, [style*="background-image"] {
                                filter: invert(100%) hue-rotate(180deg) !important;
                            }
                            body {
                                background-color: #121212 !important;
                                color: #E0E0E0 !important;
                            }
                        `;
                        (document.head || document.documentElement).appendChild(style);
                    }
                })();
            """.trimIndent(),
            customCss = """
                html { filter: invert(90%) hue-rotate(180deg) !important; background-color: #121212 !important; }
                img, video, canvas, picture, svg, iframe { filter: invert(100%) hue-rotate(180deg) !important; }
            """.trimIndent()
        ),
        ExtensionEntity(
            id = 7,
            identifier = "youtube_age_bypass",
            name = "YouTube Pro & Bypass de Edad",
            description = "Reproduce videos sin restricciones de inicio de sesión ni anuncios intermedios y descarta avisos.",
            version = "2.3.0",
            author = "Aura Media Shield",
            iconCategory = "video",
            isBuiltIn = true,
            isEnabled = true,
            matchUrlPattern = "youtube.com",
            runAt = "DOCUMENT_END",
            scriptJs = """
                (function() {
                    let lastEmbedId = null;

                    function getVideoId() {
                        const href = location.href;
                        const watchMatch = href.match(/[?&]v=([^&#]+)/);
                        if (watchMatch && watchMatch[1]) return watchMatch[1];
                        const shortsMatch = href.match(/\/shorts\/([^/?&#]+)/);
                        if (shortsMatch && shortsMatch[1]) return shortsMatch[1];
                        const embedMatch = href.match(/\/embed\/([^/?&#]+)/);
                        if (embedMatch && embedMatch[1]) return embedMatch[1];
                        const liveMatch = href.match(/\/live\/([^/?&#]+)/);
                        if (liveMatch && liveMatch[1]) return liveMatch[1];
                        return null;
                    }

                    function forceEmbedPlayer(videoId) {
                        if (!videoId) return;
                        lastEmbedId = videoId;
                        
                        const targets = [
                            document.getElementById('player-container-id'),
                            document.getElementById('player'),
                            document.querySelector('ytd-player'),
                            document.querySelector('ytm-player'),
                            document.getElementById('movie_player'),
                            document.querySelector('.player-container'),
                            document.querySelector('.html5-video-player')
                        ].filter(Boolean);

                        const target = targets[0] || document.body;
                        
                        let existing = document.getElementById('aura-bypass-embed');
                        if (existing) {
                            if (existing.getAttribute('data-vid') === videoId) return;
                            existing.remove();
                        }

                        const wrapper = document.createElement('div');
                        wrapper.id = 'aura-bypass-embed';
                        wrapper.setAttribute('data-vid', videoId);
                        wrapper.style.cssText = 'position:relative; width:100%; height:100%; min-height:240px; background:#000; z-index:9999; border-radius:12px; overflow:hidden; box-shadow:0 8px 24px rgba(0,0,0,0.5);';
                        wrapper.innerHTML = `
                            <iframe 
                                src="https://www.youtube-nocookie.com/embed/${'$'}{videoId}?autoplay=1&playsinline=1&rel=0&modestbranding=1&controls=1" 
                                style="position:absolute; top:0; left:0; width:100%; height:100%; border:0;" 
                                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" 
                                allowfullscreen>
                            </iframe>
                        `;

                        if (targets[0]) {
                            targets[0].innerHTML = '';
                            targets[0].appendChild(wrapper);
                        } else {
                            target.insertBefore(wrapper, target.firstChild);
                        }

                        document.querySelectorAll('ytm-player-error-message-renderer, ytd-enforcement-message-view-model, .ytp-error').forEach(el => el.remove());
                    }

                    function unlockYouTube() {
                        const dismissButtons = document.querySelectorAll(
                            "button[aria-label*='Aceptar' i], button[aria-label*='Accept' i], " +
                            "button[aria-label*='Entendido' i], button[aria-label*='I understand' i], " +
                            ".yt-spec-button-shape-next--filled, ytm-consent-bump-v2-renderer button, " +
                            "tp-yt-paper-button[aria-label*='Aceptar' i], .eom-button-row button"
                        );
                        dismissButtons.forEach(btn => {
                            const text = (btn.innerText || btn.textContent || "").toLowerCase();
                            if (text.includes("aceptar") || text.includes("accept") || text.includes("entendido") || text.includes("continuar") || text.includes("agree") || text.includes("i understand")) {
                                btn.click();
                            }
                        });

                        const bodyText = (document.body ? document.body.innerText || "" : "").toLowerCase();
                        const hasAgeGate = 
                            bodyText.includes("inicia sesión para confirmar tu edad") || 
                            bodyText.includes("sign in to confirm your age") ||
                            bodyText.includes("restricción de edad") ||
                            bodyText.includes("age-restricted") ||
                            bodyText.includes("este video no está disponible") ||
                            bodyText.includes("confirm your age") ||
                            Boolean(document.querySelector('ytm-age-verification-dialog-renderer')) ||
                            Boolean(document.querySelector('ytd-enforcement-message-view-model')) ||
                            Boolean(document.querySelector('.ytp-error-content-wrap-reason'));

                        const videoId = getVideoId();
                        if (videoId && (hasAgeGate || (location.href.includes('/watch') && document.querySelector('.ytp-error')))) {
                            forceEmbedPlayer(videoId);
                        }

                        if (videoId && location.href.includes('/watch') && !document.getElementById('aura-bypass-btn')) {
                            const btn = document.createElement('div');
                            btn.id = 'aura-bypass-btn';
                            btn.style.cssText = 'position:fixed; bottom:70px; right:16px; background:linear-gradient(135deg, #6366F1, #4F46E5); color:#FFF; padding:8px 14px; border-radius:24px; font-size:12px; font-weight:bold; z-index:999999; cursor:pointer; box-shadow:0 4px 14px rgba(0,0,0,0.4); display:flex; align-items:center; gap:6px; font-family:sans-serif;';
                            btn.innerHTML = '<span>🔓 Reproducir sin restricción</span>';
                            btn.onclick = function() {
                                const currentVid = getVideoId();
                                forceEmbedPlayer(currentVid);
                            };
                            document.body.appendChild(btn);
                        }
                    }

                    unlockYouTube();
                    setInterval(unlockYouTube, 1500);

                    window.addEventListener('yt-navigate-finish', function() {
                        const vid = getVideoId();
                        if (vid && vid !== lastEmbedId) {
                            const btn = document.getElementById('aura-bypass-btn');
                            if (btn) btn.remove();
                            unlockYouTube();
                        }
                    });
                })();
            """.trimIndent()
        ),
        ExtensionEntity(
            id = 5,
            identifier = "zen_reader_mode",
            name = "Lector Zen / Modo Lectura",
            description = "Extrae el texto de artículos y noticias con tipografía limpia y elimina distracciones.",
            version = "1.6.0",
            author = "Aura Reading Lab",
            iconCategory = "book",
            isBuiltIn = true,
            isEnabled = false,
            matchUrlPattern = "*",
            runAt = "DOCUMENT_END",
            scriptJs = """
                (function() {
                    window.AuraReader = {
                        activate: function() {
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
                                    <button id="aura-close-reader" style="background:#2C2E33; color:#FFF; border:none; padding:8px 16px; border-radius:20px; font-size:14px; margin-bottom:24px; cursor:pointer;">← Cerrar Modo Lectura</button>
                                    <h1 style="font-size: 30px; line-height: 1.25; margin-bottom: 20px; font-weight: 700; color: #FFF;">${'$'}{title}</h1>
                                    <div class="aura-reader-content">${'$'}{contentHtml}</div>
                                </div>
                            `;
                            readerContainer.style.cssText = 'position:fixed; top:0; left:0; width:100%; height:100%; background:#181A1B; z-index:9999999; overflow-y:auto;';
                            document.body.appendChild(readerContainer);
                            
                            document.getElementById('aura-close-reader').onclick = function() {
                                readerContainer.remove();
                            };
                        }
                    };
                })();
            """.trimIndent()
        ),
        ExtensionEntity(
            id = 4,
            identifier = "video_speed_master",
            name = "Controlador de Video & PiP",
            description = "Controla la velocidad de videos (0.5x a 3.0x) y activa ventana flotante PiP.",
            version = "2.1.0",
            author = "Aura Media Tools",
            iconCategory = "video",
            isBuiltIn = true,
            isEnabled = true,
            matchUrlPattern = "*",
            runAt = "DOCUMENT_END",
            scriptJs = """
                (function() {
                    window.AuraVideoTool = {
                        setSpeed: function(speed) {
                            document.querySelectorAll('video').forEach(v => { v.playbackRate = speed; });
                            return 'Speed set to ' + speed;
                        },
                        togglePiP: function() {
                            const v = document.querySelector('video');
                            if (v && document.pictureInPictureEnabled) {
                                if (document.pictureInPictureElement) {
                                    document.exitPictureInPicture();
                                } else {
                                    v.requestPictureInPicture();
                                }
                            }
                        }
                    };
                })();
            """.trimIndent()
        ),
        ExtensionEntity(
            id = 3,
            identifier = "cookie_killer",
            name = "Auto-Ocultador de Cookies y GDPR",
            description = "Descarta automáticamente modales y banners molestos de cookies en cualquier web.",
            version = "1.9.0",
            author = "Aura Clean Web",
            iconCategory = "sparkle",
            isBuiltIn = true,
            isEnabled = true,
            matchUrlPattern = "*",
            runAt = "DOCUMENT_END",
            scriptJs = """
                (function() {
                    const cookieSelectors = [
                        "#onetrust-consent-sdk", "#onetrust-banner-sdk", "#cookie-law-info-bar",
                        ".cookie-banner", ".cookie-notice", ".cookie-bar", ".cc-window",
                        "#CybotCookiebotDialog", ".qc-cmp2-container", ".didomi-popup-container",
                        "[id*='cookie-consent']", "[class*='cookie-consent']", "[id*='gdpr-modal']",
                        "[aria-label*='cookie consent' i]", "[aria-label*='cookies' i]"
                    ];

                    function dismissCookieModals() {
                        const selector = cookieSelectors.join(',');
                        document.querySelectorAll(selector).forEach(el => {
                            el.style.setProperty('display', 'none', 'important');
                            el.style.setProperty('opacity', '0', 'important');
                        });
                        document.documentElement.style.setProperty('overflow', 'auto', 'important');
                        document.body.style.setProperty('overflow', 'auto', 'important');
                        document.body.style.setProperty('position', 'static', 'important');
                    }

                    dismissCookieModals();
                    setInterval(dismissCookieModals, 1200);
                })();
            """.trimIndent()
        ),
        ExtensionEntity(
            id = 8,
            identifier = "paywall_cleaner",
            name = "Desbloqueador de Lectura y Artículos",
            description = "Restaura el scroll y elimina muros de suscripción o bloqueos de contenido en periódicos.",
            version = "1.2.0",
            author = "Aura Reader Lab",
            iconCategory = "book",
            isBuiltIn = true,
            isEnabled = true,
            matchUrlPattern = "*",
            runAt = "DOCUMENT_END",
            scriptJs = """
                (function() {
                    function unlockArticles() {
                        document.querySelectorAll('.paywall, .modal-backdrop, .overlay-barrier, [class*="paywall"], [id*="paywall"], .tp-modal, .tp-backdrop').forEach(el => {
                            el.style.setProperty('display', 'none', 'important');
                        });
                        document.body.style.setProperty('overflow', 'auto', 'important');
                        document.documentElement.style.setProperty('overflow', 'auto', 'important');
                    }
                    unlockArticles();
                    setInterval(unlockArticles, 2000);
                })();
            """.trimIndent()
        ),
        ExtensionEntity(
            id = 9,
            identifier = "quick_translator",
            name = "Traductor Flotante de Páginas",
            description = "Traduce rápidamente páginas en otros idiomas al español con un botón flotante discreto.",
            version = "1.0.0",
            author = "Aura Language Hub",
            iconCategory = "translate",
            isBuiltIn = true,
            isEnabled = false,
            matchUrlPattern = "*",
            runAt = "DOCUMENT_END",
            scriptJs = """
                (function() {
                    if (document.getElementById('aura-quick-translate-btn')) return;
                    const btn = document.createElement('div');
                    btn.id = 'aura-quick-translate-btn';
                    btn.style.cssText = 'position:fixed; bottom:75px; left:16px; background:#4F46E5; color:#FFF; padding:6px 12px; border-radius:20px; font-size:12px; font-weight:600; z-index:999999; cursor:pointer; box-shadow:0 4px 12px rgba(0,0,0,0.3); font-family:sans-serif;';
                    btn.innerHTML = '🌐 Traducir a Español';
                    btn.onclick = function() {
                        location.href = 'https://translate.google.com/translate?sl=auto&tl=es&u=' + encodeURIComponent(location.href);
                    };
                    document.body.appendChild(btn);
                })();
            """.trimIndent()
        ),
        ExtensionEntity(
            id = 10,
            identifier = "wikipedia_zen",
            name = "Wikipedia Plus & Tabla de Contenidos",
            description = "Mejora el formato de lectura de Wikipedia y añade una navegación limpia entre secciones.",
            version = "1.1.0",
            author = "Aura Reading Lab",
            iconCategory = "book",
            isBuiltIn = true,
            isEnabled = true,
            matchUrlPattern = "wikipedia.org",
            runAt = "DOCUMENT_END",
            scriptJs = """
                (function() {
                    const style = document.createElement('style');
                    style.textContent = `
                        .content { max-width: 820px !important; margin: 0 auto !important; font-size: 17px !important; line-height: 1.65 !important; }
                        #mw-mf-page-center { max-width: 860px !important; margin: 0 auto !important; }
                    `;
                    document.head.appendChild(style);
                })();
            """.trimIndent()
        ),
        ExtensionEntity(
            id = 11,
            identifier = "shopping_price_helper",
            name = "Asistente de Precios y Ofertas (Amazon)",
            description = "Oculta anuncios patrocinados de compras y resalta los mejores precios reales.",
            version = "1.0.0",
            author = "Aura Shopping Tools",
            iconCategory = "sparkle",
            isBuiltIn = true,
            isEnabled = false,
            matchUrlPattern = "amazon.",
            runAt = "DOCUMENT_END",
            scriptJs = """
                (function() {
                    function cleanAmazon() {
                        document.querySelectorAll("[data-component-type='sp-sponsored-result'], .AdHolder, .s-sponsored-label-info-icon").forEach(el => {
                            const container = el.closest("[data-asin]") || el;
                            container.style.setProperty('display', 'none', 'important');
                        });
                    }
                    cleanAmazon();
                    setInterval(cleanAmazon, 1500);
                })();
            """.trimIndent()
        ),
        ExtensionEntity(
            id = 6,
            identifier = "font_enhancer",
            name = "Realce de Tipografía y Contraste",
            description = "Aumenta la nitidez del texto, suavizado de fuentes y legibilidad en pantallas AMOLED.",
            version = "1.1.0",
            author = "Aura Accessibility",
            iconCategory = "code",
            isBuiltIn = true,
            isEnabled = false,
            matchUrlPattern = "*",
            runAt = "DOCUMENT_END",
            scriptJs = """
                (function() {
                    const style = document.createElement('style');
                    style.id = 'aura-typography-style';
                    style.textContent = `
                        body, p, span, li, a {
                            -webkit-font-smoothing: antialiased !important;
                            text-rendering: optimizeLegibility !important;
                            letter-spacing: 0.015em !important;
                        }
                    `;
                    document.head.appendChild(style);
                })();
            """.trimIndent()
        )
    )

    fun getRecommendedExtensionForUrl(url: String, allExtensions: List<ExtensionEntity>): ExtensionEntity? {
        val lowerUrl = url.lowercase()
        return when {
            lowerUrl.contains("youtube.com") || lowerUrl.contains("youtu.be") -> {
                allExtensions.find { it.identifier == "youtube_age_bypass" && !it.isEnabled }
            }
            lowerUrl.contains("wikipedia.org") -> {
                allExtensions.find { it.identifier == "wikipedia_zen" && !it.isEnabled }
            }
            lowerUrl.contains("amazon.") -> {
                allExtensions.find { it.identifier == "shopping_price_helper" && !it.isEnabled }
            }
            lowerUrl.contains("medium.com") || lowerUrl.contains("nytimes.com") || lowerUrl.contains("elpais.com") || lowerUrl.contains("elmundo.es") || lowerUrl.contains("bbc.com") -> {
                allExtensions.find { (it.identifier == "paywall_cleaner" || it.identifier == "zen_reader_mode") && !it.isEnabled }
            }
            else -> null
        }
    }
}
