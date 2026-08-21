package com.example.model

/**
 * Represents a login credential detected in the WebView waiting for user confirmation to save.
 */
data class PendingCredentialSave(
    val domain: String,
    val url: String,
    val username: String,
    val password: String,
    val isUpdate: Boolean = false,
    val existingId: Long? = null
)
