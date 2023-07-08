package com.fabirt.podcastapp.domain.repository

import android.content.Context
import android.net.Uri
import com.fabirt.podcastapp.data.database.dao.ArticlesDao
import com.fabirt.podcastapp.data.database.model.HashtagEntity
import com.fabirt.podcastapp.data.database.model.ParagraphEntity
import com.fabirt.podcastapp.data.database.model.ParagraphsHashtagCrossRef
import com.fabirt.podcastapp.data.database.model.QuizEntity
import com.fabirt.podcastapp.data.datastore.PodcastDataStore
import com.fabirt.podcastapp.data.network.model.ParagraphDto
import com.fabirt.podcastapp.data.network.model.Quiz
import com.fabirt.podcastapp.data.network.model.TranscriptResultDto
import com.fabirt.podcastapp.data.network.model.chat.ChatCompletionRequest
import com.fabirt.podcastapp.data.network.model.chat.ChatMessage
import com.fabirt.podcastapp.data.network.service.OpenAiService
import com.fabirt.podcastapp.data.network.service.PodcastService
import com.fabirt.podcastapp.domain.model.Caption
import com.fabirt.podcastapp.domain.model.DailyWord
import com.fabirt.podcastapp.domain.model.OptionsQuiz
import com.fabirt.podcastapp.domain.model.PodcastCaptions
import com.fabirt.podcastapp.domain.model.Word
import com.fabirt.podcastapp.error.Failure
import com.fabirt.podcastapp.util.Either
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import java.io.File
import javax.inject.Inject

class ArticleRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context,
    private val podcastService: PodcastService,
    private val openAiService: OpenAiService,
    private val dataStore: PodcastDataStore,
    private val articlesDao: ArticlesDao,
) : ArticleRepository {

    // Upload the podcast file to OpenAI and return the captions
    override suspend fun fetchPodcastCaptions(url: String, audioId: String): Either<Failure, PodcastCaptions> {
        try {
            return withContext(Dispatchers.IO) {
                val captions = articlesDao.getCaptionsByArticle(audioId).map {
                    Caption.fromEntity(it)
                }
                if (captions.isNotEmpty()) {
                    println("dion: Transcripting Captions: Captions already exist")
                    return@withContext Either.Right(PodcastCaptions(audioId, captions))
                }


                val audioFile = getPodcatAudioFile(url, audioId)
                println("dion: Transcripting Captions: File Name= ${audioFile.name}, File length= ${audioFile.length()}")
                val filePart = MultipartBody.Part.createFormData(
                    "file",
                    audioFile.name,
                    audioFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                )

                openAiService.audioTranscriptions(file = filePart).let { response ->
                    println("dion: Transcript success")
                    val result = response.body()?.string()!!
                    val podcastCaptions = PodcastCaptions(audioId, TranscriptResultDto(result).asDomainModel())
                    articlesDao.updateArticleContent(audioId, podcastCaptions.captions.joinToString(" ") { it.captionText })
                    articlesDao.inserCaption(podcastCaptions.captions.map { it.asEntity(audioId) })
                    Either.Right(podcastCaptions)
                }
            }

        } catch (e: Exception) {
            println("dion: Error fetching captions, error message: ${e.message}")
            return Either.Left(Failure.UnexpectedFailure)
        }

    }

    private suspend fun getPodcatAudioFile(url: String, audioId: String): File {
        val audioDir = File(context.cacheDir, "audio").also {
            if (!it.exists()) {
                it.mkdir()
            }
        }

        File(audioDir, audioId).also { audioIdDir ->
            return if (audioIdDir.exists()) {
                println("dion: Audio file already exists")
                audioIdDir.listFiles()?.first()!!
            } else {
                downloadAudioFile(url, audioId, audioIdDir)
            }
        }
    }

    private suspend fun downloadAudioFile(url: String, audioId: String, audioIdDir: File): File {
        println("dion: Audio file downloading...")
        val response = podcastService.downloadAudioFile(url)
        val realFileName = (response.raw() as Response).priorResponse?.let { priorResponse ->
            Uri.parse(priorResponse.headers["location"]).buildUpon().clearQuery().build().toString().split("/").last()
        } ?: ("${audioId}.mp3")

        val inputStream = response.body()?.byteStream()

        audioIdDir.mkdir()
        val file = File(audioIdDir, realFileName)
        println("dion: Audio file saving...")
        file.outputStream().use { fileOut ->
            inputStream?.use { input ->
                input.copyTo(fileOut)
            }
        }
        println("dion: Audio file downloaded")
        return file
    }

    override suspend fun parseArticle(articleId: String) {
        try {

            if (articlesDao.getParagraphs(articleId).isNotEmpty()) {
                println("dion: Article already parsed")
                return
            }

            val article = articlesDao.getArticle(articleId)?.orginArticle!!
            val orginDescription = articlesDao.getArticle(articleId)?.orginDescription
            val themes = orginDescription?.trim()?.split("\n") ?: emptyList()
            val systemRole = "You are an english teacher."
            val userPrompt = """
            Please divide the article into ${themes.size} paragraphs according to the following themes, the article is delimited by triple backticks.

            Themes as following:
            ${themes.joinToString("\n") { it }}

            If there are other paragraphs remaining, merge it and create a theme "Misc".
            The article may have greetings and a short intro at first, give it a theme  "Greetings.".
            The article may have closing in the end, give it a theme  "Closing.".

            Output format is a valid JSON Array with JSON Objects contains the following keys:
            theme, paragraphs_index, paragraphs_content, hashtags

            If there is no hashtag or quiz, give it a null. 
            The paragraphs_index is an integer starting with 1.
            The hashtags are paragraphs key words which is format as a ArrayList of String. 
            
            ```
            $article
            ```
            
            The output example as following is delimited by triple backticks:
            ```
                        {
                        "theme": "Theme here.",
                        "paragraph_index": 1,
                        "paragraph_content": "Content here",
                        "hashtags": ["hashtag1", "hashtag2", "hashtag3"],
                      }
            ```
        """.trimIndent()
            val chatCompletionRequest = ChatCompletionRequest(
                messages = listOf(ChatMessage("system", systemRole), ChatMessage("user", userPrompt)),
            )
            val response = openAiService.chatCompletions(chatCompletionRequest).body()

            var result = response?.choices?.first()?.message?.content
            println("dion: result: $result")
            if (result?.startsWith("```json\n") == true) {
                result = result.substringAfter("```json\n").substringBefore("```")
            }
            val paragraphDtoList = Gson().fromJson(result, Array<ParagraphDto>::class.java).toList()
            val captions = articlesDao.getCaptionsByArticle(articleId)
            paragraphDtoList.forEach { paragraphDto ->
                val captionId = captions.find { paragraphDto.paragraphContent.startsWith(it.captionText) }?.id
                val paragraphId = articlesDao.insertParagaraphs(
                    ParagraphEntity(
                        articleId = articleId,
                        theme = paragraphDto.theme,
                        index = paragraphDto.paragraphIndex,
                        content = paragraphDto.paragraphContent,
                        captionId = captionId,
                    )
                )

                paragraphDto.hashtags?.forEach { hashtag ->
                    val hashtagEntity = articlesDao.getHashtag(hashtag)
                    val hashtagId = if (hashtagEntity == null) {
                        articlesDao.insertHashtag(HashtagEntity(name = hashtag))
                    } else {
                        hashtagEntity.hashtagId!!
                    }
                    articlesDao.insertParagraphsHashtagCrossRef(ParagraphsHashtagCrossRef(paragraphId, hashtagId))
                }
            }
        } catch (e: Exception) {
            println("dion: Error parsing article, error: $e")
        }
    }

    override suspend fun gerenateQuiz(articleId: String): Either<Failure, List<OptionsQuiz>> {
        articlesDao.getQuizzesByArticle(articleId).let {
            if (it.isNotEmpty()) {
                println("dion: Quizzes already gerenated")
                return Either.Right(it.map { quizEntity -> quizEntity.asDomainModel() })
            }
        }

        val paragraphs = articlesDao.getParagraphs(articleId)

        try {
            paragraphs.forEach { paragraph ->
                val systemRole = "You are an english teacher."
                val userPrompt = """
            Provide a multiple-choice question that test reader comprehension of following article, the article is delimited by triple backticks.

            Output format is a valid JSON Objects contains the following keys: 
            question, options, answer, reason, quiz_hashtags.
            The quiz_hashtags are question key words and format is a ArrayList of String. 
            The options is a JSON Array contain JSON Object with the following keys: 
            index, value
            The options index will be an alphabet.
            
            ```
            ${paragraph.content}
            ```
            
            The output example as following is delimited by triple backticks:
            ```
                {
                 "question": "Question here", 
                 "options": [
                   {"index": "A", "value": "options 1"},
                   {"index": "B", "value": "options 2"},
                   {"index": "C", "value": "options 3"},
                   {"index": "D", "value": "options 4"}
                 ],
                 "answer": "A",
                 "reason": "Reason here.",
                 "quiz_hashtags": ["hashtag1", "hashtag2", "hashtag3"]
                }
            ```
        """.trimIndent()
                val chatCompletionRequest = ChatCompletionRequest(
                    messages = listOf(ChatMessage("system", systemRole), ChatMessage("user", userPrompt)),
                )
                val response = openAiService.chatCompletions(chatCompletionRequest).body()

                var result = response?.choices?.first()?.message?.content
                println("dion: result: $result")
                if (result?.startsWith("```json\n") == true) {
                    result = result.substringAfter("```json\n").substringBefore("```")
                }
                val quiz = Gson().fromJson(result, Quiz::class.java)
                articlesDao.insertQuiz(
                    // Todo: parse quiz hashtags
                    QuizEntity(
                        articleId = articleId,
                        paragraphId = paragraph.paragraphId!!,
                        question = quiz.question,
                        options = quiz.options.map { "${it.index}. ${it.value}" },
                        correctAnswer = quiz.answer,
                    )
                )
            }

            return Either.Right(articlesDao.getQuizzesByArticle(articleId).map { it.asDomainModel() })
        } catch (e: Exception) {
            println("dion: Error generate quizzes, error: $e")
            return Either.Left(Failure.UnexpectedFailure)
        }
    }

    override suspend fun getDailyWord(audioId: String, article: String): Either<Failure, DailyWord> {
        return try {
            dataStore.readDailyWordResult(audioId)?.let {
                return Either.Right(it)
            }
            val dailyWord = fectchDailyWords(audioId, article)
            dataStore.storeDailyWordResult(dailyWord)
            Either.Right(dailyWord)
        } catch (e: Exception) {
            Either.Left(Failure.UnexpectedFailure)
        }

    }

    override suspend fun getParagraphCaption(paragraphId: Long): Caption? {
        return articlesDao.getParagraph(paragraphId)?.captionId?.let {
            return articlesDao.getCaption(it)?.run { Caption.fromEntity(this) }
        }
    }

    private suspend fun fectchDailyWords(audioId: String, article: String): DailyWord {
        val userPrompt = """
            Present ten English words and phrases with a difficulty of Oxford 5000 from the article, the article is delimited by triple backticks, and find sentences that meet the following conditions from the article:
            1. Contains the word
            2. Include subjects and contexts other than commas.
            Provide them in a JSON Array contain JSON Object with the following keys: 
            [words], [translate], [pos], [sentences], [sentences_translate]
            The key sentences_translate is the sentences translate to zh-TW.
            The key translate is the words translate to zh-TW.
            The key pos stand for part of speech.

            Example:
            [
                {
                    "words": "overtake",
                    "translate": "超越",
                    "pos": "verb",
                    "sentences": "Samsung overtook Apple through a slender 1� lead to secure the top spot in smartphone",
                    "sentences_translate": "三星通過纖細的1�領先超越蘋果，在智慧型手機中獲得頂尖位置"
                }
            ]

            ```
            $article
            ```
        """.trimIndent()
        val systemRole = "You are an english teacher."

        val chatCompletionRequest = ChatCompletionRequest(
            messages = listOf(ChatMessage("system", systemRole), ChatMessage("user", userPrompt)),
        )
        val response = openAiService.chatCompletions(chatCompletionRequest).body()

        var result = response?.choices?.first()?.message?.content
        println("dion: result: $result")
        if (result?.startsWith("```json\n") == true) {
            result = result.substringAfter("```json\n").substringBefore("```")
        }
        val type = object : TypeToken<List<DailyWordDto>>() {}.type
        val wordDataList = Gson().fromJson<List<DailyWordDto>>(result, type)
        val words = wordDataList.map { it.asDomainModel() }
        println("dion: DailyWords: $words")

        return DailyWord(audioId, words)
    }
}

data class DailyWordDto(
    @SerializedName("words") val words: String,
    @SerializedName("translate") val translate: String,
    @SerializedName("pos") val pos: String,
    @SerializedName("sentences") val sentences: String,
    @SerializedName("sentences_translate") val sentencesTranslate: String
) {
    fun asDomainModel(): Word {
        return Word(words, translate, sentences, sentencesTranslate)
    }
}