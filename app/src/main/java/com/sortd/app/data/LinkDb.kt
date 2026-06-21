package com.sortd.app.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "saved_links")
data class SavedLink(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val url: String,
    val title: String?,
    val imageUrl: String?,
    val savedAt: Long
)

@Dao
interface SavedLinkDao {
    @Query("SELECT * FROM saved_links ORDER BY savedAt DESC")
    fun observeAll(): Flow<List<SavedLink>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(link: SavedLink): Long

    @Query("DELETE FROM saved_links WHERE id = :id")
    suspend fun delete(id: Long)
}

@Database(entities = [SavedLink::class], version = 1, exportSchema = false)
abstract class SortdDb : RoomDatabase() {
    abstract fun links(): SavedLinkDao
}
