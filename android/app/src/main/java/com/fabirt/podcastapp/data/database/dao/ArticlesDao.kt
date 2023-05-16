package com.fabirt.podcastapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.fabirt.podcastapp.data.database.model.ArticleEntity
import com.fabirt.podcastapp.data.database.model.ArticleHashtagCrossRef
import com.fabirt.podcastapp.data.database.model.CaptionEntity
import com.fabirt.podcastapp.data.database.model.HashtagEntity
import com.fabirt.podcastapp.data.database.model.HashtagWithArticles
import com.fabirt.podcastapp.data.database.model.ParagraphEntity
import com.fabirt.podcastapp.data.database.model.ParagraphsHashtagCrossRef
import com.fabirt.podcastapp.data.database.model.QuizEntity

/**
 * Created by dion on 2023/05/11.
 */

@Dao
interface ArticlesDao {

    @Query("SELECT * FROM Article WHERE article_id = :articleId")
    suspend fun getArticle(articleId: String): ArticleEntity?

    @Insert
    suspend fun insertArticle(article: ArticleEntity)

    @Query("UPDATE Article SET orgin_article = :orginArticle WHERE article_id = :articleId")
    suspend fun updateArticleContent(articleId: String, orginArticle: String)

    @Query("SELECT * FROM Paragraph WHERE article_id = :articleId")
    suspend fun getParagraphs(articleId: String): List<ParagraphEntity>

    @Upsert
    suspend fun insertParagaraphs(paragaraphs: ParagraphEntity): Long

    @Upsert
    suspend fun insertParagaraphs(paragaraphs: List<ParagraphEntity>): List<Long>

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
    suspend fun inserCaption(caption: List<CaptionEntity>): List<Long>

    @Query("SELECT * FROM Caption WHERE article_id = :articleId ORDER BY `index` ASC")
    suspend fun getCaptionsByArticle(articleId: String): List<CaptionEntity>

    @Insert
    suspend fun insertArticleHashtagCrossRef(articleHashtagCrossRef: ArticleHashtagCrossRef)

    @Insert
    suspend fun insertParagraphsHashtagCrossRef(paragraphsHashtagCrossRef: ParagraphsHashtagCrossRef)

    @Transaction
    @Query("SELECT * FROM Hashtag WHERE name = :hashtagName")
    suspend fun getArticlesWithHashtag(hashtagName: String): HashtagWithArticles?

}