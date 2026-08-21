package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.CacheClearLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CacheClearLogDao {
    @Query("SELECT * FROM cache_clear_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<CacheClearLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: CacheClearLogEntity): Long

    @Query("DELETE FROM cache_clear_logs WHERE id = :id")
    suspend fun deleteLog(id: Long)

    @Query("DELETE FROM cache_clear_logs")
    suspend fun clearAllLogs()
}
