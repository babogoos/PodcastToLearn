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
import com.fabirt.podcastapp.data.database.model.QuizEntity

/**
 * Created by dion on 2023/05/11.
 */

@Dao
interface ArticlesDao {

    @Query("SELECT * FROM Article WHERE article_id = :articleId")
    suspend fun getArticle(articleId: String): ArticleEntity?

    @Upsert
    suspend fun insertArticle(article: ArticleEntity)

    @Query("SELECT * FROM Paragraph WHERE article_id = :articleId")
    suspend fun getParagraphs(articleId: String): ParagraphEntity?

    @Upsert
    suspend fun insertParagaraphs(paragaraphs: ParagraphEntity): Long

    @Upsert
    suspend fun insertHashtag(hashtag: HashtagEntity): Long

    @Query("SELECT * FROM Hashtag WHERE name = :hashtagName")
    suspend fun getHashtag(hashtagName: String): HashtagEntity?

    @Insert
    suspend fun insertQuiz(quiz: QuizEntity): Long

    @Query("SELECT * FROM Quiz WHERE article_id = :articleId")
    suspend fun getQuizzesByArticle(articleId: String): List<QuizEntity>

    @Query("SELECT * FROM Quiz WHERE paragraph_id = :paragraphId")
    suspend fun getQuizzesByParagraph(paragraphId: String): List<QuizEntity>

    @Insert
    suspend fun insertArticleHashtagCrossRef(articleHashtagCrossRef: ArticleHashtagCrossRef)

    @Transaction
    @Query("SELECT * FROM Hashtag WHERE name = :hashtagName")
    suspend fun getArticlesWithHashtag(hashtagName: String): HashtagWithArticles?

}