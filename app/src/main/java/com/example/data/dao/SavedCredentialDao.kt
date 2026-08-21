package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.SavedCredentialEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedCredentialDao {
    @Query("SELECT * FROM saved_credentials ORDER BY domain ASC, lastUsedTimestamp DESC")
    fun getAllCredentials(): Flow<List<SavedCredentialEntity>>

    @Query("SELECT * FROM saved_credentials WHERE domain LIKE '%' || :domainQuery || '%' OR url LIKE '%' || :domainQuery || '%' ORDER BY lastUsedTimestamp DESC")
    fun getCredentialsForDomain(domainQuery: String): Flow<List<SavedCredentialEntity>>

    @Query("SELECT * FROM saved_credentials WHERE domain = :domain LIMIT 1")
    suspend fun getCredentialForExactDomain(domain: String): SavedCredentialEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCredential(credential: SavedCredentialEntity): Long

    @Update
    suspend fun updateCredential(credential: SavedCredentialEntity)

    @Delete
    suspend fun deleteCredential(credential: SavedCredentialEntity)

    @Query("DELETE FROM saved_credentials WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM saved_credentials")
    suspend fun clearAll()
}
