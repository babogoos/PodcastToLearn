package com.dionchang.podcasttolearn.di

import android.content.Context
import com.dionchang.podcasttolearn.data.database.dao.ArticlesDao
import com.dionchang.podcasttolearn.data.datastore.PodcastDataStore
import com.dionchang.podcasttolearn.data.exoplayer.PodcastMediaSource
import com.dionchang.podcasttolearn.data.network.client.ListenNotesAPIClient
import com.dionchang.podcasttolearn.data.network.client.OpenAiClient
import com.dionchang.podcasttolearn.data.network.client.RSSReaderClient
import com.dionchang.podcasttolearn.data.network.service.OpenAiService
import com.dionchang.podcasttolearn.data.network.service.PodcastService
import com.dionchang.podcasttolearn.data.service.MediaPlayerServiceConnection
import com.dionchang.podcasttolearn.domain.repository.ArticleRepository
import com.dionchang.podcasttolearn.domain.repository.ArticleRepositoryImpl
import com.dionchang.podcasttolearn.domain.repository.ITunesPodcastRepositoryImpl
import com.dionchang.podcasttolearn.domain.repository.PodcastRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideHttpClient(): OkHttpClient = ListenNotesAPIClient.createHttpClient()

    @Provides
    fun provideRSSReaderClient(@ApplicationContext context: Context): RSSReaderClient = RSSReaderClient(context)

    @Provides
    @Singleton
    fun providePodcastService(
        client: OkHttpClient
    ): PodcastService = ListenNotesAPIClient.createPodcastService(client)

    @Provides
    @Singleton
    fun provideOpenAiService(
        client: OkHttpClient
    ): OpenAiService = OpenAiClient.createOpenAPIService(client)

    @Provides
    @Singleton
    fun providePodcastDataStore(
        @ApplicationContext context: Context
    ): PodcastDataStore = PodcastDataStore(context)

    @Provides
    @Singleton
    fun providePodcastRepository(
        rssReaderClient: RSSReaderClient,
        service: PodcastService,
        dataStore: PodcastDataStore,
        articlesDao: ArticlesDao,
    ): PodcastRepository = ITunesPodcastRepositoryImpl(rssReaderClient, dataStore, articlesDao)

    @Provides
    @Singleton
    fun provideArticleRepository(
        @ApplicationContext context: Context,
        podcastService: PodcastService,
        openAiService: OpenAiService,
        dataStore: PodcastDataStore,
        articlesDao: ArticlesDao
    ): ArticleRepository = ArticleRepositoryImpl(context, podcastService, openAiService, dataStore, articlesDao)

    @Provides
    @Singleton
    fun provideMediaPlayerServiceConnection(
        @ApplicationContext context: Context,
        mediaSource: PodcastMediaSource
    ): MediaPlayerServiceConnection = MediaPlayerServiceConnection(context, mediaSource)
}