package com.sortd.launcher.data.repository

import com.sortd.launcher.data.local.dao.FavoriteAppDao
import com.sortd.launcher.data.local.entity.FavoriteAppEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteAppRepository @Inject constructor(
    private val favoriteAppDao: FavoriteAppDao
) {
    fun getAllFavorites(): Flow<List<FavoriteAppEntity>> =
        favoriteAppDao.getAllFavorites()

    suspend fun getAllFavoritesOnce(): List<FavoriteAppEntity> =
        favoriteAppDao.getAllFavoritesOnce()

    suspend fun addFavorite(packageName: String) {
        val existing = favoriteAppDao.getAllFavoritesOnce()
        val position = existing.size
        favoriteAppDao.insertFavorite(
            FavoriteAppEntity(packageName = packageName, position = position)
        )
    }

    suspend fun removeFavorite(packageName: String) {
        favoriteAppDao.removeFavorite(packageName)
    }
}