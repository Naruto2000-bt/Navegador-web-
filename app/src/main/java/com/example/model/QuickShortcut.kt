package com.example.model

data class QuickShortcut(
    val title: String,
    val url: String,
    val initial: String,
    val iconColor: Long = 0xFF6366F1
) {
    companion object {
        val defaultShortcuts = listOf(
            QuickShortcut("Google", "https://www.google.com/?hl=es", "G", 0xFF4285F4),
            QuickShortcut("Wikipedia", "https://es.wikipedia.org", "W", 0xFF000000),
            QuickShortcut("GitHub", "https://github.com", "GH", 0xFF24292E),
            QuickShortcut("Reddit", "https://www.reddit.com", "R", 0xFFFF4500),
            QuickShortcut("YouTube", "https://www.youtube.com/?hl=es&gl=ES", "YT", 0xFFFF0000),
            QuickShortcut("Noticias", "https://news.google.com/?hl=es&gl=ES&ceid=ES:es", "N", 0xFF34A853),
            QuickShortcut("MDN Web", "https://developer.mozilla.org/es/", "M", 0xFF8338EC),
            QuickShortcut("DuckDuckGo", "https://duckduckgo.com/?kl=es-es", "DDG", 0xFFDE5833)
        )
    }
}
