package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.ExtensionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExtensionDao {
    @Query("SELECT * FROM extensions ORDER BY isBuiltIn DESC, createdAt ASC")
    fun getAllExtensions(): Flow<List<ExtensionEntity>>

    @Query("SELECT * FROM extensions WHERE isEnabled = 1")
    fun getEnabledExtensions(): Flow<List<ExtensionEntity>>

    @Query("SELECT * FROM extensions WHERE identifier = :identifier LIMIT 1")
    suspend fun getByIdentifier(identifier: String): ExtensionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExtension(extension: ExtensionEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(extensions: List<ExtensionEntity>)

    @Update
    suspend fun updateExtension(extension: ExtensionEntity)

    @Delete
    suspend fun deleteExtension(extension: ExtensionEntity)

    @Query("DELETE FROM extensions WHERE id = :id AND isBuiltIn = 0")
    suspend fun deleteCustomById(id: Long)

    @Query("UPDATE extensions SET isEnabled = :enabled WHERE id = :id")
    suspend fun toggleEnabled(id: Long, enabled: Boolean)

    @Query("UPDATE extensions SET blockedCount = blockedCount + :count WHERE identifier = :identifier")
    suspend fun incrementBlockedCount(identifier: String, count: Int = 1)
}
