package com.dionchang.podcasttolearn

import android.content.Context
import androidx.room.Room
import com.dionchang.podcasttolearn.data.database.Pod2LearnDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

/**
 * Created by dion on 2023/05/12.
 */
@Module
@InstallIn(SingletonComponent::class)
class TestDatabaseModule {
    @Provides
    @Named("test_db")
    fun provideInMemoryDb(@ApplicationContext context: Context) =
        Room.inMemoryDatabaseBuilder(
            context, Pod2LearnDatabase::class.java
        ).allowMainThreadQueries()
            .build()
}