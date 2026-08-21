package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "site_permissions")
data class SitePermissionEntity(
    @PrimaryKey
    val domain: String,
    val cookiesAllowed: Boolean = true,
    val thirdPartyAllowed: Boolean = false,
    val customPolicy: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)
