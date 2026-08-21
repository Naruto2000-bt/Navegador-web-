package com.example.model

enum class CookiePolicy(val title: String, val description: String) {
    ALLOW_ALL("Permitir todas", "Acepta cookies propias y de terceros"),
    BLOCK_THIRD_PARTY("Bloquear de terceros", "Recomendado: protege tu privacidad sin romper sitios web"),
    BLOCK_ALL("Bloquear todas", "Modo estricto: ninguna página podrá guardar cookies")
}

data class SiteDataInfo(
    val domain: String,
    val cookieCount: Int,
    val storageBytes: Long,
    val cookiesAllowed: Boolean = true,
    val thirdPartyAllowed: Boolean = false,
    val lastAccessed: Long = System.currentTimeMillis()
) {
    val formattedStorage: String
        get() {
            val kb = storageBytes / 1024.0
            return if (kb < 1024) {
                String.format("%.1f KB", kb)
            } else {
                String.format("%.1f MB", kb / 1024.0)
            }
        }
}

data class StorageBreakdown(
    val cacheBytes: Long,
    val cookiesBytes: Long,
    val siteDataBytes: Long
) {
    val totalBytes: Long get() = cacheBytes + cookiesBytes + siteDataBytes

    val formattedTotal: String
        get() = formatBytes(totalBytes)

    val formattedCache: String
        get() = formatBytes(cacheBytes)

    val formattedCookies: String
        get() = formatBytes(cookiesBytes)

    val formattedSiteData: String
        get() = formatBytes(siteDataBytes)

    private fun formatBytes(bytes: Long): String {
        val mb = bytes / (1024.0 * 1024.0)
        return if (mb < 0.1) {
            String.format("%.1f KB", bytes / 1024.0)
        } else {
            String.format("%.2f MB", mb)
        }
    }
}
