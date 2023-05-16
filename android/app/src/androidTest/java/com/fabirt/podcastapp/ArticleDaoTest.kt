package com.fabirt.podcastapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.fabirt.podcastapp.data.database.Pod2LearnDatabase
import com.fabirt.podcastapp.data.database.dao.ArticlesDao
import com.fabirt.podcastapp.data.database.model.ArticleEntity
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
                articleId = "123",
                orginArticle = "Article",
                orginDescription = "Description",
                pubDateMS = 123456789,
            )
        )

        articlesDao.insertArticle(
            ArticleEntity(
                articleId = "456",
                orginArticle = "Article_2",
                orginDescription = "Description_2",
                pubDateMS = 123456789,
            )
        )

        assertThat(articlesDao.getArticle("123")?.orginArticle, equalTo("Article"))
        assertThat(articlesDao.getArticle("789")?.orginArticle, nullValue())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertParagraph() = runTest {
        val paragraphId1 = articlesDao.insertParagaraphs(
            ParagraphEntity(
                articleId = "123",
                index = 1,
                theme = "Theme 1",
                content = "Content 1",
            )
        )

        val paragraphId2 = articlesDao.insertParagaraphs(
            ParagraphEntity(
                articleId = "123",
                index = 2,
                theme = "Theme 2",
                content = "Content 2",
            )
        )

        assertThat(paragraphId1, equalTo(1L))
        assertThat(paragraphId2, equalTo(2L))
    }
}