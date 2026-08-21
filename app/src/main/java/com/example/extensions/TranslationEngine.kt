package com.example.extensions

object TranslationEngine {

    const val DETECT_LANGUAGE_JS = """
        (function() {
            try {
                let lang = document.documentElement.lang || (document.body ? document.body.lang : '') || '';
                if (!lang) {
                    let meta = document.querySelector('meta[http-equiv="content-language" i]') || 
                               document.querySelector('meta[name="language" i]') ||
                               document.querySelector('meta[property="og:locale" i]');
                    if (meta) lang = meta.getAttribute('content') || '';
                }
                if (lang) {
                    lang = lang.trim().toLowerCase().split('-')[0].split('_')[0];
                    if (lang.length >= 2) return lang;
                }

                let sample = (document.body ? (document.body.innerText || '') : '').substring(0, 1500).toLowerCase();
                if (sample.length > 20) {
                    if (/[\u3040-\u30ff]/.test(sample)) return 'ja';
                    if (/[\u4e00-\u9fff]/.test(sample)) return 'zh-CN';
                    if (/[\uac00-\ud7af]/.test(sample)) return 'ko';
                    if (/[\u0400-\u04ff]/.test(sample)) return 'ru';
                    if (/[\u0600-\u06ff]/.test(sample)) return 'ar';
                    if (/[\u0900-\u097f]/.test(sample)) return 'hi';
                    if (/\b(the|is|are|and|with|for|you|that|this|from|have|not|with|news|share)\b/.test(sample)) return 'en';
                    if (/\b(le|la|les|des|un|une|est|dans|pour|avec|sont|sur|qui)\b/.test(sample)) return 'fr';
                    if (/\b(der|die|das|und|ist|nicht|für|mit|ein|eine|nach|über)\b/.test(sample)) return 'de';
                    if (/\b(il|la|di|che|per|sono|con|del|degli|una|nelle|della)\b/.test(sample)) return 'it';
                    if (/\b(de|que|em|um|uma|para|com|não|são|por|mais|notícias)\b/.test(sample)) return 'pt';
                    if (/\b(el|la|los|las|de|que|en|un|una|por|para|con|noticias|más|titulares)\b/.test(sample)) return 'es';
                }
                return 'es';
            } catch(e) {
                return 'es';
            }
        })();
    """

