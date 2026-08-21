package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabs")
data class TabEntity(
    @PrimaryKey
    val tabId: String,
    val title: String = "Nueva pestaña",
    val url: String = "about:blank",
    val faviconUrl: String? = null,
    val isIncognito: Boolean = false,
    val position: Int = 0,
    val lastAccessed: Long = System.currentTimeMillis()
)
