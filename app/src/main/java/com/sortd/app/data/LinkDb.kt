package com.sortd.app.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "folders")
data class Folder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val colorHex: String = "#7C4DFF",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "saved_links",
    foreignKeys = [
        ForeignKey(
            entity = Folder::class,
            parentColumns = ["id"],
            childColumns = ["folderId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("folderId")]
)
data class SavedLink(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val url: String,
    val title: String?,
    val description: String?,
    val imageUrl: String?,
    val folderId: Long? = null,
    val notes: String? = null,
    val isFavorite: Boolean = false,
    val savedAt: Long
)

data class FolderWithCount(
    val id: Long,
    val name: String,
    val colorHex: String,
    val createdAt: Long,
    val itemCount: Int
)

@Dao
interface SavedLinkDao {
    @Query("SELECT * FROM saved_links ORDER BY savedAt DESC")
    fun observeAll(): Flow<List<SavedLink>>

    @Query("SELECT * FROM saved_links WHERE folderId = :folderId ORDER BY savedAt DESC")
    fun observeByFolder(folderId: Long): Flow<List<SavedLink>>

    @Query("SELECT * FROM saved_links WHERE isFavorite = 1 ORDER BY savedAt DESC")
    fun observeFavorites(): Flow<List<SavedLink>>

    @Query(
        """SELECT * FROM saved_links
           WHERE url LIKE '%' || :q || '%'
              OR title LIKE '%' || :q || '%'
              OR description LIKE '%' || :q || '%'
              OR notes LIKE '%' || :q || '%'
           ORDER BY savedAt DESC"""
    )
    fun search(q: String): Flow<List<SavedLink>>

    @Query("SELECT * FROM saved_links WHERE id = :id")
    fun observeOne(id: Long): Flow<SavedLink?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(link: SavedLink): Long

    @Update
    suspend fun update(link: SavedLink)

    @Query("UPDATE saved_links SET isFavorite = :fav WHERE id = :id")
    suspend fun setFavorite(id: Long, fav: Boolean)

    @Query("UPDATE saved_links SET folderId = :folderId WHERE id = :id")
    suspend fun setFolder(id: Long, folderId: Long?)

    @Query("DELETE FROM saved_links WHERE id = :id")
    suspend fun delete(id: Long)
}

@Dao
interface FolderDao {
    @Query("SELECT * FROM folders ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<Folder>>

    @Query(
        """SELECT f.id, f.name, f.colorHex, f.createdAt,
                  (SELECT COUNT(*) FROM saved_links l WHERE l.folderId = f.id) AS itemCount
           FROM folders f
           ORDER BY f.createdAt DESC"""
    )
    fun observeWithCounts(): Flow<List<FolderWithCount>>

    @Query("SELECT * FROM folders WHERE id = :id")
    suspend fun get(id: Long): Folder?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(folder: Folder): Long

    @Update
    suspend fun update(folder: Folder)

    @Query("DELETE FROM folders WHERE id = :id")
    suspend fun delete(id: Long)
}

@Database(
    entities = [SavedLink::class, Folder::class],
    version = 2,
    exportSchema = false
)
abstract class SortdDb : RoomDatabase() {
    abstract fun links(): SavedLinkDao
    abstract fun folders(): FolderDao
}
