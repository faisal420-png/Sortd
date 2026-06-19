package com.sortd.launcher.di

import com.sortd.launcher.data.repository.AppRepository
import com.sortd.launcher.data.repository.FavoriteAppRepository
import com.sortd.launcher.data.repository.NoteRepository
import com.sortd.launcher.data.repository.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAppRepository(appRepository: AppRepository): AppRepository {
        return appRepository
    }

    @Provides
    @Singleton
    fun provideFavoriteAppRepository(favoriteAppRepository: FavoriteAppRepository): FavoriteAppRepository {
        return favoriteAppRepository
    }

    @Provides
    @Singleton
    fun provideTaskRepository(taskRepository: TaskRepository): TaskRepository {
        return taskRepository
    }

    @Provides
    @Singleton
    fun provideNoteRepository(noteRepository: NoteRepository): NoteRepository {
        return noteRepository
    }
}