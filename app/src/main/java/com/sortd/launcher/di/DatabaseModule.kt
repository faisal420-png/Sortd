package com.sortd.launcher.di

import android.content.Context
import com.sortd.launcher.data.local.SortdDatabase
import com.sortd.launcher.data.local.dao.FavoriteAppDao
import com.sortd.launcher.data.local.dao.NoteDao
import com.sortd.launcher.data.local.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SortdDatabase {
        return SortdDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideTaskDao(database: SortdDatabase): TaskDao {
        return database.taskDao()
    }

    @Provides
    @Singleton
    fun provideNoteDao(database: SortdDatabase): NoteDao {
        return database.noteDao()
    }

    @Provides
    @Singleton
    fun provideFavoriteAppDao(database: SortdDatabase): FavoriteAppDao {
        return database.favoriteAppDao()
    }
}