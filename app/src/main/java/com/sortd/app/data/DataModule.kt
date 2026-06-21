package com.sortd.app.data

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides @Singleton
    fun provideDb(@ApplicationContext ctx: Context): SortdDb =
        Room.databaseBuilder(ctx, SortdDb::class.java, "sortd.db").build()

    @Provides @Singleton
    fun provideLinkDao(db: SortdDb): SavedLinkDao = db.links()
}
