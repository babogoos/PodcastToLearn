package com.fabirt.podcastapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.fabirt.podcastapp.data.database.Pod2LearnDatabase
import com.fabirt.podcastapp.data.database.dao.ArticlesDao
import com.fabirt.podcastapp.data.database.model.ArticleEntity
import com.fabirt.podcastapp.data.database.model.ArticleHashtagCrossRef
import com.fabirt.podcastapp.data.database.model.HashtagEntity
import com.fabirt.podcastapp.data.database.model.ParagraphEntity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by dion on 2023/05/12.
 */

@HiltAndroidTest
@SmallTest
class ArticleDaoTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var database: Pod2LearnDatabase
    private lateinit var articlesDao: ArticlesDao

    @Before
    fun setup() {
        hiltRule.inject()
        articlesDao = database.articlesDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertArticle() = runTest {
        articlesDao.insertArticle(
            ArticleEntity(
                guid = "123",
                orginArticle = "Article",
                orginDescription = "Description",
                date = "2023/05/12",
            )
        )

        articlesDao.insertArticle(
            ArticleEntity(
                guid = "456",
                orginArticle = "Article_2",
                orginDescription = "Description_2",
                date = "2023/05/13",
            )
        )

        articlesDao.insertParagaraphs(
            ParagraphEntity(
                guid = "123",
                paragraph_index = 1,
                theme = "Theme",
                content = "Content",
            )
        )

        articlesDao.insertHashtag(
            HashtagEntity(hashtagId = 1, name = "Hashtag")
        )

        articlesDao.insertArticleHashtagCrossRef(
            ArticleHashtagCrossRef(
                guid = "123",
                hashtagId = 1,
            )
        )
        articlesDao.insertArticleHashtagCrossRef(
            ArticleHashtagCrossRef(
                guid = "456",
                hashtagId = 1,
            )
        )
        assertThat(articlesDao.getArticle("123")?.orginArticle, equalTo("Article"))
        assertThat(articlesDao.getArticle("789")?.orginArticle, nullValue())
        assertThat(articlesDao.getArticlesWithHashtag("Hashtag")?.articles?.size, equalTo(2))
    }
}