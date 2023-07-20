package com.dionchang.podcasttolearn.di

import android.content.Context
import androidx.room.Room
import com.dionchang.podcasttolearn.data.database.Pod2LearnDatabase
import com.dionchang.podcasttolearn.data.database.dao.ArticlesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun providePod2LearnDatabase(@ApplicationContext context: Context): Pod2LearnDatabase = Room.databaseBuilder(
        context, Pod2LearnDatabase::class.java, "Pod2LearnDatabase.sqlite"
    )
        .allowMainThreadQueries()
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideArticlesDao(
        db: Pod2LearnDatabase
    ): ArticlesDao = db.articlesDao()
}