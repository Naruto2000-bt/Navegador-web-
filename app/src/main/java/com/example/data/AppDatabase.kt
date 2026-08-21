package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.BookmarkDao
import com.example.data.dao.CacheClearLogDao
import com.example.data.dao.ExtensionDao
import com.example.data.dao.HistoryDao
import com.example.data.dao.SavedCredentialDao
import com.example.data.dao.SitePermissionDao
import com.example.data.dao.TabDao
import com.example.data.entity.BookmarkEntity
import com.example.data.entity.CacheClearLogEntity
import com.example.data.entity.ExtensionEntity
import com.example.data.entity.HistoryEntity
import com.example.data.entity.SavedCredentialEntity
import com.example.data.entity.SitePermissionEntity
import com.example.data.entity.TabEntity

@Database(
    entities = [
        BookmarkEntity::class,
        HistoryEntity::class,
        ExtensionEntity::class,
        TabEntity::class,
        CacheClearLogEntity::class,
        SitePermissionEntity::class,
        SavedCredentialEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun historyDao(): HistoryDao
    abstract fun extensionDao(): ExtensionDao
    abstract fun tabDao(): TabDao
    abstract fun cacheClearLogDao(): CacheClearLogDao
    abstract fun sitePermissionDao(): SitePermissionDao
    abstract fun savedCredentialDao(): SavedCredentialDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "aura_browser_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
