package com.dionchang.podcasttolearn.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dionchang.podcasttolearn.data.database.dao.ArticlesDao
import com.dionchang.podcasttolearn.data.database.model.ArticleEntity
import com.dionchang.podcasttolearn.data.database.model.ArticleHashtagCrossRef
import com.dionchang.podcasttolearn.data.database.model.CaptionEntity
import com.dionchang.podcasttolearn.data.database.model.HashtagEntity
import com.dionchang.podcasttolearn.data.database.model.ParagraphEntity
import com.dionchang.podcasttolearn.data.database.model.ParagraphsHashtagCrossRef
import com.dionchang.podcasttolearn.data.database.model.QuizEntity
import com.dionchang.podcasttolearn.data.database.typeconverter.Converters

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
        CaptionEntity::class,
    ], version = 1
)
@TypeConverters(Converters::class)
abstract class Pod2LearnDatabase : RoomDatabase() {
    abstract fun articlesDao(): ArticlesDao
}