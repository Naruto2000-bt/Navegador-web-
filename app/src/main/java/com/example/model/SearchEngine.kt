package com.example.model

enum class SearchEngine(
    val displayName: String,
    val searchUrlTemplate: String,
    val iconCategory: String,
    val homeUrl: String
) {
    GOOGLE("Google", "https://www.google.com/search?q=%s&hl=es", "google", "https://www.google.com/?hl=es"),
    DUCKDUCKGO("DuckDuckGo", "https://duckduckgo.com/?q=%s&kl=es-es&kad=es_ES", "privacy", "https://duckduckgo.com/?kl=es-es"),
    BING("Bing", "https://www.bing.com/search?q=%s&setlang=es", "bing", "https://www.bing.com/?setlang=es"),
    ECOSIA("Ecosia", "https://www.ecosia.org/search?q=%s&_sp=es", "tree", "https://www.ecosia.org"),
    WIKIPEDIA("Wikipedia", "https://es.wikipedia.org/wiki/Special:Search?search=%s", "wiki", "https://es.wikipedia.org"),
    GITHUB("GitHub", "https://github.com/search?q=%s", "github", "https://github.com");

    fun buildUrl(query: String): String {
        val trimmed = query.trim()
        if (isDirectUrl(trimmed)) {
            return if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
                trimmed
            } else {
                "https://$trimmed"
            }
        }
        val encoded = java.net.URLEncoder.encode(trimmed, "UTF-8")
        return String.format(searchUrlTemplate, encoded)
    }

    companion object {
        fun isDirectUrl(query: String): Boolean {
            val q = query.trim().lowercase()
            if (q.startsWith("http://") || q.startsWith("https://") || q.startsWith("ftp://")) return true
            if (q.contains(" ") || !q.contains(".")) return false
            val domainRegex = Regex("^[a-zA-Z0-9-]+(\\.[a-zA-Z]{2,})+(/.*)?$")
            return domainRegex.matches(q) || q.startsWith("localhost:") || q.startsWith("127.0.0.1")
        }
    }
}
