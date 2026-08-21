package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.SitePermissionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SitePermissionDao {
    @Query("SELECT * FROM site_permissions ORDER BY updatedAt DESC")
    fun getAllPermissions(): Flow<List<SitePermissionEntity>>

    @Query("SELECT * FROM site_permissions WHERE domain = :domain LIMIT 1")
    suspend fun getPermissionForDomain(domain: String): SitePermissionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(permission: SitePermissionEntity)

    @Query("DELETE FROM site_permissions WHERE domain = :domain")
    suspend fun deletePermission(domain: String)

    @Query("DELETE FROM site_permissions")
    suspend fun clearAllPermissions()
}
