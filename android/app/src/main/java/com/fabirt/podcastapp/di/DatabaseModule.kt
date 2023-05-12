package com.fabirt.podcastapp.di

import android.content.Context
import androidx.room.Room
import com.fabirt.podcastapp.data.database.Pod2LearnDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ServiceComponent::class)
object DatabaseModule {

    //Hilt needs to know how to create an instance of NoteDatabase. For that add another method below provideDao.
    @Provides
    @Singleton
    fun provide(@ApplicationContext context: Context) = Room.databaseBuilder(
        context, Pod2LearnDatabase::class.java, "Pod2LearnDatabase"
    )
        .allowMainThreadQueries()
        .fallbackToDestructiveMigration()
        .build()
}