package com.fabirt.podcastapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.fabirt.podcastapp.data.database.model.ArticleEntity
import com.fabirt.podcastapp.data.database.model.ArticleHashtagCrossRef
import com.fabirt.podcastapp.data.database.model.HashtagEntity
import com.fabirt.podcastapp.data.database.model.HashtagWithArticles
import com.fabirt.podcastapp.data.database.model.ParagraphEntity

/**
 * Created by dion on 2023/05/11.
 */

@Dao
interface ArticlesDao {

    @Query("SELECT * FROM Article WHERE guid = :guid")
    fun getArticle(guid: String): ArticleEntity?

    @Upsert
    fun insertArticle(article: ArticleEntity)

    @Query("SELECT * FROM Paragraph WHERE guid = :guid")
    fun getParagraphs(guid: String): ParagraphEntity?

    @Upsert
    fun insertParagaraphs(paragaraphs: ParagraphEntity)

    @Upsert
    fun insertHashtag(hashtag: HashtagEntity)

    @Query("SELECT * FROM Hashtag WHERE name = :hashtagName")
    fun getHashtag(hashtagName: String): HashtagEntity?

    @Insert
    fun insertArticleHashtagCrossRef(articleHashtagCrossRef: ArticleHashtagCrossRef)


    @Transaction
    @Query("SELECT * FROM Hashtag WHERE name = :hashtagName")
    fun getArticlesWithHashtag(hashtagName: String): HashtagWithArticles?

}