package com.fabirt.podcastapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fabirt.podcastapp.data.database.dao.ArticlesDao
import com.fabirt.podcastapp.data.database.model.ArticleEntity
import com.fabirt.podcastapp.data.database.model.ArticleHashtagCrossRef
import com.fabirt.podcastapp.data.database.model.HashtagEntity
import com.fabirt.podcastapp.data.database.model.ParagraphEntity
import com.fabirt.podcastapp.data.database.model.ParagraphsHashtagCrossRef
import com.fabirt.podcastapp.data.database.model.QuizEntity
import com.fabirt.podcastapp.data.database.typeconverter.Converters

/**
 * Created by dion on 2023/05/09.
 */
@Database(
    entities = [
        ArticleEntity::class,
        ParagraphEntity::class,
        HashtagEntity::class,
        ArticleHashtagCrossRef::class,
        ParagraphsHashtagCrossRef::class,
        QuizEntity::class,
    ], version = 1
)
@TypeConverters(Converters::class)
abstract class Pod2LearnDatabase : RoomDatabase() {
    abstract fun articlesDao(): ArticlesDao
}