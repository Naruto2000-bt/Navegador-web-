package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "cache_clear_logs")
data class CacheClearLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val clearedCookies: Boolean,
    val clearedCache: Boolean,
    val clearedHistory: Boolean,
    val bytesFreed: Long,
    val timeRange: String = "ALL",
    val title: String
) {
    val formattedDate: String
        get() = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date(timestamp))

    val formattedFreed: String
        get() {
            val mb = bytesFreed / (1024.0 * 1024.0)
            return if (mb < 0.1) {
                String.format("%.1f KB", bytesFreed / 1024.0)
            } else {
                String.format("%.2f MB", mb)
            }
        }
}
