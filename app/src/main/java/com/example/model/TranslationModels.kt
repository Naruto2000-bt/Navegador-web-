package com.example.model

data class LanguageItem(
    val code: String,
    val name: String,
    val nativeName: String,
    val flagEmoji: String
)

object TranslationLanguages {
    val supported = listOf(
        LanguageItem("es", "Español", "Español", "🇪🇸"),
        LanguageItem("en", "Inglés", "English", "🇺🇸"),
        LanguageItem("fr", "Francés", "Français", "🇫🇷"),
        LanguageItem("de", "Alemán", "Deutsch", "🇩🇪"),
        LanguageItem("it", "Italiano", "Italiano", "🇮🇹"),
        LanguageItem("pt", "Portugués", "Português", "🇵🇹"),
        LanguageItem("ja", "Japonés", "日本語", "🇯🇵"),
        LanguageItem("zh-CN", "Chino", "中文", "🇨🇳"),
        LanguageItem("ko", "Coreano", "한국어", "🇰🇷"),
        LanguageItem("ru", "Ruso", "Русский", "🇷🇺"),
        LanguageItem("ar", "Árabe", "العربية", "🇸🇦"),
        LanguageItem("nl", "Holandés", "Nederlands", "🇳🇱"),
        LanguageItem("pl", "Polaco", "Polski", "🇵🇱"),
        LanguageItem("tr", "Turco", "Türkçe", "🇹🇷"),
        LanguageItem("sv", "Sueco", "Svenska", "🇸🇪"),
        LanguageItem("hi", "Hindi", "हिन्दी", "🇮🇳"),
        LanguageItem("vi", "Vietnamita", "Tiếng Việt", "🇻🇳"),
        LanguageItem("el", "Griego", "Ελληνικά", "🇬🇷"),
        LanguageItem("id", "Indonesio", "Bahasa Indonesia", "🇮🇩"),
        LanguageItem("auto", "Detectar idioma", "Auto", "🌐")
    )

    fun find(code: String?): LanguageItem {
        if (code.isNullOrBlank()) return supported.first { it.code == "en" }
        val clean = code.lowercase().trim().split("-", "_")[0]
        return supported.find { it.code.equals(code, ignoreCase = true) }
            ?: supported.find { it.code.startsWith(clean) }
            ?: LanguageItem(clean, clean.uppercase(), clean.uppercase(), "🌐")
    }

    fun getDisplayName(code: String): String {
        return find(code).name
    }

    fun getFlag(code: String): String {
        return find(code).flagEmoji
    }
}

data class PageTranslationState(
    val isBannerVisible: Boolean = false,
    val sourceLangCode: String = "en",
    val targetLangCode: String = "es",
    val isTranslating: Boolean = false,
    val isTranslated: Boolean = false,
    val originalUrlBeforeTranslate: String? = null
)
