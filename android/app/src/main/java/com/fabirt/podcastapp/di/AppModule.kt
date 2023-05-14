package com.fabirt.podcastapp.di

import android.content.Context
import com.fabirt.podcastapp.data.database.dao.ArticlesDao
import com.fabirt.podcastapp.data.datastore.PodcastDataStore
import com.fabirt.podcastapp.data.exoplayer.PodcastMediaSource
import com.fabirt.podcastapp.data.network.client.ListenNotesAPIClient
import com.fabirt.podcastapp.data.network.client.OpenAiClient
import com.fabirt.podcastapp.data.network.client.RSSReaderClient
import com.fabirt.podcastapp.data.network.service.OpenAiService
import com.fabirt.podcastapp.data.network.service.PodcastService
import com.fabirt.podcastapp.data.service.MediaPlayerServiceConnection
import com.fabirt.podcastapp.domain.repository.ArticleRepository
import com.fabirt.podcastapp.domain.repository.ArticleRepositoryImpl
import com.fabirt.podcastapp.domain.repository.ITunesPodcastRepositoryImpl
import com.fabirt.podcastapp.domain.repository.PodcastRepository
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
        dataStore: PodcastDataStore
    ): PodcastRepository = ITunesPodcastRepositoryImpl(rssReaderClient, dataStore)

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