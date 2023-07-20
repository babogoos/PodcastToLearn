package com.dionchang.podcasttolearn

import com.dionchang.podcasttolearn.data.network.model.ParagraphDto
import com.google.gson.Gson
import org.junit.Test

/**
 * Created by dion on 2023/05/16.
 */
class ParagraphTest {
    private val paragraphJson = """
        [
          {
            "theme": "Misc",
            "paragraph_index": 1,
            "paragraph_content": "Coming up, Pinterest partners with Amazon and YouTube Music officially rolls out podcasts., But first, Meta has announced that it's introducing new body shapes, improved hair and clothing, textures to its avatars., The company also announced that more than 1 billion avatars have been created across, its platforms., Starting this month, users will be able to choose from a wider range of body shape options,, including two curvier body shapes., Meta says it's updating some of its existing options to help differentiate them as well., Although Meta has previously allowed you to choose your avatar's body type, the company, says the old options were all quite similar, which is why it's launching new ones., The announcement comes as Meta recently opened up Horizon Worlds to teen users in the US, and Canada, after previously restricting the social VR platform to users 18 years of age, and above.",
            "hashtags": ["Meta", "avatars", "body shapes", "Horizon Worlds", "teen users"],
            "quiz": {
              "question": "What is Meta introducing to its avatars?",
              "options": [
                {"index": "A", "value": "New body shapes, improved hair and clothing, textures"},
                {"index": "B", "value": "New voice recognition technology"},
                {"index": "C", "value": "New gaming features"},
                {"index": "D", "value": "New payment options"}
              ],
              "answer": "A",
              "reason": "Meta is introducing new body shapes, improved hair and clothing, textures to its avatars.",
              "quiz_hashtags": ["Meta", "avatars", "body shapes"]
            }
          },
          {
            "theme": "1. Pinterest partners with Amazon.",
            "paragraph_index": 2,
            "paragraph_content": "Up next, Pinterest has announced a multi-year strategic ad partnership with Amazon, aimed, at bringing more brands and relevant products to its platform., The new deal will make the e-commerce giant Pinterest's first partner on third-party ads,, the company said in a blog post shared alongside its first quarter earnings beat., The partnership is a step in a new direction for the image sharing and social media site,, which has been working to adjust to consumers' changing interests around product discovery, in recent years., As demand for video platforms like TikTok and Reels grew, Pinterest's image pin board, began to feel dated, leading it to launch its video-first idea pins and increase its, investment in creator content., Like other tech companies, Pinterest has been struggling with the macroeconomic forces impacting, its business, but promised it was working to adapt to the changing environment.",
            "hashtags": ["Pinterest", "Amazon", "partnership", "third-party ads", "product discovery"],
            "quiz": {
              "question": "What is the aim of Pinterest's partnership with Amazon?",
              "options": [
                {"index": "A", "value": "To bring more brands and relevant products to its platform"},
                {"index": "B", "value": "To launch a new video-first idea pins feature"},
                {"index": "C", "value": "To invest in creator content"},
                {"index": "D", "value": "To compete with TikTok and Reels"}
              ],
              "answer": "A",
              "reason": "The aim of Pinterest's partnership with Amazon is to bring more brands and relevant products to its platform.",
              "quiz_hashtags": ["Pinterest", "Amazon", "partnership"]
            }
          },
          {
            "theme": "2. YouTube Music officially rolls out podcasts.",
            "paragraph_index": 3,
            "paragraph_content": "And YouTube Music is officially adding podcasts to its platform in the United States on Android,, iOS, and the web., The rollout comes a few months after YouTube podcasting head Kai Chook revealed that podcasts, would be added to YouTube Music soon., The update allows users watching podcasts on the main app to continue listening to them, on YouTube Music., The company notes that all users can listen to podcasts on demand, offline, in the background,, and while casting, and can seamlessly switch between audio-video versions on YouTube Music., Podcasts in YouTube Music will be available regardless of whether you have a YouTube Premium, subscription., YouTube even notes that paying customers may encounter host-read endorsements or sponsorship, messages when listening to podcasts on YouTube Music.",
            "hashtags": ["YouTube Music", "podcasts", "Android", "iOS", "web"],
            "quiz": {
              "question": "What platforms will YouTube Music podcasts be available on?",
              "options": [
                {"index": "A", "value": "Android, iOS, and the web"},
                {"index": "B", "value": "Windows, macOS, and Linux"},
                {"index": "C", "value": "PlayStation, Xbox, and Nintendo Switch"},
                {"index": "D", "value": "Smart TVs and streaming devices"}
              ],
              "answer": "A",
              "reason": "YouTube Music podcasts will be available on Android, iOS, and the web.",
              "quiz_hashtags": ["YouTube Music", "podcasts"]
            }
          },
          {
            "theme": "Misc",
            "paragraph_index": 4,
            "paragraph_content": "Now let's see what's going on in the world of startups., Chief of Professional Network Design for Women in Leadership has cut 14% of staff, or 43, jobs today, saying in an email seen by TechCrunch that the move is a response to the economy, and that the outfit is restructuring to further focus on member experience., Investors continue to pump money into generative AI tech., Case in point, Riplet, an IDE startup developing a code-generating AI-powered tool called Ghostwriter,, this week raised nearly ${'$'}100 million, ${'$'}97.4 million, at ${'$'}1.16 billion post-money valuation., And when Pinecone launched a vector database aimed at data scientists in 2021, it was probably, ahead of its time., But as the use cases began to take shape last year, the company began pushing AI-driven, semantics search., The company announced a ${'$'}100 million Series B investment on a ${'$'}750 million post-valuation.",
            "hashtags": ["startups", "job cuts", "generative AI tech", "Riplet", "Pinecone"],
            "quiz": {
              "question": "What is Riplet?",
              "options": [
                {"index": "A", "value": "An IDE startup"},
                {"index": "B", "value": "A social media platform"},
                {"index": "C", "value": "A video streaming service"},
                {"index": "D", "value": "A food delivery app"}
              ],
              "answer": "A",
              "reason": "Riplet is an IDE startup developing a code-generating AI-powered tool called Ghostwriter.",
              "quiz_hashtags": ["startups", "generative AI tech", "Riplet"]
            }
          }
        ]
    """.trimIndent()

    @Test
    fun `test parse paragraphJson`() {
        val paragraphDto = Gson().fromJson(paragraphJson, Array<ParagraphDto>::class.java).toList()
        println("paragraphDto = $paragraphDto")
    }
}