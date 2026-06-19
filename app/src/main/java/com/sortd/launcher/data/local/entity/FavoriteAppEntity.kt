package com.sortd.launcher.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_apps")
data class FavoriteAppEntity(
    @PrimaryKey
    val packageName: String,
    val position: Int = 0,
    val addedAt: Long = System.currentTimeMillis()
)