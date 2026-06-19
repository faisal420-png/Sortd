package com.sortd.launcher.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sortd.launcher.data.local.dao.FavoriteAppDao
import com.sortd.launcher.data.local.dao.NoteDao
import com.sortd.launcher.data.local.dao.TaskDao
import com.sortd.launcher.data.local.entity.FavoriteAppEntity
import com.sortd.launcher.data.local.entity.NoteEntity
import com.sortd.launcher.data.local.entity.TaskEntity

@Database(
    entities = [TaskEntity::class, NoteEntity::class, FavoriteAppEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SortdDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun noteDao(): NoteDao
    abstract fun favoriteAppDao(): FavoriteAppDao

    companion object {
        @Volatile
        private var INSTANCE: SortdDatabase? = null

        fun getDatabase(context: Context): SortdDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SortdDatabase::class.java,
                    "sortd_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}