    fun getFastTranslateScript(sourceLang: String, targetLang: String): String {
        val src = if (sourceLang.isBlank() || sourceLang == "auto") "auto" else sourceLang
        val cleanTarget = if (targetLang == "zh" || targetLang == "zh-CN") "zh-CN" else targetLang

        return """
            (async function() {
                try {
                    const srcLang = '$src';
                    const targetLang = '$cleanTarget';

                    // 1. Collect candidate text nodes
                    const walker = document.createTreeWalker(
                        document.body || document.documentElement,
                        NodeFilter.SHOW_TEXT,
                        {
                            acceptNode: function(node) {
                                if (!node.nodeValue || !node.nodeValue.trim()) return NodeFilter.FILTER_REJECT;
                                const parent = node.parentElement;
                                if (!parent) return NodeFilter.FILTER_REJECT;
                                const tag = parent.tagName ? parent.tagName.toUpperCase() : '';
                                if (tag === 'SCRIPT' || tag === 'STYLE' || tag === 'NOSCRIPT' || tag === 'TEXTAREA' || tag === 'CODE' || tag === 'PRE') {
                                    return NodeFilter.FILTER_REJECT;
                                }
                                return NodeFilter.FILTER_ACCEPT;
                            }
                        }
                    );

                    window.__auraNodes = [];
                    let n;
                    while (n = walker.nextNode()) {
                        const val = n.nodeValue;
                        if (val && val.trim().length > 0) {
                            if (n.__auraOrig === undefined) {
                                n.__auraOrig = val;
                            }
                            window.__auraNodes.push(n);
                        }
                    }

                    if (window.__auraNodes.length === 0) {
                        return "no_text_nodes";
                    }

                    // 2. Prepare batches for translation
                    const BATCH_SIZE = 25;
                    const batches = [];
                    for (let i = 0; i < window.__auraNodes.length; i += BATCH_SIZE) {
                        batches.push(window.__auraNodes.slice(i, i + BATCH_SIZE));
                    }

                    // 3. Fast translation function
                    async function translateBatch(batch) {
                        const separator = ' ||| ';
                        const combinedText = batch.map(node => node.__auraOrig.trim()).join(separator);
                        if (!combinedText) return;

                        const url = 'https://translate.googleapis.com/translate_a/single?client=gtx&sl=' + encodeURIComponent(srcLang) + '&tl=' + encodeURIComponent(targetLang) + '&dt=t&q=' + encodeURIComponent(combinedText);
                        
                        try {
                            const resp = await fetch(url);
                            if (resp.ok) {
                                const data = await resp.json();
                                if (data && data[0]) {
                                    let translatedFull = '';
                                    for (let item of data[0]) {
                                        if (item && item[0]) translatedFull += item[0];
                                    }
                                    const parts = translatedFull.split('|||');
                                    for (let i = 0; i < batch.length; i++) {
                                        if (parts[i] !== undefined) {
                                            const orig = batch[i].__auraOrig;
                                            const lead = (orig.match(/^\s+/) || [''])[0];
                                            const trail = (orig.match(/\s+$/) || [''])[0];
                                            batch[i].nodeValue = lead + parts[i].trim() + trail;
                                        }
                                    }
                                    return;
                                }
                            }
                        } catch (e) {
                            // Fallback to bridge or individual request
                        }

                        // Fallback individual translation
                        for (let node of batch) {
                            const trimmed = node.__auraOrig.trim();
                            if (trimmed.length < 2) continue;
                            try {
                                const singleUrl = 'https://translate.googleapis.com/translate_a/single?client=gtx&sl=' + encodeURIComponent(srcLang) + '&tl=' + encodeURIComponent(targetLang) + '&dt=t&q=' + encodeURIComponent(trimmed);
                                const sResp = await fetch(singleUrl);
                                const sData = await sResp.json();
                                if (sData && sData[0]) {
                                    let res = '';
                                    for (let itm of sData[0]) {
                                        if (itm && itm[0]) res += itm[0];
                                    }
                                    const orig = node.__auraOrig;
                                    const lead = (orig.match(/^\s+/) || [''])[0];
                                    const trail = (orig.match(/\s+$/) || [''])[0];
                                    node.nodeValue = lead + res.trim() + trail;
                                }
                            } catch(err) {}
                        }
                    }

                    // Process up to 4 batches concurrently for ultra fast execution
                    for (let i = 0; i < batches.length; i += 4) {
                        const chunk = batches.slice(i, i + 4);
                        await Promise.all(chunk.map(b => translateBatch(b)));
                    }

                    return "translated_" + window.__auraNodes.length;
                } catch(e) {
                    return "error: " + e.message;
                }
            })();
        """.trimIndent()
    }

    fun getRevertScript(): String {
        return """
            (function() {
                try {
                    let count = 0;
                    if (window.__auraNodes && window.__auraNodes.length > 0) {
                        for (let n of window.__auraNodes) {
                            if (n && n.__auraOrig !== undefined) {
                                n.nodeValue = n.__auraOrig;
                                count++;
                            }
                        }
                    } else {
                        const walker = document.createTreeWalker(
                            document.body || document.documentElement,
                            NodeFilter.SHOW_TEXT,
                            null
                        );
                        let n;
                        while (n = walker.nextNode()) {
                            if (n.__auraOrig !== undefined) {
                                n.nodeValue = n.__auraOrig;
                                count++;
                            }
                        }
                    }
                    return "restored_" + count;
                } catch(e) {
                    return "error";
                }
            })();
        """.trimIndent()
    }

    fun getTranslateProxyUrl(url: String, sourceLang: String, targetLang: String): String {
        val src = if (sourceLang == "auto" || sourceLang.isBlank()) "auto" else sourceLang
        return "https://translate.google.com/translate?sl=$src&tl=$targetLang&u=${android.net.Uri.encode(url)}"
    }
}
