package com.fabirt.podcastapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fabirt.podcastapp.data.database.dao.ArticlesDao
import com.fabirt.podcastapp.data.database.model.ArticleEntity
import com.fabirt.podcastapp.data.database.model.ArticleHashtagCrossRef
import com.fabirt.podcastapp.data.database.model.HashtagEntity
import com.fabirt.podcastapp.data.database.model.ParagraphEntity

/**
 * Created by dion on 2023/05/09.
 */
@Database(
    entities = [
        ArticleEntity::class,
        ParagraphEntity::class,
        HashtagEntity::class,
        ArticleHashtagCrossRef::class], version = 1
)
abstract class Pod2LearnDatabase : RoomDatabase() {
    abstract fun articlesDao(): ArticlesDao
}