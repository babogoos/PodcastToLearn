package com.dionchang.podcasttolearn.domain.repository

import com.dionchang.podcasttolearn.domain.model.Caption
import com.dionchang.podcasttolearn.domain.model.DailyWord
import com.dionchang.podcasttolearn.domain.model.OptionsQuiz
import com.dionchang.podcasttolearn.domain.model.PodcastCaptions
import com.dionchang.podcasttolearn.error.Failure
import com.dionchang.podcasttolearn.util.Either

/**
 * Created by dion on 2023/04/29.
 */
interface ArticleRepository {
    suspend fun fetchPodcastCaptions(url: String, audioId: String): Either<Failure, PodcastCaptions>
    suspend fun parseArticle(articleId: String)
    suspend fun getDailyWord(audioId: String, article: String): Either<Failure, DailyWord>
    suspend fun gerenateQuiz(articleId: String): Either<Failure, List<OptionsQuiz>>
    suspend fun getParagraphCaption(paragraphId: Long): Caption?
}