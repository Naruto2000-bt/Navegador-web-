package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "extensions")
data class ExtensionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val identifier: String, // e.g. "adblock_core", "darkmode_oled", "custom_script_1"
    val name: String,
    val description: String,
    val version: String = "1.0.0",
    val author: String = "Aura Engine",
    val iconCategory: String = "shield", // shield, moon, book, video, code, sparkle, translate
    val scriptJs: String,
    val customCss: String = "",
    val isEnabled: Boolean = true,
    val matchUrlPattern: String = "*", // "*" for all sites, or "wikipedia.org", etc.
    val runAt: String = "DOCUMENT_END", // DOCUMENT_START or DOCUMENT_END
    val isBuiltIn: Boolean = false,
    val blockedCount: Int = 0, // telemetry / stats
    val createdAt: Long = System.currentTimeMillis()
)
