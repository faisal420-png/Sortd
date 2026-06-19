package com.sortd.launcher.data.local.dao

import androidx.room.*
import com.sortd.launcher.data.local.entity.FavoriteAppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteAppDao {
    @Query("SELECT * FROM favorite_apps ORDER BY position ASC")
    fun getAllFavorites(): Flow<List<FavoriteAppEntity>>

    @Query("SELECT * FROM favorite_apps ORDER BY position ASC")
    suspend fun getAllFavoritesOnce(): List<FavoriteAppEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteAppEntity)

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteAppEntity)

    @Query("DELETE FROM favorite_apps")
    suspend fun deleteAllFavorites()

    @Query("DELETE FROM favorite_apps WHERE packageName = :packageName")
    suspend fun removeFavorite(packageName: String)
}